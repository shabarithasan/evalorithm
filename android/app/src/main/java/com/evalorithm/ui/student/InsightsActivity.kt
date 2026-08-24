package com.evalorithm.ui.student

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.data.local.TokenManager
import com.evalorithm.databinding.ActivityInsightsBinding
import com.evalorithm.ui.adapter.InsightAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.AIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InsightsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsightsBinding
    private val viewModel: AIViewModel by viewModels()
    private lateinit var adapter: InsightAdapter

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsightsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = InsightAdapter()
        binding.rvInsights.layoutManager = LinearLayoutManager(this)
        binding.rvInsights.adapter = adapter

        CoroutineScope(Dispatchers.Main).launch {
            val userId = tokenManager.getUserId().first()
            if (userId > 0) {
                viewModel.loadInsights(userId)
            }
        }

        observeInsights()
    }

    private fun observeInsights() {
        viewModel.insights.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = resource.data ?: emptyList()
                    adapter.submitList(data)
                    binding.layoutEmpty.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
