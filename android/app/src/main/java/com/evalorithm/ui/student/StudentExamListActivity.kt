package com.evalorithm.ui.student

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.data.local.TokenManager
import com.evalorithm.databinding.ActivityStudentExamListBinding
import com.evalorithm.ui.adapter.ExamAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.ExamViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudentExamListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentExamListBinding
    private val viewModel: ExamViewModel by viewModels()
    private lateinit var examAdapter: ExamAdapter

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentExamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFilters()
        observeExams()

        viewModel.loadExams()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Exams"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        examAdapter = ExamAdapter(
            onExamClick = { exam ->
                val intent = Intent(this, ExamResultActivity::class.java)
                intent.putExtra("EXAM_ID", exam.id)
                intent.putExtra("EXAM_TITLE", exam.title)
                startActivity(intent)
            },
            onActionClick = { exam ->
                when (exam.status.uppercase()) {
                    "ACTIVE" -> {
                        val intent = Intent(this, ExamTakingActivity::class.java)
                        intent.putExtra("EXAM_ID", exam.id)
                        intent.putExtra("EXAM_TITLE", exam.title)
                        intent.putExtra("DURATION", exam.durationMinutes)
                        intent.putExtra("TOTAL_QUESTIONS", exam.questionCount)
                        startActivity(intent)
                    }
                    "COMPLETED" -> {
                        val intent = Intent(this, ExamResultActivity::class.java)
                        intent.putExtra("EXAM_ID", exam.id)
                        intent.putExtra("EXAM_TITLE", exam.title)
                        startActivity(intent)
                    }
                }
            }
        )
        binding.rvExams.layoutManager = LinearLayoutManager(this)
        binding.rvExams.adapter = examAdapter
    }

    private fun setupFilters() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = findViewById<Chip>(checkedIds[0])
                val status = when (chip.id) {
                    R.id.chipActive -> "ACTIVE"
                    R.id.chipUpcoming -> "SCHEDULED"
                    R.id.chipCompleted -> "COMPLETED"
                    else -> null
                }
                viewModel.loadExams(status = status)
            }
        }
    }

    private fun observeExams() {
        viewModel.exams.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val exams = resource.data?.content ?: emptyList()
                    if (exams.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvExams.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvExams.visibility = View.VISIBLE
                        examAdapter.submitList(exams)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
