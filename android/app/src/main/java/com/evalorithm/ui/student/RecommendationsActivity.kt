package com.evalorithm.ui.student

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.data.local.TokenManager
import com.evalorithm.data.model.Recommendation
import com.evalorithm.databinding.ActivityRecommendationsBinding
import com.evalorithm.ui.adapter.RecommendationAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.AIViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecommendationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendationsBinding
    private val viewModel: AIViewModel by viewModels()
    private lateinit var adapter: RecommendationAdapter

    @Inject
    lateinit var tokenManager: TokenManager

    private var studentId: Long = 0
    private var allRecommendations = listOf<Recommendation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupFilterChips()

        CoroutineScope(Dispatchers.Main).launch {
            studentId = tokenManager.getUserId().first()
            if (studentId > 0) {
                viewModel.loadRecommendations(studentId)
            }
        }

        observeRecommendations()
    }

    private fun setupRecyclerView() {
        adapter = RecommendationAdapter { recommendation ->
            Toast.makeText(this, recommendation.title, Toast.LENGTH_SHORT).show()
        }
        binding.rvRecommendations.layoutManager = LinearLayoutManager(this)
        binding.rvRecommendations.adapter = adapter
    }

    private fun setupFilterChips() {
        val chips = listOf(binding.chipAll, binding.chipCritical, binding.chipHigh, binding.chipMedium, binding.chipLow)
        chips.forEach { chip ->
            chip.setOnClickListener {
                filterRecommendations(chip.text.toString())
            }
        }
    }

    private fun filterRecommendations(priority: String) {
        val filtered = if (priority == "All") {
            allRecommendations
        } else {
            allRecommendations.filter { it.priority.equals(priority, ignoreCase = true) }
        }
        adapter.submitList(filtered)
        binding.layoutEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun observeRecommendations() {
        viewModel.recommendations.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    allRecommendations = resource.data ?: emptyList()
                    adapter.submitList(allRecommendations)
                    binding.layoutEmpty.visibility = if (allRecommendations.isEmpty()) View.VISIBLE else View.GONE
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
