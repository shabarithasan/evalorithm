package com.evalorithm.ui.student

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.R
import com.evalorithm.databinding.ActivityStudentCertificatesBinding
import com.evalorithm.ui.adapter.CertificateAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.OBEViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentCertificatesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentCertificatesBinding
    private val viewModel: OBEViewModel by viewModels()
    private lateinit var certificateAdapter: CertificateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentCertificatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeCertificates()

        val studentId = intent.getLongExtra("student_id", 0L)
        if (studentId > 0) {
            viewModel.loadCertificates(studentId)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Certificates"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        certificateAdapter = CertificateAdapter { certificate ->
            AlertDialog.Builder(this)
                .setTitle("Certificate Verification")
                .setMessage(
                    "Certificate Number: ${certificate.certificateNumber}\n" +
                    "Type: ${certificate.certificateType}\n" +
                    "Student: ${certificate.studentName}\n" +
                    "Issued: ${certificate.issuedDate}\n" +
                    "QR Code: ${certificate.qrCode}"
                )
                .setPositiveButton("OK", null)
                .show()
        }
        binding.rvCertificates.layoutManager = LinearLayoutManager(this)
        binding.rvCertificates.adapter = certificateAdapter
    }

    private fun observeCertificates() {
        viewModel.certificates.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val certificates = resource.data ?: emptyList()
                    if (certificates.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvCertificates.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvCertificates.visibility = View.VISIBLE
                        certificateAdapter.submitList(certificates)
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
