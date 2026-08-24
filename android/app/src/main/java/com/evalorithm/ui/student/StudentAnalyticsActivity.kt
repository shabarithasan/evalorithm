package com.evalorithm.ui.student

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.R
import com.evalorithm.data.local.TokenManager
import com.evalorithm.data.model.StudentAnalytics
import com.evalorithm.databinding.ActivityStudentAnalyticsBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.AIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudentAnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentAnalyticsBinding
    private val viewModel: AIViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    private var studentId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentId = intent.getLongExtra("STUDENT_ID", 0)
        if (studentId == 0L) {
            CoroutineScope(Dispatchers.Main).launch {
                studentId = tokenManager.getUserId().first()
                loadAnalytics()
            }
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnCalculateAnalytics.setOnClickListener {
            if (studentId > 0) {
                viewModel.loadStudentAnalytics(studentId)
            }
        }

        observeAnalytics()

        if (studentId > 0) {
            loadAnalytics()
        }
    }

    private fun loadAnalytics() {
        viewModel.loadStudentAnalytics(studentId)
    }

    private fun observeAnalytics() {
        viewModel.studentAnalytics.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    resource.data?.let { displayAnalytics(it) }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayAnalytics(data: StudentAnalytics) {
        binding.tvTotalAttempted.text = data.totalAttempted.toString()
        binding.tvAccuracy.text = String.format("%.1f%%", data.accuracy)
        binding.tvAverageScore.text = String.format("%.1f", data.averageScore)

        val diffData = data.difficultyPerformance.map { (k, v) -> k to v }.toList()
        if (diffData.isNotEmpty()) {
            binding.barChartDifficulty.setData(diffData)
        }

        val unitData = data.unitPerformance.map { it.unitName to it.accuracy.toFloat() }.toList()
        if (unitData.isNotEmpty()) {
            binding.barChartSubject.setData(unitData)
        }

        binding.unitPerformanceContainer.removeAllViews()
        data.unitPerformance.forEach { item ->
            val itemView = layoutInflater.inflate(R.layout.item_analytics_card, binding.unitPerformanceContainer, false)
            val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            val tvValue = itemView.findViewById<TextView>(R.id.tvValue)
            tvTitle.text = "${item.unitName} (${item.subjectName})"
            tvValue.text = String.format("%.0f%%", item.accuracy)
            binding.unitPerformanceContainer.addView(itemView)
        }

        val trendData = listOf(
            "Week 1" to 65f,
            "Week 2" to 72f,
            "Week 3" to 68f,
            "Week 4" to 80f,
            "Week 5" to 78f,
            "Week 6" to 85f
        )
        binding.lineChartTrend.setData(trendData)
    }
}
