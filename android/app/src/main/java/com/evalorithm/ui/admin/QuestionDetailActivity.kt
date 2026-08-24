package com.evalorithm.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.R
import com.evalorithm.data.model.Question
import com.evalorithm.data.model.QuestionVersion
import com.evalorithm.databinding.ActivityQuestionDetailBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.QuestionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionDetailBinding
    private val viewModel: QuestionViewModel by viewModels()
    private var questionId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        questionId = intent.getLongExtra("question_id", -1L)
        if (questionId == -1L) {
            Toast.makeText(this, "Invalid question", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Question Detail"

        setupButtons()
        observeViewModel()

        viewModel.loadQuestion(questionId)
        viewModel.loadVersions(questionId)
    }

    private fun setupButtons() {
        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, QuestionCreateActivity::class.java)
            intent.putExtra("question_id", questionId)
            startActivity(intent)
        }

        binding.btnSubmitReview.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Submit for Review")
                .setMessage("Are you sure you want to submit this question for review?")
                .setPositiveButton("Submit") { _, _ ->
                    viewModel.submitForReview(questionId)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.questionDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    resource.data?.let { question -> displayQuestion(question) }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message ?: "Error loading question", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.versions.observe(this) { resource ->
            if (resource is Resource.Success) {
                val versions = resource.data ?: emptyList()
                if (versions.isNotEmpty()) {
                    displayVersionHistory(versions)
                }
            }
        }
    }

    private fun displayVersionHistory(versions: List<QuestionVersion>) {
        val historyBuilder = StringBuilder()
        historyBuilder.appendLine("Version History:")
        for (version in versions.takeLast(5).reversed()) {
            historyBuilder.appendLine("  v${version.versionNumber} - ${version.updatedByName} (${version.createdAt})")
            if (!version.changeDescription.isNullOrBlank()) {
                historyBuilder.appendLine("    ${version.changeDescription}")
            }
        }
    }

    private fun displayQuestion(question: Question) {
        supportActionBar?.title = question.title

        binding.tvTitle.text = question.title
        binding.tvDescription.text = question.description ?: "No description"

        binding.chipStatus.text = question.status
        when (question.status.lowercase()) {
            "approved" -> {
                binding.chipStatus.setChipBackgroundColorResource(R.color.green_100)
                binding.chipStatus.setTextColor(getColor(R.color.green_600))
            }
            "pending" -> {
                binding.chipStatus.setChipBackgroundColorResource(R.color.orange_100)
                binding.chipStatus.setTextColor(getColor(R.color.orange_600))
            }
            "rejected" -> {
                binding.chipStatus.setChipBackgroundColorResource(R.color.red_100)
                binding.chipStatus.setTextColor(getColor(R.color.red_600))
            }
            "draft" -> {
                binding.chipStatus.setChipBackgroundColorResource(R.color.grey_200)
                binding.chipStatus.setTextColor(getColor(R.color.grey_600))
            }
        }

        binding.chipType.text = question.questionType
        binding.chipDifficulty.text = question.difficulty
        binding.chipBloom.text = question.bloomLevel

        when (question.difficulty.lowercase()) {
            "easy" -> {
                binding.chipDifficulty.setChipBackgroundColorResource(R.color.green_100)
                binding.chipDifficulty.setTextColor(getColor(R.color.green_600))
            }
            "medium" -> {
                binding.chipDifficulty.setChipBackgroundColorResource(R.color.orange_100)
                binding.chipDifficulty.setTextColor(getColor(R.color.orange_600))
            }
            "hard" -> {
                binding.chipDifficulty.setChipBackgroundColorResource(R.color.red_100)
                binding.chipDifficulty.setTextColor(getColor(R.color.red_600))
            }
        }

        binding.tvMarks.text = "Marks: ${question.marks}"
        binding.tvTime.text = "Time: ${question.estimatedTime} min"

        binding.tvDepartment.text = "Department: ${question.departmentName ?: "N/A"}"
        binding.tvSemester.text = "Semester: ${question.semesterNumber ?: "N/A"}"
        binding.tvSubject.text = "Subject: ${question.subjectName ?: "N/A"}"
        binding.tvUnit.text = "Unit: ${question.unitName ?: "N/A"}"
        binding.tvTopic.text = "Topic: ${question.topicName ?: "N/A"}"

        binding.tvExplanation.text = question.explanation ?: "No explanation provided"

        if (question.questionType.equals("MCQ", ignoreCase = true) && !question.mcqOptions.isNullOrEmpty()) {
            binding.cardMCQOptions.visibility = View.VISIBLE
            binding.optionsContainer.removeAllViews()
            val labels = listOf("A", "B", "C", "D", "E", "F")
            question.mcqOptions.forEachIndexed { index, option ->
                val optionView = LayoutInflater.from(this)
                    .inflate(R.layout.item_question_option, binding.optionsContainer, false)
                val tvLabel = optionView.findViewById<TextView>(R.id.tvLabel)
                val tvOptionText = optionView.findViewById<TextView>(R.id.tvOptionText)
                val radioCorrect = optionView.findViewById<RadioButton>(R.id.radioCorrect)

                tvLabel.text = if (index < labels.size) labels[index] else "${index + 1}"
                tvOptionText.text = option.optionText
                radioCorrect.isChecked = option.isCorrect
                radioCorrect.isEnabled = false

                if (option.isCorrect) {
                    tvLabel.setTextColor(getColor(R.color.green_600))
                    tvOptionText.setTextColor(getColor(R.color.green_600))
                }

                binding.optionsContainer.addView(optionView)
            }
        } else {
            binding.cardMCQOptions.visibility = View.GONE
        }

        val stats = question.statistics
        if (stats != null) {
            binding.tvViews.text = "Views: ${stats.viewCount}"
            binding.tvUsage.text = "Used: ${stats.usageCount} times"
            binding.tvCorrectPct.text = "Correct: ${stats.correctPercentage}%"
            binding.tvWrongPct.text = "Wrong: ${stats.wrongPercentage}%"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_question_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_edit -> {
                val intent = Intent(this, QuestionCreateActivity::class.java)
                intent.putExtra("question_id", questionId)
                startActivity(intent)
                true
            }
            R.id.action_duplicate -> {
                viewModel.duplicateQuestion(questionId)
                Toast.makeText(this, "Duplicating question...", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_archive -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Archive Question")
                    .setMessage("Are you sure you want to archive this question?")
                    .setPositiveButton("Archive") { _, _ ->
                        viewModel.archiveQuestion(questionId)
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
            R.id.action_delete -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Question")
                    .setMessage("Are you sure you want to delete this question? This action cannot be undone.")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteQuestion(questionId)
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if (questionId != -1L) {
            viewModel.loadQuestion(questionId)
            viewModel.loadVersions(questionId)
        }
    }
}
