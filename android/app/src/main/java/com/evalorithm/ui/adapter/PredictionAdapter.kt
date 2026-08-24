package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.R
import com.evalorithm.data.model.Prediction
import com.evalorithm.databinding.ItemPredictionBinding

class PredictionAdapter : ListAdapter<Prediction, PredictionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPredictionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPredictionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Prediction) {
            binding.tvSubjectName.text = item.subjectName
            binding.tvPredictedMarks.text = "${item.predictedMarks.toInt()} marks"
            binding.tvPredictedGrade.text = "Grade: ${item.predictedGrade}"

            val prob = (item.passProbability * 100).toInt()
            when {
                prob >= 70 -> binding.circularProgress.setProgress(prob.toFloat(), ContextCompat.getColor(itemView.context, R.color.chart_green), "Pass Prob")
                prob >= 40 -> binding.circularProgress.setProgress(prob.toFloat(), ContextCompat.getColor(itemView.context, R.color.chart_orange), "Pass Prob")
                else -> binding.circularProgress.setProgress(prob.toFloat(), ContextCompat.getColor(itemView.context, R.color.chart_red), "Pass Prob")
            }

            binding.chipRiskLevel.text = item.riskLevel
            when (item.riskLevel.uppercase()) {
                "LOW" -> {
                    binding.chipRiskLevel.setChipBackgroundColorResource(R.color.green_100)
                    binding.chipRiskLevel.setTextColor(ContextCompat.getColor(itemView.context, R.color.green_600))
                }
                "MEDIUM" -> {
                    binding.chipRiskLevel.setChipBackgroundColorResource(R.color.orange_100)
                    binding.chipRiskLevel.setTextColor(ContextCompat.getColor(itemView.context, R.color.orange_600))
                }
                else -> {
                    binding.chipRiskLevel.setChipBackgroundColorResource(R.color.red_100)
                    binding.chipRiskLevel.setTextColor(ContextCompat.getColor(itemView.context, R.color.red_600))
                }
            }

            binding.tvSuggestedImprovement.text = item.suggestedImprovement
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Prediction>() {
        override fun areItemsTheSame(oldItem: Prediction, newItem: Prediction) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Prediction, newItem: Prediction) = oldItem == newItem
    }
}
