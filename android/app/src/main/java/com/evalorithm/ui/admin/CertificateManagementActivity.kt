package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.databinding.ActivityCertificateManagementBinding
import com.evalorithm.databinding.DialogGenerateCertificateBinding
import com.evalorithm.ui.adapter.CertificateAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.OBEViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CertificateManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertificateManagementBinding
    private val viewModel: OBEViewModel by viewModels()
    private lateinit var certificateAdapter: CertificateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeCertificates()

        viewModel.loadCertificates(0L)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Certificates"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        certificateAdapter = CertificateAdapter { certificate ->
            AlertDialog.Builder(this)
                .setTitle("Certificate Details")
                .setMessage(
                    "Number: ${certificate.certificateNumber}\n" +
                    "Type: ${certificate.certificateType}\n" +
                    "Student: ${certificate.studentName}\n" +
                    "Reg: ${certificate.registerNumber}\n" +
                    "Issued: ${certificate.issuedDate}\n" +
                    "By: ${certificate.issuedByName ?: "System"}\n" +
                    "QR: ${certificate.qrCode}"
                )
                .setPositiveButton("OK", null)
                .show()
        }
        binding.rvCertificates.layoutManager = LinearLayoutManager(this)
        binding.rvCertificates.adapter = certificateAdapter
    }

    private fun setupFab() {
        binding.fabGenerate.setOnClickListener {
            showGenerateDialog()
        }
    }

    private fun showGenerateDialog() {
        val dialogBinding = DialogGenerateCertificateBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        val types = listOf("Merit Certificate", "Participation Certificate", "Course Completion", "Achievement Certificate")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, types)
        dialogBinding.spinnerType.setAdapter(typeAdapter)
        dialogBinding.spinnerType.setText(types[0], false)

        val students = listOf("Select Student")
        val studentAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, students)
        dialogBinding.spinnerStudent.setAdapter(studentAdapter)

        val subjects = listOf("Select Subject")
        val subjectAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjects)
        dialogBinding.spinnerSubject.setAdapter(subjectAdapter)

        dialogBinding.btnGenerate.setOnClickListener {
            Toast.makeText(this, "Certificate generation initiated", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun observeCertificates() {
        viewModel.certificates.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
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
