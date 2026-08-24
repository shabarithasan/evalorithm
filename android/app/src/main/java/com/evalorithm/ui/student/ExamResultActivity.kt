package com.evalorithm.ui.student

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.R
import com.evalorithm.data.local.TokenManager
import com.evalorithm.databinding.ActivityExamResultBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.ExamViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExamResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamResultBinding
    private val viewModel: ExamViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    private var examId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        examId = intent.getLongExtra("EXAM_ID", 0)
        val examTitle = intent.getStringExtra("EXAM_TITLE") ?: "Exam Result"

        setupToolbar(examTitle)
        observeResult()

        binding.btnReviewAnswers.setOnClickListener {
            Toast.makeText(this, "Answer review coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun observeResult() {
        viewModel.examDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    resource.data?.let { exam ->
                        binding.tvExamTitle.text = exam.title
                        binding.tvMarksObtained.text = "${exam.totalMarks} marks total"
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.loadExamDetail(examId)
    }
}
