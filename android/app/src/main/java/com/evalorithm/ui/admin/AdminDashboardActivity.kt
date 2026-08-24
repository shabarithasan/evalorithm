package com.evalorithm.ui.admin

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
import com.evalorithm.databinding.ActivityAdminDashboardBinding
import com.evalorithm.ui.auth.LoginActivity
import com.evalorithm.ui.common.ProfileActivity
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.DashboardViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawer()
        setupToolbar()
        setupNavigationHeader()
        observeDashboard()
        observeProfile()

        viewModel.loadAdminDashboard()
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
                    viewModel.loadAdminDashboard()
                }
                R.id.nav_departments -> {
                    Toast.makeText(this, "Departments", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_subjects -> {
                    Toast.makeText(this, "Subjects", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_faculty -> {
                    Toast.makeText(this, "Faculty", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_students -> {
                    Toast.makeText(this, "Students", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_question_dashboard -> {
                    startActivity(Intent(this, QuestionDashboardActivity::class.java))
                }
                R.id.nav_question_bank -> {
                    startActivity(Intent(this, QuestionBankActivity::class.java))
                }
                R.id.nav_question_categories -> {
                    startActivity(Intent(this, QuestionCategoryActivity::class.java))
                }
                R.id.nav_analytics -> {
                    startActivity(Intent(this, AdminAnalyticsActivity::class.java))
                }
                R.id.nav_obe_assessment -> {
                    startActivity(Intent(this, OBEAssessmentActivity::class.java))
                }
                R.id.nav_certificates -> {
                    startActivity(Intent(this, CertificateManagementActivity::class.java))
                }
                R.id.nav_reports -> {
                    startActivity(Intent(this, ReportsActivity::class.java))
                }
                R.id.nav_audit_logs -> {
                    startActivity(Intent(this, AuditLogActivity::class.java))
                }
                R.id.nav_backup -> {
                    startActivity(Intent(this, BackupActivity::class.java))
                }
                R.id.nav_monitoring -> {
                    startActivity(Intent(this, AdminMonitoringActivity::class.java))
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SystemSettingsActivity::class.java))
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
        supportActionBar?.title = "Admin Dashboard"
    }

    private fun setupNavigationHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.tvHeaderName)
        val tvHeaderEmail = headerView.findViewById<TextView>(R.id.tvHeaderEmail)
        viewModel.userName.observe(this) { name ->
            tvHeaderName.text = name ?: "Admin"
        }
        viewModel.userRole.observe(this) { role ->
            tvHeaderEmail.text = "Administrator"
        }
    }

    private fun observeDashboard() {
        viewModel.adminDashboard.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    resource.data?.let { data ->
                        binding.tvTotalDepartments.text = data.totalDepartments.toString()
                        binding.tvTotalSubjects.text = data.totalSubjects.toString()
                        binding.tvTotalFaculty.text = data.totalFaculty.toString()
                        binding.tvTotalStudents.text = data.totalStudents.toString()
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
