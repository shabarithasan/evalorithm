package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.R
import com.evalorithm.data.model.AIInsight
import com.evalorithm.databinding.ItemInsightBinding

class InsightAdapter : ListAdapter<AIInsight, InsightAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInsightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemInsightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AIInsight) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.tvValue.text = String.format("%.1f", item.value)

            when (item.insightType.uppercase()) {
                "PERFORMANCE" -> binding.ivInsightIcon.setImageResource(R.drawable.ic_analytics_chart)
                "TREND" -> binding.ivInsightIcon.setImageResource(R.drawable.ic_trending_up)
                "RECOMMENDATION" -> binding.ivInsightIcon.setImageResource(R.drawable.ic_lightbulb)
                "PREDICTION" -> binding.ivInsightIcon.setImageResource(R.drawable.ic_psychology)
                else -> binding.ivInsightIcon.setImageResource(R.drawable.ic_auto_awesome)
            }

            if (!item.subjectName.isNullOrEmpty()) {
                binding.tvSubject.text = item.subjectName
                binding.tvSubject.visibility = View.VISIBLE
            } else {
                binding.tvSubject.visibility = View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AIInsight>() {
        override fun areItemsTheSame(oldItem: AIInsight, newItem: AIInsight) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AIInsight, newItem: AIInsight) = oldItem == newItem
    }
}
