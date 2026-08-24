package com.evalorithm.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.R
import com.evalorithm.databinding.ActivityQuestionDashboardBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.QuestionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionDashboardBinding
    private val viewModel: QuestionViewModel by viewModels()
    private lateinit var recentAdapter: QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Question Dashboard"

        setupRecentQuestions()
        setupButtons()
        observeViewModel()

        viewModel.loadDashboard()
        viewModel.loadQuestions(page = 0, size = 5)
    }

    private fun setupRecentQuestions() {
        recentAdapter = QuestionAdapter(
            onItemClick = { question ->
                val intent = Intent(this, QuestionDetailActivity::class.java)
                intent.putExtra("question_id", question.id)
                startActivity(intent)
            },
            onEditClick = { question ->
                val intent = Intent(this, QuestionCreateActivity::class.java)
                intent.putExtra("question_id", question.id)
                startActivity(intent)
            },
            onDuplicateClick = { question ->
                viewModel.duplicateQuestion(question.id)
            },
            onArchiveClick = { question ->
                viewModel.archiveQuestion(question.id)
            },
            onDeleteClick = { question ->
                viewModel.deleteQuestion(question.id)
            }
        )
        binding.rvRecentQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvRecentQuestions.adapter = recentAdapter
    }

    private fun setupButtons() {
        binding.btnCreateQuestion.setOnClickListener {
            startActivity(Intent(this, QuestionCreateActivity::class.java))
        }

        binding.btnBulkImport.setOnClickListener {
            Toast.makeText(this, "Bulk import coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.dashboard.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    resource.data?.let { data ->
                        binding.tvTotalQuestions.text = data.totalQuestions.toString()
                        binding.tvApprovedQuestions.text = data.approvedQuestions.toString()
                        binding.tvPendingQuestions.text = data.pendingQuestions.toString()
                        binding.tvRejectedQuestions.text = data.rejectedQuestions.toString()
                        binding.tvRecentlyAdded.text = data.recentlyAdded.toString()
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message ?: "Error loading dashboard", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.questions.observe(this) { resource ->
            if (resource is Resource.Success) {
                val questions = resource.data?.content ?: emptyList()
                recentAdapter.submitList(questions.take(5))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboard()
        viewModel.loadQuestions(page = 0, size = 5)
    }
}
