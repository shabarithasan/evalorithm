package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.data.model.Exam
import com.evalorithm.databinding.ItemExamBinding

class ExamAdapter(
    private val onExamClick: (Exam) -> Unit,
    private val onActionClick: (Exam) -> Unit
) : ListAdapter<Exam, ExamAdapter.ExamViewHolder>(ExamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val binding = ItemExamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExamViewHolder(private val binding: ItemExamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exam: Exam) {
            binding.tvExamTitle.text = exam.title
            binding.tvExamType.text = exam.examType
            binding.tvExamStatus.text = exam.status
            binding.tvExamSubject.text = exam.subjectName ?: "General"
            binding.tvExamDate.text = exam.startDate
            binding.tvExamDuration.text = "${exam.durationMinutes} min"
            binding.tvExamMarks.text = "${exam.totalMarks} marks"
            binding.tvQuestionCount.text = "${exam.questionCount} questions"

            when (exam.status.uppercase()) {
                "ACTIVE" -> {
                    binding.tvExamStatus.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                    binding.btnAction.text = "Start Exam"
                    binding.btnAction.isEnabled = true
                }
                "COMPLETED" -> {
                    binding.tvExamStatus.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                    binding.btnAction.text = "View Results"
                    binding.btnAction.isEnabled = true
                }
                "SCHEDULED" -> {
                    binding.tvExamStatus.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                    binding.btnAction.text = "Upcoming"
                    binding.btnAction.isEnabled = false
                }
                else -> {
                    binding.tvExamStatus.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                    binding.btnAction.text = exam.status
                    binding.btnAction.isEnabled = false
                }
            }

            binding.root.setOnClickListener { onExamClick(exam) }
            binding.btnAction.setOnClickListener { onActionClick(exam) }
        }
    }

    class ExamDiffCallback : DiffUtil.ItemCallback<Exam>() {
        override fun areItemsTheSame(oldItem: Exam, newItem: Exam) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Exam, newItem: Exam) = oldItem == newItem
    }
}
