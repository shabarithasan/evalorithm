package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.evalorithm.R
import com.evalorithm.databinding.ActivityQuestionCreateBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.QuestionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionCreateBinding
    private val viewModel: QuestionViewModel by viewModels()

    private val questionTypes = arrayOf("MCQ", "TRUE_FALSE", "MATCH_FOLLOWING", "FILL_BLANKS", "ASSERTION_REASON", "DESCRIPTIVE", "CASE_STUDY", "PROGRAMMING")
    private val difficulties = arrayOf("EASY", "MEDIUM", "HARD", "EXPERT")
    private val bloomLevels = arrayOf("K1", "K2", "K3", "K4", "K5", "K6")

    private var editingQuestionId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingQuestionId = intent.getLongExtra("question_id", -1L)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (editingQuestionId != -1L) "Edit Question" else "Create Question"

        setupSpinners()
        setupTypeSpecificContent()
        setupButtons()
        observeViewModel()

        if (editingQuestionId != -1L) {
            viewModel.loadQuestion(editingQuestionId)
            observeEditData()
        }
    }

    private fun setupSpinners() {
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, questionTypes)
        binding.spinnerType.adapter = typeAdapter

        val difficultyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, difficulties)
        binding.spinnerDifficulty.adapter = difficultyAdapter

        val bloomAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloomLevels)
        binding.spinnerBloomLevel.adapter = bloomAdapter

        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateTypeSpecificFields(questionTypes[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTypeSpecificContent() {
        updateTypeSpecificFields(questionTypes[0])
    }

    private fun updateTypeSpecificFields(type: String) {
        binding.typeSpecificContainer.removeAllViews()

        when (type) {
            "MCQ" -> {
                val mcqView = layoutInflater.inflate(R.layout.layout_mcq_options, binding.typeSpecificContainer, false)
                binding.typeSpecificContainer.addView(mcqView)
            }
            "TRUE_FALSE" -> {
                val tfView = layoutInflater.inflate(R.layout.layout_true_false, binding.typeSpecificContainer, false)
                binding.typeSpecificContainer.addView(tfView)
            }
            "FILL_BLANKS" -> {
                val fbView = layoutInflater.inflate(R.layout.layout_fill_blanks, binding.typeSpecificContainer, false)
                binding.typeSpecificContainer.addView(fbView)
            }
            else -> {
                val defaultView = layoutInflater.inflate(R.layout.layout_default_options, binding.typeSpecificContainer, false)
                binding.typeSpecificContainer.addView(defaultView)
            }
        }
    }

    private fun setupButtons() {
        binding.btnSaveDraft.setOnClickListener {
            if (validateForm()) {
                saveQuestion("DRAFT")
            }
        }

        binding.btnSubmitReview.setOnClickListener {
            if (validateForm()) {
                saveQuestion("PENDING")
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.etTitle.text.isNullOrBlank()) {
            binding.tilTitle.error = "Title is required"
            return false
        }
        binding.tilTitle.error = null

        if (binding.etMarks.text.isNullOrBlank()) {
            binding.tilMarks.error = "Marks is required"
            return false
        }
        binding.tilMarks.error = null

        return true
    }

    private fun saveQuestion(status: String) {
        val request = mutableMapOf<String, Any>(
            "title" to (binding.etTitle.text?.toString() ?: ""),
            "questionType" to questionTypes[binding.spinnerType.selectedItemPosition],
            "difficulty" to difficulties[binding.spinnerDifficulty.selectedItemPosition],
            "bloomLevel" to bloomLevels[binding.spinnerBloomLevel.selectedItemPosition],
            "marks" to (binding.etMarks.text?.toString()?.toIntOrNull() ?: 1),
            "estimatedTime" to (binding.etTime.text?.toString()?.toIntOrNull() ?: 10),
            "status" to status
        )

        if (binding.etDescription.text?.isNotBlank() == true) {
            request["description"] = binding.etDescription.text.toString()
        }
        if (binding.etExplanation.text?.isNotBlank() == true) {
            request["explanation"] = binding.etExplanation.text.toString()
        }
        if (binding.etReference.text?.isNotBlank() == true) {
            request["reference"] = binding.etReference.text.toString()
        }
        if (binding.etCourseOutcome.text?.isNotBlank() == true) {
            request["courseOutcome"] = binding.etCourseOutcome.text.toString()
        }
        if (binding.etProgramOutcome.text?.isNotBlank() == true) {
            request["programOutcome"] = binding.etProgramOutcome.text.toString()
        }
        if (binding.etProgramSpecificOutcome.text?.isNotBlank() == true) {
            request["programSpecificOutcome"] = binding.etProgramSpecificOutcome.text.toString()
        }

        val type = questionTypes[binding.spinnerType.selectedItemPosition]
        if (type == "MCQ") {
            val options = collectMCQOptions()
            if (options.isNotEmpty()) {
                request["mcqOptions"] = options
            }
        } else if (type == "TRUE_FALSE") {
            val radioGroup = binding.typeSpecificContainer.findViewById<android.widget.RadioGroup>(R.id.radioGroupTrueFalse)
            if (radioGroup != null) {
                val correctAnswer = when (radioGroup.checkedRadioButtonId) {
                    R.id.radioTrue -> "TRUE"
                    R.id.radioFalse -> "FALSE"
                    else -> "TRUE"
                }
                request["correctAnswer"] = correctAnswer
            }
        }

        viewModel.createQuestion(request)
    }

    private fun collectMCQOptions(): List<Map<String, Any>> {
        val options = mutableListOf<Map<String, Any>>()
        val radioGroup = binding.typeSpecificContainer.findViewById<android.widget.RadioGroup>(R.id.radioGroupOptions)

        val etOptionA = binding.typeSpecificContainer.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etOptionA)
        val etOptionB = binding.typeSpecificContainer.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etOptionB)
        val etOptionC = binding.typeSpecificContainer.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etOptionC)
        val etOptionD = binding.typeSpecificContainer.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etOptionD)

        val optionTexts = listOf(etOptionA, etOptionB, etOptionC, etOptionD)
        val labels = listOf("A", "B", "C", "D")

        optionTexts.forEachIndexed { index, editText ->
            val text = editText?.text?.toString() ?: ""
            if (text.isNotBlank()) {
                options.add(mapOf(
                    "optionLabel" to labels[index],
                    "optionText" to text,
                    "isCorrect" to (radioGroup?.checkedRadioButtonId == getRadioButtonId(index))
                ))
            }
        }
        return options
    }

    private fun getRadioButtonId(index: Int): Int {
        return when (index) {
            0 -> R.id.radioOptionA
            1 -> R.id.radioOptionB
            2 -> R.id.radioOptionC
            3 -> R.id.radioOptionD
            else -> R.id.radioOptionA
        }
    }

    private fun observeViewModel() {
        viewModel.createState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSaveDraft.isEnabled = false
                    binding.btnSubmitReview.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Question saved successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSaveDraft.isEnabled = true
                    binding.btnSubmitReview.isEnabled = true
                    Toast.makeText(this, resource.message ?: "Error saving question", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeEditData() {
        viewModel.questionDetail.observe(this) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { question ->
                    binding.etTitle.setText(question.title)
                    binding.etDescription.setText(question.description)
                    binding.etMarks.setText(question.marks.toString())
                    binding.etTime.setText(question.estimatedTime.toString())
                    binding.etExplanation.setText(question.explanation)
                    binding.etReference.setText(question.reference)
                    binding.etCourseOutcome.setText(question.courseOutcome)
                    binding.etProgramOutcome.setText(question.programOutcome)
                    binding.etProgramSpecificOutcome.setText(question.programSpecificOutcome)

                    val typeIndex = questionTypes.indexOf(question.questionType)
                    if (typeIndex >= 0) binding.spinnerType.setSelection(typeIndex)

                    val diffIndex = difficulties.indexOf(question.difficulty)
                    if (diffIndex >= 0) binding.spinnerDifficulty.setSelection(diffIndex)

                    val bloomIndex = bloomLevels.indexOf(question.bloomLevel)
                    if (bloomIndex >= 0) binding.spinnerBloomLevel.setSelection(bloomIndex)
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
