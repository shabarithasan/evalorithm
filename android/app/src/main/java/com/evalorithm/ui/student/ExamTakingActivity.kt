package com.evalorithm.ui.student

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.evalorithm.R
import com.evalorithm.data.model.LiveExamQuestion
import com.evalorithm.databinding.ActivityExamTakingBinding
import com.evalorithm.ui.adapter.QuestionPaletteAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.ExamViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExamTakingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamTakingBinding
    private val viewModel: ExamViewModel by viewModels()
    private lateinit var paletteAdapter: QuestionPaletteAdapter

    private var examId: Long = 0
    private var examTitle: String = ""
    private var durationMinutes: Int = 60
    private var totalQuestions: Int = 0
    private var countDownTimer: CountDownTimer? = null
    private var timeRemainingMillis: Long = 0
    private var isFullscreen = false

    private val questionStatuses = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamTakingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        examId = intent.getLongExtra("EXAM_ID", 0)
        examTitle = intent.getStringExtra("EXAM_TITLE") ?: "Exam"
        durationMinutes = intent.getIntExtra("DURATION", 60)
        totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)

        binding.tvExamTitle.text = examTitle

        setupToolbar()
        setupQuestionPalette()
        setupButtons()
        observeLiveExam()
        observeSubmitResult()

        enterFullscreen()
        startExam()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = examTitle
        binding.toolbar.setNavigationOnClickListener { showExitConfirmation() }
    }

    private fun setupQuestionPalette() {
        paletteAdapter = QuestionPaletteAdapter { index ->
            viewModel.loadQuestion(index)
        }
        binding.rvQuestionPalette.layoutManager = GridLayoutManager(this, 5)
        binding.rvQuestionPalette.adapter = paletteAdapter
        paletteAdapter.setTotalQuestions(totalQuestions)
    }

    private fun setupButtons() {
        binding.btnPrevious.setOnClickListener {
            if (viewModel.currentQuestionIndex > 0) {
                saveCurrentAnswer()
                viewModel.loadQuestion(viewModel.currentQuestionIndex - 1)
            }
        }

        binding.btnNext.setOnClickListener {
            if (viewModel.currentQuestionIndex < totalQuestions - 1) {
                saveCurrentAnswer()
                viewModel.loadQuestion(viewModel.currentQuestionIndex + 1)
            }
        }

        binding.btnMarkReview.setOnClickListener {
            val question = (viewModel.liveExam.value as? Resource.Success)?.data
            question?.let {
                questionStatuses[it.questionIndex] = "MARKED_REVIEW"
                paletteAdapter.updateAnswer(it.questionIndex, "MARKED_REVIEW")
            }
        }
    }

    private fun observeLiveExam() {
        viewModel.liveExam.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.scrollViewQuestion.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    binding.scrollViewQuestion.visibility = View.VISIBLE
                    resource.data?.let { question ->
                        displayQuestion(question)
                        if (viewModel.currentAttemptId != null && countDownTimer == null) {
                            startTimer()
                        }
                    }
                }
                is Resource.Error -> {
                    binding.scrollViewQuestion.visibility = View.VISIBLE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeSubmitResult() {
        viewModel.submitResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(this, "Submitting...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    countDownTimer?.cancel()
                    resource.data?.let { result ->
                        Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    }
                    val intent = Intent(this, ExamResultActivity::class.java)
                    intent.putExtra("EXAM_ID", examId)
                    intent.putExtra("EXAM_TITLE", examTitle)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayQuestion(question: LiveExamQuestion) {
        binding.tvQuestionNumber.text = "Question ${question.questionIndex + 1} of ${question.totalQuestions}"
        binding.tvQuestionType.text = question.questionType
        binding.tvQuestionText.text = question.questionTitle
        binding.tvProgress.text = "${question.questionIndex + 1}/${question.totalQuestions}"

        binding.radioOptions.removeAllViews()
        binding.radioOptions.visibility = View.GONE
        binding.etTextAnswer.visibility = View.GONE

        if (question.questionType.uppercase() == "MCQ" && !question.options.isNullOrEmpty()) {
            binding.radioOptions.visibility = View.VISIBLE
            question.options.forEach { option ->
                val radioButton = RadioButton(this).apply {
                    id = View.generateViewId()
                    text = "${option.optionLabel}. ${option.optionText}"
                    textSize = 15f
                    setPadding(16, 12, 16, 12)
                    tag = option.optionLabel
                }
                binding.radioOptions.addView(radioButton)

                if (question.userAnswer?.selectedOptionLabel == option.optionLabel) {
                    radioButton.isChecked = true
                }
            }
        } else {
            binding.etTextAnswer.visibility = View.VISIBLE
            binding.etTextAnswer.setText(question.userAnswer?.textAnswer ?: "")
        }

        paletteAdapter.setCurrentIndex(question.questionIndex)
    }

    private fun saveCurrentAnswer() {
        val question = (viewModel.liveExam.value as? Resource.Success)?.data ?: return
        if (question.questionType.uppercase() == "MCQ") {
            val selectedId = binding.radioOptions.checkedRadioButtonId
            val selectedOption = if (selectedId != -1) {
                val radioButton = findViewById<RadioButton>(selectedId)
                radioButton.tag as? String
            } else null
            viewModel.saveAnswer(question.examQuestionId, selectedOption, null)
            if (selectedOption != null) {
                questionStatuses[question.questionIndex] = "ANSWERED"
                paletteAdapter.updateAnswer(question.questionIndex, "ANSWERED")
            } else {
                questionStatuses[question.questionIndex] = "NOT_ANSWERED"
                paletteAdapter.updateAnswer(question.questionIndex, "NOT_ANSWERED")
            }
        } else {
            val textAnswer = binding.etTextAnswer.text.toString().trim()
            viewModel.saveAnswer(question.examQuestionId, null, textAnswer.ifEmpty { null })
            if (textAnswer.isNotEmpty()) {
                questionStatuses[question.questionIndex] = "ANSWERED"
                paletteAdapter.updateAnswer(question.questionIndex, "ANSWERED")
            } else {
                questionStatuses[question.questionIndex] = "NOT_ANSWERED"
                paletteAdapter.updateAnswer(question.questionIndex, "NOT_ANSWERED")
            }
        }
    }

    private fun startTimer() {
        timeRemainingMillis = durationMinutes * 60L * 1000L
        countDownTimer = object : CountDownTimer(timeRemainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingMillis = millisUntilFinished
                val hours = millisUntilFinished / 3600000
                val minutes = (millisUntilFinished % 3600000) / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.tvTimer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }

            override fun onFinish() {
                Toast.makeText(this@ExamTakingActivity, "Time's up! Submitting exam...", Toast.LENGTH_LONG).show()
                saveCurrentAnswer()
                viewModel.submitExam()
            }
        }.start()
    }

    private fun enterFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        isFullscreen = true
    }

    private fun exitFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
        isFullscreen = false
    }

    private fun toggleFullscreen() {
        if (isFullscreen) exitFullscreen() else enterFullscreen()
    }

    private fun showExitConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Exit Exam?")
            .setMessage("Your progress will be saved. Are you sure you want to exit?")
            .setPositiveButton("Save & Exit") { _, _ ->
                saveCurrentAnswer()
                countDownTimer?.cancel()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSubmitConfirmation() {
        val answered = questionStatuses.values.count { it == "ANSWERED" }
        val unanswered = totalQuestions - answered

        AlertDialog.Builder(this)
            .setTitle("Submit Exam?")
            .setMessage("You have answered $answered out of $totalQuestions questions.\n$unanswered questions will be marked as skipped.\n\nAre you sure you want to submit?")
            .setPositiveButton("Submit") { _, _ ->
                saveCurrentAnswer()
                viewModel.submitExam()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_exam_taking, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_submit -> {
                showSubmitConfirmation()
                true
            }
            R.id.action_fullscreen -> {
                toggleFullscreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Use OnBackPressedCallback")
    override fun onBackPressed() {
        showExitConfirmation()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
