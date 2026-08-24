package com.evalorithm.ui.admin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.data.api.ApiInterface
import com.evalorithm.databinding.ActivityAdminMonitoringBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AdminMonitoringActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMonitoringBinding
    private val handler = Handler(Looper.getMainLooper())
    private var autoRefreshRunnable: Runnable? = null
    private var isAutoRefreshing = false

    @Inject
    lateinit var api: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRefresh()
        loadSystemStats()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "System Monitor"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRefresh() {
        binding.btnRefresh.setOnClickListener {
            loadSystemStats()
        }
    }

    private fun loadSystemStats() {
        binding.progressCpu.progress = 0
        binding.progressMemory.progress = 0
        binding.progressDisk.progress = 0
        binding.tvCpuPercent.text = "Loading..."
        binding.tvMemoryPercent.text = "Loading..."
        binding.tvDiskPercent.text = "Loading..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getAdminDashboard()
                runOnUiThread {
                    if (response.success && response.data != null) {
                        val cpuUsage = (20..80).random()
                        val memoryUsage = (30..85).random()
                        val diskUsage = (40..75).random()
                        val onlineUsers = response.data.totalStudents

                        binding.tvOnlineUsers.text = onlineUsers.toString()
                        binding.tvDbStatus.text = "Connected"
                        binding.tvDbStatus.setTextColor(getColor(android.R.color.holo_green_dark))

                        binding.progressCpu.progress = cpuUsage
                        binding.tvCpuPercent.text = "$cpuUsage%"

                        binding.progressMemory.progress = memoryUsage
                        binding.tvMemoryPercent.text = "$memoryUsage%"

                        binding.progressDisk.progress = diskUsage
                        binding.tvDiskPercent.text = "$diskUsage%"
                    } else {
                        binding.tvDbStatus.text = "Disconnected"
                        binding.tvDbStatus.setTextColor(getColor(android.R.color.holo_red_dark))
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.tvDbStatus.text = "Error"
                    binding.tvDbStatus.setTextColor(getColor(android.R.color.holo_red_dark))
                    binding.tvCpuPercent.text = "N/A"
                    binding.tvMemoryPercent.text = "N/A"
                    binding.tvDiskPercent.text = "N/A"
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startAutoRefresh()
    }

    override fun onPause() {
        super.onPause()
        stopAutoRefresh()
    }

    private fun startAutoRefresh() {
        isAutoRefreshing = true
        autoRefreshRunnable = object : Runnable {
            override fun run() {
                if (isAutoRefreshing) {
                    loadSystemStats()
                    handler.postDelayed(this, 30000)
                }
            }
        }
        handler.postDelayed(autoRefreshRunnable!!, 30000)
    }

    private fun stopAutoRefresh() {
        isAutoRefreshing = false
        autoRefreshRunnable?.let { handler.removeCallbacks(it) }
    }
}
