package com.evalorithm.ui.student

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.R
import com.evalorithm.data.model.AdaptiveQuestion
import com.evalorithm.data.model.AdaptiveSession
import com.evalorithm.data.model.Subject
import com.evalorithm.data.api.ApiInterface
import com.evalorithm.databinding.ActivityAdaptiveTestBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.AIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AdaptiveTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdaptiveTestBinding
    private val viewModel: AIViewModel by viewModels()

    @Inject
    lateinit var api: ApiInterface

    private var subjects = listOf<Subject>()
    private var selectedSubjectId: Long = 0
    private var questionStartTime: Long = 0
    private var currentQuestionId: Long = 0
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdaptiveTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSubjects()
        setupStartScreen()
        setupQuestionScreen()
        setupResultScreen()
        observeSession()
        observeQuestion()
    }

    private fun loadSubjects() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = api.getSubjects(0, 100)
                if (response.success && response.data != null) {
                    subjects = response.data!!.content
                    val names = subjects.map { it.name }
                    val adapter = ArrayAdapter(this@AdaptiveTestActivity, android.R.layout.simple_spinner_dropdown_item, names)
                    binding.spinnerSubject.adapter = adapter
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdaptiveTestActivity, "Failed to load subjects", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupStartScreen() {
        binding.btnStartTest.setOnClickListener {
            if (subjects.isEmpty()) {
                Toast.makeText(this, "No subjects available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val pos = binding.spinnerSubject.selectedItemPosition
            if (pos < 0 || pos >= subjects.size) {
                Toast.makeText(this, "Select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selectedSubjectId = subjects[pos].id
            viewModel.startAdaptiveSession(selectedSubjectId)
        }
    }

    private fun setupQuestionScreen() {
        binding.btnSubmitAnswer.setOnClickListener {
            submitCurrentAnswer()
        }

        binding.btnEndTest.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("End Test?")
                .setMessage("Are you sure you want to end the adaptive test?")
                .setPositiveButton("End") { _, _ ->
                    viewModel.endAdaptiveSession()
                }
                .setNegativeButton("Continue", null)
                .show()
        }
    }

    private fun setupResultScreen() {
        binding.btnBackToDashboard.setOnClickListener {
            finish()
        }
    }

    private fun observeSession() {
        viewModel.adaptiveSession.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val session = resource.data
                    if (session != null && !session.isActive) {
                        showResults(session)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeQuestion() {
        viewModel.adaptiveQuestion.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    resource.data?.let { displayQuestion(it) }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message ?: "No more questions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayQuestion(question: AdaptiveQuestion) {
        binding.startScreen.visibility = View.GONE
        binding.resultScreen.visibility = View.GONE
        binding.questionScreen.visibility = View.VISIBLE

        currentQuestionId = question.questionId
        binding.tvQuestionNumber.text = "Question (Difficulty: ${question.difficulty})"
        binding.tvQuestionText.text = question.questionText
        binding.tvDifficultyBadge.text = question.difficulty.uppercase()

        when (question.difficulty.uppercase()) {
            "EASY" -> binding.tvDifficultyBadge.setBackgroundColor(getColor(R.color.green_600))
            "MEDIUM" -> binding.tvDifficultyBadge.setBackgroundColor(getColor(R.color.orange_600))
            "HARD" -> binding.tvDifficultyBadge.setBackgroundColor(getColor(R.color.red_600))
            else -> binding.tvDifficultyBadge.setBackgroundColor(getColor(R.color.blue_600))
        }

        binding.radioOptions.removeAllViews()
        binding.radioOptions.visibility = View.GONE
        binding.etTextAnswer.visibility = View.GONE

        if (question.questionType.uppercase() == "MCQ" && !question.options.isNullOrEmpty()) {
            binding.radioOptions.visibility = View.VISIBLE
            question.options.forEach { option ->
                val radioButton = RadioButton(this).apply {
                    id = View.generateViewId()
                    text = "${option.label}. ${option.text}"
                    textSize = 15f
                    setPadding(16, 12, 16, 12)
                    tag = option.label
                }
                binding.radioOptions.addView(radioButton)
            }
        } else {
            binding.etTextAnswer.visibility = View.VISIBLE
        }

        questionStartTime = System.currentTimeMillis()
    }

    private fun submitCurrentAnswer() {
        val selectedOption: String? = if (binding.radioOptions.visibility == View.VISIBLE) {
            val selectedId = binding.radioOptions.checkedRadioButtonId
            if (selectedId != -1) {
                val rb = findViewById<RadioButton>(selectedId)
                rb.tag as? String
            } else null
        } else null

        val textAnswer: String? = if (binding.etTextAnswer.visibility == View.VISIBLE) {
            binding.etTextAnswer.text.toString().trim().ifEmpty { null }
        } else null

        if (selectedOption == null && textAnswer == null) {
            Toast.makeText(this, "Please select or type an answer", Toast.LENGTH_SHORT).show()
            return
        }

        val timeTaken = ((System.currentTimeMillis() - questionStartTime) / 1000).toInt()
        binding.radioOptions.clearCheck()
        binding.etTextAnswer.text?.clear()

        viewModel.submitAnswer(currentQuestionId, selectedOption, textAnswer, timeTaken)
    }

    private fun showResults(session: AdaptiveSession) {
        binding.startScreen.visibility = View.GONE
        binding.questionScreen.visibility = View.GONE
        binding.resultScreen.visibility = View.VISIBLE

        binding.tvResultScore.text = String.format("%.1f", session.score)
        binding.tvResultAccuracy.text = String.format("%.1f%%", session.accuracy)
        binding.tvResultQuestions.text = session.questionsAnswered.toString()
        binding.tvStreak.text = "Max Streak: ${session.maxStreak}"
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
