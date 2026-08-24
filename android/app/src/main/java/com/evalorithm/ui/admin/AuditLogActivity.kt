package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.data.api.ApiInterface
import com.evalorithm.databinding.ActivityAuditLogBinding
import com.evalorithm.ui.adapter.AuditLogAdapter
import com.evalorithm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuditLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuditLogBinding
    private lateinit var auditLogAdapter: AuditLogAdapter

    @Inject
    lateinit var api: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuditLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadAuditLogs()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Audit Logs"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        auditLogAdapter = AuditLogAdapter()
        binding.rvAuditLogs.layoutManager = LinearLayoutManager(this)
        binding.rvAuditLogs.adapter = auditLogAdapter
    }

    private fun loadAuditLogs() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = api.getAuditLogs()
                binding.progressBar.visibility = View.GONE
                if (response.success && response.data != null) {
                    val logs = response.data.content
                    if (logs.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvAuditLogs.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvAuditLogs.visibility = View.VISIBLE
                        auditLogAdapter.submitList(logs)
                    }
                } else {
                    Toast.makeText(this@AuditLogActivity, response.message ?: "Failed to load", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AuditLogActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
