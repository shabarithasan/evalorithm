package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.R
import com.evalorithm.data.model.QuestionCategory
import com.evalorithm.databinding.ActivityQuestionCategoryBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.QuestionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionCategoryBinding
    private val viewModel: QuestionViewModel by viewModels()
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Question Categories"

        setupRecyclerView()
        setupFab()
        observeViewModel()

        viewModel.loadCategories()
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(
            onEditClick = { category -> showCategoryDialog(category) },
            onDeleteClick = { category ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete \"${category.categoryName}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        deleteCategory(category)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAddCategory.setOnClickListener {
            showCategoryDialog(null)
        }
    }

    private fun showCategoryDialog(existingCategory: QuestionCategory?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_category, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etCategoryName)
        val etDescription = dialogView.findViewById<TextInputEditText>(R.id.etDescription)

        existingCategory?.let {
            etName.setText(it.categoryName)
            etDescription.setText(it.description)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(if (existingCategory != null) "Edit Category" else "Create Category")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text?.toString()?.trim() ?: ""
                val description = etDescription.text?.toString()?.trim() ?: ""

                if (name.isEmpty()) {
                    Toast.makeText(this, "Category name is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val request = mutableMapOf<String, String>()
                request["categoryName"] = name
                if (description.isNotEmpty()) {
                    request["description"] = description
                }

                createCategory(request)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createCategory(request: Map<String, String>) {
        viewModel.createCategory(request)
    }

    private fun deleteCategory(category: QuestionCategory) {
        // API for delete not in repository, show message
        Toast.makeText(this, "Delete functionality coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        viewModel.categories.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvCategories.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val categories = resource.data?.content ?: emptyList()
                    if (categories.isEmpty()) {
                        binding.rvCategories.visibility = View.GONE
                        binding.emptyStateText.visibility = View.VISIBLE
                    } else {
                        binding.rvCategories.visibility = View.VISIBLE
                        binding.emptyStateText.visibility = View.GONE
                        adapter.submitList(categories)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message ?: "Error loading categories", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.categoryCreateState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Category saved successfully", Toast.LENGTH_SHORT).show()
                    viewModel.loadCategories()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message ?: "Error saving category", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
