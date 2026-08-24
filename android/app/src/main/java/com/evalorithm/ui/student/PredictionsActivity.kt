package com.evalorithm.ui.student

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.data.local.TokenManager
import com.evalorithm.databinding.ActivityPredictionsBinding
import com.evalorithm.ui.adapter.PredictionAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.AIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PredictionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPredictionsBinding
    private val viewModel: AIViewModel by viewModels()
    private lateinit var adapter: PredictionAdapter

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = PredictionAdapter()
        binding.rvPredictions.layoutManager = LinearLayoutManager(this)
        binding.rvPredictions.adapter = adapter

        CoroutineScope(Dispatchers.Main).launch {
            val studentId = tokenManager.getUserId().first()
            if (studentId > 0) {
                viewModel.loadPredictions(studentId)
            }
        }

        observePredictions()
    }

    private fun observePredictions() {
        viewModel.predictions.observe(this) { resource ->
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
