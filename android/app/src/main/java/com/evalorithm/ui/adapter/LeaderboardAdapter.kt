package com.evalorithm.ui.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.R
import com.evalorithm.data.model.LeaderboardItem
import com.evalorithm.databinding.ItemLeaderboardBinding

class LeaderboardAdapter : ListAdapter<LeaderboardItem, LeaderboardAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeaderboardItem) {
            binding.tvName.text = item.studentName ?: item.departmentName ?: "Unknown"
            binding.tvDepartment.text = item.departmentName ?: ""
            binding.tvScore.text = String.format("%.1f", item.score)
            binding.tvAccuracy.text = String.format("%.1f%% accuracy", item.accuracy)
            binding.tvExams.text = "${item.totalExams} exams"

            binding.tvRank.text = "#${item.rank}"

            val bg = binding.tvRank.background as? GradientDrawable
            when (item.rank) {
                1 -> {
                    bg?.setColor(Color.parseColor("#FFD700"))
                    binding.tvRank.setTextColor(Color.parseColor("#7B6200"))
                }
                2 -> {
                    bg?.setColor(Color.parseColor("#C0C0C0"))
                    binding.tvRank.setTextColor(Color.parseColor("#555555"))
                }
                3 -> {
                    bg?.setColor(Color.parseColor("#CD7F32"))
                    binding.tvRank.setTextColor(Color.WHITE)
                }
                else -> {
                    bg?.setColor(Color.parseColor("#EEEEEE"))
                    binding.tvRank.setTextColor(Color.DKGRAY)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<LeaderboardItem>() {
        override fun areItemsTheSame(oldItem: LeaderboardItem, newItem: LeaderboardItem) = oldItem.rank == newItem.rank
        override fun areContentsTheSame(oldItem: LeaderboardItem, newItem: LeaderboardItem) = oldItem == newItem
    }
}
