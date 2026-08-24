package com.evalorithm.ui.faculty

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.evalorithm.R
import com.evalorithm.databinding.ActivityFacultyDashboardBinding
import com.evalorithm.ui.auth.LoginActivity
import com.evalorithm.ui.common.ProfileActivity
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.DashboardViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FacultyDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFacultyDashboardBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacultyDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawer()
        setupToolbar()
        setupNavigationHeader()
        observeDashboard()
        observeProfile()

        viewModel.loadFacultyDashboard()
        viewModel.loadProfile()
    }

    private fun setupDrawer() {
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    viewModel.loadFacultyDashboard()
                }
                R.id.nav_subjects -> {
                    Toast.makeText(this, "My Subjects", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    viewModel.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Faculty Dashboard"
    }

    private fun setupNavigationHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.tvHeaderName)
        val tvHeaderEmail = headerView.findViewById<TextView>(R.id.tvHeaderEmail)
        viewModel.userName.observe(this) { name ->
            tvHeaderName.text = name ?: "Faculty"
        }
        viewModel.userRole.observe(this) { _ ->
            tvHeaderEmail.text = "Faculty Member"
        }
    }

    private fun observeDashboard() {
        viewModel.facultyDashboard.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    resource.data?.let { data ->
                        binding.tvAssignedSubjects.text = data.assignedSubjects.toString()
                        binding.tvTotalQuestions.text = data.questionCount.toString()
                        binding.tvPendingQuestions.text = data.pendingQuestions.toString()
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeProfile() {
        viewModel.profile.observe(this) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { user ->
                    val headerView = binding.navView.getHeaderView(0)
                    val tvHeaderName = headerView.findViewById<TextView>(R.id.tvHeaderName)
                    tvHeaderName.text = "${user.firstName} ${user.lastName}"
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
}
