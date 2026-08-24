package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.databinding.ActivityBackupBinding
import com.evalorithm.ui.adapter.BackupAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.OBEViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackupBinding
    private val viewModel: OBEViewModel by viewModels()
    private lateinit var backupAdapter: BackupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupCreateBackup()
        observeBackups()

        viewModel.loadBackups()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Backup & Recovery"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        backupAdapter = BackupAdapter { backup ->
            AlertDialog.Builder(this)
                .setTitle("Restore Backup")
                .setMessage("Are you sure you want to restore backup '${backup.fileName}'?\n\nThis action cannot be undone.")
                .setPositiveButton("Restore") { _, _ ->
                    restoreBackup(backup.id)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        binding.rvBackups.layoutManager = LinearLayoutManager(this)
        binding.rvBackups.adapter = backupAdapter
    }

    private fun setupCreateBackup() {
        binding.btnCreateBackup.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Create Backup")
                .setMessage("Create a new system backup?")
                .setPositiveButton("Create") { _, _ ->
                    viewModel.createBackup()
                    Toast.makeText(this, "Backup creation started", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun restoreBackup(id: Long) {
        Toast.makeText(this, "Restoring backup...", Toast.LENGTH_SHORT).show()
    }

    private fun observeBackups() {
        viewModel.backups.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val backups = resource.data ?: emptyList()
                    if (backups.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvBackups.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvBackups.visibility = View.VISIBLE
                        backupAdapter.submitList(backups)
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
