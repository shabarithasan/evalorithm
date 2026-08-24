package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.data.model.AuditLog
import com.evalorithm.databinding.ItemAuditLogBinding

class AuditLogAdapter : ListAdapter<AuditLog, AuditLogAdapter.AuditLogViewHolder>(AuditLogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditLogViewHolder {
        val binding = ItemAuditLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AuditLogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AuditLogViewHolder(private val binding: ItemAuditLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(log: AuditLog) {
            binding.tvUserName.text = log.userName
            binding.tvAction.text = log.action
            binding.tvEntity.text = log.entityName
            binding.tvDescription.text = log.description
            binding.tvTimestamp.text = log.timestamp
            binding.tvIpAddress.text = log.ipAddress
        }
    }

    class AuditLogDiffCallback : DiffUtil.ItemCallback<AuditLog>() {
        override fun areItemsTheSame(oldItem: AuditLog, newItem: AuditLog) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AuditLog, newItem: AuditLog) = oldItem == newItem
    }
}
