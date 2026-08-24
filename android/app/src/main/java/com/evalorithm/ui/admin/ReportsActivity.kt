package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.evalorithm.R
import com.evalorithm.data.api.ApiInterface
import com.evalorithm.databinding.ActivityReportsBinding
import com.evalorithm.databinding.ItemReportTypeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding

    @Inject
    lateinit var api: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupReportGrid()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reports"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupReportGrid() {
        val reportTypes = listOf(
            Triple("Student Report", "Individual student performance report", R.drawable.ic_student),
            Triple("Faculty Report", "Faculty performance and workload report", R.drawable.ic_monitor),
            Triple("Department Report", "Department-wise analytics report", R.drawable.ic_analytics_chart),
            Triple("Exam Report", "Exam statistics and analysis", R.drawable.ic_quiz),
            Triple("OBE Report", "Outcome-based education report", R.drawable.ic_certificate),
            Triple("Attendance Report", "Student attendance summary", R.drawable.ic_audit)
        )

        binding.gridReportTypes.removeAllViews()
        reportTypes.forEach { (title, description, icon) ->
            val itemBinding = ItemReportTypeBinding.inflate(layoutInflater, binding.gridReportTypes, false)
            itemBinding.tvTitle.text = title
            itemBinding.tvDescription.text = description
            itemBinding.ivIcon.setImageResource(icon)

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            itemBinding.root.layoutParams = params

            itemBinding.root.setOnClickListener {
                showExportDialog(title)
            }

            binding.gridReportTypes.addView(itemBinding.root)
        }
    }

    private fun showExportDialog(reportType: String) {
        val formats = arrayOf("PDF", "Excel (XLSX)", "CSV")
        AlertDialog.Builder(this)
            .setTitle("Export $reportType")
            .setItems(formats) { _, which ->
                val format = formats[which]
                generateReport(reportType, format)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun generateReport(reportType: String, format: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = api.generateReport(
                    mapOf(
                        "reportType" to reportType,
                        "format" to format
                    )
                )
                binding.progressBar.visibility = View.GONE
                if (response.success) {
                    Toast.makeText(this@ReportsActivity, "$reportType generated in $format format", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ReportsActivity, response.message ?: "Failed to generate report", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ReportsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
