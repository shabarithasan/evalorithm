package com.evalorithm.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.databinding.ActivityExamDetailBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.ExamViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExamDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamDetailBinding
    private val viewModel: ExamViewModel by viewModels()

    private var examId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        examId = intent.getLongExtra("EXAM_ID", 0)

        setupToolbar()
        observeExamDetail()

        viewModel.loadExamDetail(examId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Exam Details"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun observeExamDetail() {
        viewModel.examDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    resource.data?.let { exam ->
                        binding.tvExamTitle.text = exam.title
                        binding.tvExamDescription.text = exam.description ?: "No description"
                        binding.tvExamType.text = exam.examType
                        binding.tvExamStatus.text = exam.status
                        binding.tvExamDate.text = "Start: ${exam.startDate}\nEnd: ${exam.endDate}"
                        binding.tvExamDuration.text = "${exam.durationMinutes} minutes"
                        binding.tvExamMarks.text = "Total: ${exam.totalMarks} | Pass: ${exam.passingMarks}"
                        binding.tvQuestionCount.text = "${exam.questionCount} questions"
                        binding.tvStudentCount.text = "${exam.studentCount} students"
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
