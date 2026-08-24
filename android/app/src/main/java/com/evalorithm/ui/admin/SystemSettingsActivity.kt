package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.data.api.ApiInterface
import com.evalorithm.data.model.SystemSetting
import com.evalorithm.databinding.ActivitySystemSettingsBinding
import com.evalorithm.ui.adapter.SettingAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.OBEViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SystemSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySystemSettingsBinding
    private val viewModel: OBEViewModel by viewModels()
    private lateinit var settingAdapter: SettingAdapter

    @Inject
    lateinit var api: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySystemSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeSettings()

        viewModel.loadSettings()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "System Settings"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        settingAdapter = SettingAdapter { setting, newValue ->
            updateSetting(setting, newValue)
        }
        binding.rvSettings.layoutManager = LinearLayoutManager(this)
        binding.rvSettings.adapter = settingAdapter
    }

    private fun updateSetting(setting: SystemSetting, newValue: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.updateSystemSetting(
                    mapOf(
                        "id" to setting.id.toString(),
                        "settingKey" to setting.settingKey,
                        "settingValue" to newValue
                    )
                )
                runOnUiThread {
                    if (response.success) {
                        Toast.makeText(this@SystemSettingsActivity, "Setting updated", Toast.LENGTH_SHORT).show()
                        viewModel.loadSettings()
                    } else {
                        Toast.makeText(this@SystemSettingsActivity, response.message ?: "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@SystemSettingsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeSettings() {
        viewModel.settings.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val settings = resource.data ?: emptyList()
                    if (settings.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvSettings.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvSettings.visibility = View.VISIBLE
                        settingAdapter.submitList(settings)
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
