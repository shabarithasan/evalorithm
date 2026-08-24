package com.evalorithm.ui.student

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
import com.evalorithm.databinding.ActivityStudentDashboardBinding
import com.evalorithm.ui.auth.LoginActivity
import com.evalorithm.ui.common.ProfileActivity
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.DashboardViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawer()
        setupToolbar()
        setupNavigationHeader()
        setupAICards()
        observeDashboard()
        observeProfile()

        viewModel.loadStudentDashboard()
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
                    viewModel.loadStudentDashboard()
                }
                R.id.nav_subjects -> {
                    Toast.makeText(this, "My Subjects", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_exams -> {
                    startActivity(Intent(this, StudentExamListActivity::class.java))
                }
                R.id.nav_analytics -> {
                    startActivity(Intent(this, StudentAnalyticsActivity::class.java))
                }
                R.id.nav_adaptive_test -> {
                    startActivity(Intent(this, AdaptiveTestActivity::class.java))
                }
                R.id.nav_recommendations -> {
                    startActivity(Intent(this, RecommendationsActivity::class.java))
                }
                R.id.nav_predictions -> {
                    startActivity(Intent(this, PredictionsActivity::class.java))
                }
                R.id.nav_insights -> {
                    startActivity(Intent(this, InsightsActivity::class.java))
                }
                R.id.nav_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                }
                R.id.nav_certificates -> {
                    startActivity(Intent(this, StudentCertificatesActivity::class.java))
                }
                R.id.nav_feedback -> {
                    startActivity(Intent(this, StudentFeedbackActivity::class.java))
                }
                R.id.nav_help -> {
                    startActivity(Intent(this, StudentHelpActivity::class.java))
                }
                R.id.nav_scan_qr -> {
                    startActivity(Intent(this, com.evalorithm.ui.common.QRScannerActivity::class.java))
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
        supportActionBar?.title = "Student Dashboard"
    }

    private fun setupNavigationHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.tvHeaderName)
        val tvHeaderEmail = headerView.findViewById<TextView>(R.id.tvHeaderEmail)
        viewModel.userName.observe(this) { name ->
            tvHeaderName.text = name ?: "Student"
        }
        viewModel.userRole.observe(this) { _ ->
            tvHeaderEmail.text = "Student"
        }
    }

    private fun setupAICards() {
        binding.cardAnalytics.setOnClickListener {
            startActivity(Intent(this, StudentAnalyticsActivity::class.java))
        }
        binding.cardAdaptiveTest.setOnClickListener {
            startActivity(Intent(this, AdaptiveTestActivity::class.java))
        }
        binding.cardLeaderboard.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }
        binding.cardRecommendations.setOnClickListener {
            startActivity(Intent(this, RecommendationsActivity::class.java))
        }
        binding.cardPredictions.setOnClickListener {
            startActivity(Intent(this, PredictionsActivity::class.java))
        }
        binding.cardInsights.setOnClickListener {
            startActivity(Intent(this, InsightsActivity::class.java))
        }
    }

    private fun observeDashboard() {
        viewModel.studentDashboard.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    resource.data?.let { data ->
                        binding.tvEnrolledSubjects.text = data.enrolledSubjects.toString()
                        binding.tvUpcomingExams.text = data.upcomingExams.toString()
                        binding.tvRecentResults.text = data.recentResults.size.toString()
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
