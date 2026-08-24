package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.R
import com.evalorithm.data.model.LeaderboardItem
import com.evalorithm.databinding.ActivityAdminAnalyticsBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.AIViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminAnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAnalyticsBinding
    private val viewModel: AIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        loadLeaderboard()
        observeLeaderboard()
    }

    private fun loadLeaderboard() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        viewModel.loadLeaderboard(10)
    }

    private fun observeLeaderboard() {
        viewModel.leaderboard.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    resource.data?.let { displayData(it) }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayData(leaderboard: List<LeaderboardItem>) {
        val totalStudents = leaderboard.size
        val totalExams = leaderboard.sumOf { it.totalExams }
        val avgScore = if (leaderboard.isNotEmpty()) leaderboard.map { it.score }.average() else 0.0

        binding.tvTotalStudents.text = totalStudents.toString()
        binding.tvTotalExams.text = totalExams.toString()
        binding.tvAvgScore.text = String.format("%.1f%%", avgScore)

        val deptData = leaderboard
            .groupBy { it.departmentName ?: "Unknown" }
            .map { (dept, items) -> dept to items.map { it.score }.average().toFloat() }
        if (deptData.isNotEmpty()) {
            binding.barChartDepartments.setData(deptData)
        }

        val growthData = listOf(
            "Jan" to 12f, "Feb" to 18f, "Mar" to 25f,
            "Apr" to 32f, "May" to 40f, "Jun" to 48f
        )
        binding.barChartGrowth.setData(growthData)

        binding.topPerformersContainer.removeAllViews()
        leaderboard.take(5).forEach { item ->
            val itemView = layoutInflater.inflate(R.layout.item_leaderboard, binding.topPerformersContainer, false)
            val tvRank = itemView.findViewById<TextView>(R.id.tvRank)
            val tvName = itemView.findViewById<TextView>(R.id.tvName)
            val tvDept = itemView.findViewById<TextView>(R.id.tvDepartment)
            val tvScore = itemView.findViewById<TextView>(R.id.tvScore)
            tvRank.text = "#${item.rank}"
            tvName.text = item.studentName ?: "Unknown"
            tvDept.text = item.departmentName ?: ""
            tvScore.text = String.format("%.1f", item.score)
            binding.topPerformersContainer.addView(itemView)
        }
    }
}
