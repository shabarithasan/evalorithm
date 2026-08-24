package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.R
import com.evalorithm.data.model.Recommendation
import com.evalorithm.databinding.ItemRecommendationBinding

class RecommendationAdapter(
    private val onItemClick: (Recommendation) -> Unit
) : ListAdapter<Recommendation, RecommendationAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Recommendation) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.tvTimestamp.text = item.generatedAt

            binding.chipPriority.text = item.priority
            when (item.priority.uppercase()) {
                "CRITICAL" -> {
                    binding.chipPriority.setChipBackgroundColorResource(R.color.red_100)
                    binding.chipPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.red_600))
                }
                "HIGH" -> {
                    binding.chipPriority.setChipBackgroundColorResource(R.color.orange_100)
                    binding.chipPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.orange_600))
                }
                "MEDIUM" -> {
                    binding.chipPriority.setChipBackgroundColorResource(R.color.blue_100)
                    binding.chipPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue_600))
                }
                else -> {
                    binding.chipPriority.setChipBackgroundColorResource(R.color.green_100)
                    binding.chipPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.green_600))
                }
            }

            if (!item.subjectName.isNullOrEmpty()) {
                binding.chipSubject.text = item.subjectName
                binding.chipSubject.visibility = View.VISIBLE
            } else {
                binding.chipSubject.visibility = View.GONE
            }

            if (!item.topicName.isNullOrEmpty()) {
                binding.chipTopic.text = item.topicName
                binding.chipTopic.visibility = View.VISIBLE
            } else {
                binding.chipTopic.visibility = View.GONE
            }

            when (item.type.uppercase()) {
                "WEAK_AREA" -> binding.ivRecommendIcon.setImageResource(R.drawable.ic_lightbulb)
                "STUDY_PLAN" -> binding.ivRecommendIcon.setImageResource(R.drawable.ic_psychology)
                "PRACTICE" -> binding.ivRecommendIcon.setImageResource(R.drawable.ic_quiz)
                else -> binding.ivRecommendIcon.setImageResource(R.drawable.ic_auto_awesome)
            }

            if (item.isRead) {
                binding.root.alpha = 0.7f
            } else {
                binding.root.alpha = 1.0f
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Recommendation>() {
        override fun areItemsTheSame(oldItem: Recommendation, newItem: Recommendation) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Recommendation, newItem: Recommendation) = oldItem == newItem
    }
}
