package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.data.model.ExamResult
import com.evalorithm.databinding.ItemExamResultBinding

class ExamResultAdapter : ListAdapter<ExamResult, ExamResultAdapter.ResultViewHolder>(ResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemExamResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ResultViewHolder(private val binding: ItemExamResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: ExamResult) {
            binding.tvStudentName.text = result.studentName
            binding.tvExamTitle.text = result.examTitle
            binding.tvMarks.text = "${result.totalMarksObtained.toInt()}/${result.totalMarksPossible}"
            binding.tvPercentage.text = String.format("%.1f%%", result.percentage)
            binding.tvGrade.text = result.grade
            binding.tvCorrect.text = "Correct: ${result.correctAnswers}"
            binding.tvWrong.text = "Wrong: ${result.wrongAnswers}"
            binding.tvSkipped.text = "Skipped: ${result.skippedQuestions}"

            if (result.isPassed) {
                binding.tvPassFail.text = "PASSED"
                binding.tvPassFail.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                binding.tvPassFail.text = "FAILED"
                binding.tvPassFail.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            }
        }
    }

    class ResultDiffCallback : DiffUtil.ItemCallback<ExamResult>() {
        override fun areItemsTheSame(oldItem: ExamResult, newItem: ExamResult) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ExamResult, newItem: ExamResult) = oldItem == newItem
    }
}
