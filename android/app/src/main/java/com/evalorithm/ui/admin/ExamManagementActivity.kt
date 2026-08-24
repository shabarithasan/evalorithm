package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.databinding.ActivityExamManagementBinding
import com.evalorithm.ui.adapter.ExamAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.ExamViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExamManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamManagementBinding
    private val viewModel: ExamViewModel by viewModels()
    private lateinit var examAdapter: ExamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeExams()

        viewModel.loadExams()

        binding.fabCreateExam.setOnClickListener {
            Toast.makeText(this, "Create Exam coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Exam Management"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        examAdapter = ExamAdapter(
            onExamClick = { exam ->
                Toast.makeText(this, "Exam: ${exam.title}", Toast.LENGTH_SHORT).show()
            },
            onActionClick = { exam ->
                Toast.makeText(this, "Action: ${exam.title}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvExams.layoutManager = LinearLayoutManager(this)
        binding.rvExams.adapter = examAdapter
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
