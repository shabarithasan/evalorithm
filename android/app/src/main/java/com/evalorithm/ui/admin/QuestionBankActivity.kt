package com.evalorithm.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.R
import com.evalorithm.data.model.Question
import com.evalorithm.databinding.ActivityQuestionBankBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.QuestionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionBankActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionBankBinding
    private val viewModel: QuestionViewModel by viewModels()
    private lateinit var adapter: QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Question Bank"

        setupRecyclerView()
        setupFilters()
        setupFab()
        setupSearch()
        observeViewModel()

        viewModel.loadQuestions()
    }

    private fun setupRecyclerView() {
        adapter = QuestionAdapter(
            onItemClick = { question -> openQuestionDetail(question) },
            onEditClick = { question -> openQuestionDetail(question) },
            onDuplicateClick = { question ->
                viewModel.duplicateQuestion(question.id)
                Toast.makeText(this, "Duplicating question...", Toast.LENGTH_SHORT).show()
            },
            onArchiveClick = { question ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Archive Question")
                    .setMessage("Are you sure you want to archive this question?")
                    .setPositiveButton("Archive") { _, _ ->
                        viewModel.archiveQuestion(question.id)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onDeleteClick = { question ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Question")
                    .setMessage("Are you sure you want to delete this question? This action cannot be undone.")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteQuestion(question.id)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.rvQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvQuestions.adapter = adapter
    }

    private fun setupFilters() {
        binding.chipAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.clearFilters()
                binding.chipGroupFilters.clearCheck()
                binding.chipAll.isChecked = true
            }
        }

        binding.chipMCQ.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectedType = "MCQ"
                viewModel.loadQuestions()
            }
        }

        binding.chipTrueFalse.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectedType = "TRUE_FALSE"
                viewModel.loadQuestions()
            }
        }

        binding.chipShortAnswer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectedType = "SHORT_ANSWER"
                viewModel.loadQuestions()
            }
        }

        binding.chipLongAnswer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectedType = "LONG_ANSWER"
                viewModel.loadQuestions()
            }
        }

        binding.chipEasy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectedDifficulty = "EASY"
                viewModel.loadQuestions()
            }
        }

        binding.chipMedium.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectedDifficulty = "MEDIUM"
                viewModel.loadQuestions()
            }
        }

        binding.chipHard.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectedDifficulty = "HARD"
                viewModel.loadQuestions()
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s?.toString()?.trim()
                viewModel.searchQuery = if (query.isNullOrEmpty()) null else query
                viewModel.loadQuestions()
            }
        })
    }

    private fun setupFab() {
        binding.fabAddQuestion.setOnClickListener {
            startActivity(Intent(this, QuestionCreateActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.questions.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvQuestions.visibility = View.GONE
                    binding.emptyStateLayout.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val questions = resource.data?.content ?: emptyList()
                    if (questions.isEmpty()) {
                        binding.rvQuestions.visibility = View.GONE
                        binding.emptyStateLayout.visibility = View.VISIBLE
                    } else {
                        binding.rvQuestions.visibility = View.VISIBLE
                        binding.emptyStateLayout.visibility = View.GONE
                        adapter.submitList(questions)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    Toast.makeText(this, resource.message ?: "Error loading questions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openQuestionDetail(question: Question) {
        val intent = Intent(this, QuestionDetailActivity::class.java)
        intent.putExtra("question_id", question.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_question_bank, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterDialog() {
        val types = arrayOf("All", "MCQ", "True/False", "Short Answer", "Long Answer")
        val difficulties = arrayOf("All", "Easy", "Medium", "Hard")
        val statuses = arrayOf("All", "Draft", "Pending", "Approved", "Rejected")

        MaterialAlertDialogBuilder(this)
            .setTitle("Filter by Type")
            .setItems(types) { _, which ->
                viewModel.selectedType = if (which == 0) null else types[which]
                viewModel.loadQuestions()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadQuestions()
    }
}
