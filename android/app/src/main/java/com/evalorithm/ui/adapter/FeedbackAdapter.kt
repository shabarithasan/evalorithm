package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.data.model.Feedback
import com.evalorithm.databinding.ItemFeedbackBinding

class FeedbackAdapter : ListAdapter<Feedback, FeedbackAdapter.FeedbackViewHolder>(FeedbackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val binding = ItemFeedbackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedbackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedbackViewHolder(private val binding: ItemFeedbackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(feedback: Feedback) {
            binding.tvFeedbackType.text = feedback.feedbackType
            binding.tvFromUser.text = if (feedback.isAnonymous) "Anonymous" else feedback.fromUserName
            binding.tvToUser.text = feedback.toUserName ?: "N/A"
            binding.tvSubject.text = feedback.subjectName ?: ""
            binding.tvComment.text = feedback.comment
            binding.tvDate.text = feedback.createdAt

            val stars = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
            stars.forEachIndexed { index, star ->
                star.visibility = if (index < feedback.rating) View.VISIBLE else View.INVISIBLE
            }

            if (feedback.isAnonymous) {
                binding.badgeAnonymous.visibility = View.VISIBLE
            } else {
                binding.badgeAnonymous.visibility = View.GONE
            }
        }
    }

    class FeedbackDiffCallback : DiffUtil.ItemCallback<Feedback>() {
        override fun areItemsTheSame(oldItem: Feedback, newItem: Feedback) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Feedback, newItem: Feedback) = oldItem == newItem
    }
}
