package com.evalorithm.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.R
import com.evalorithm.databinding.ActivityRegisterBinding
import com.evalorithm.ui.admin.AdminDashboardActivity
import com.evalorithm.ui.faculty.FacultyDashboardActivity
import com.evalorithm.ui.student.StudentDashboardActivity
import com.evalorithm.util.Constants
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private var selectedRole = Constants.ROLE_STUDENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeRegisterState()
    }

    private fun setupListeners() {
        binding.radioGroupRole.setOnCheckedChangeListener { _, checkedId ->
            selectedRole = when (checkedId) {
                R.id.radioFaculty -> Constants.ROLE_FACULTY
                R.id.radioStudent -> Constants.ROLE_STUDENT
                else -> Constants.ROLE_STUDENT
            }
        }

        binding.btnRegister.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInput(firstName, lastName, email, password, confirmPassword)) {
                viewModel.register(firstName, lastName, email, password, phone, selectedRole)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun observeRegisterState() {
        viewModel.registerState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnRegister.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.btnRegister.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    val role = resource.data?.role
                    navigateToDashboard(role)
                }
                is Resource.Error -> {
                    binding.btnRegister.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToDashboard(role: String?) {
        val intent = when (role) {
            Constants.ROLE_ADMIN -> Intent(this, AdminDashboardActivity::class.java)
            Constants.ROLE_FACULTY -> Intent(this, FacultyDashboardActivity::class.java)
            Constants.ROLE_STUDENT -> Intent(this, StudentDashboardActivity::class.java)
            else -> Intent(this, AdminDashboardActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun validateInput(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (firstName.isEmpty()) {
            binding.tilFirstName.error = "First name is required"
            return false
        }
        if (lastName.isEmpty()) {
            binding.tilLastName.error = "Last name is required"
            return false
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            return false
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            return false
        }
        binding.tilFirstName.error = null
        binding.tilLastName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        return true
    }
}
