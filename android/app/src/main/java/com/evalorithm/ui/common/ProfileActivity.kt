package com.evalorithm.ui.common

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.databinding.ActivityProfileBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"

        binding.toolbar.setNavigationOnClickListener { finish() }

        observeProfile()
        viewModel.loadProfile()

        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Edit profile coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Change password coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeProfile() {
        viewModel.profile.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    resource.data?.let { user ->
                        binding.tvName.text = "${user.firstName} ${user.lastName}"
                        binding.tvEmail.text = user.email
                        binding.tvPhone.text = user.phone ?: "Not provided"
                        binding.tvRole.text = user.role
                        binding.tvStatus.text = if (user.enabled) "Active" else "Inactive"
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
