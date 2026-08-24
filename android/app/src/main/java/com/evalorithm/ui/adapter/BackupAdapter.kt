package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.data.model.Backup
import com.evalorithm.databinding.ItemBackupBinding

class BackupAdapter(
    private val onRestoreClick: (Backup) -> Unit
) : ListAdapter<Backup, BackupAdapter.BackupViewHolder>(BackupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        val binding = ItemBackupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BackupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BackupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BackupViewHolder(private val binding: ItemBackupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(backup: Backup) {
            binding.tvFileName.text = backup.fileName
            binding.tvFileSize.text = formatFileSize(backup.fileSize)
            binding.tvBackupType.text = backup.backupType
            binding.tvStatus.text = backup.status
            binding.tvCreatedBy.text = backup.createdByName ?: "System"
            binding.tvCreatedAt.text = backup.createdAt

            when (backup.status.uppercase()) {
                "COMPLETED" -> binding.tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                "FAILED" -> binding.tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                "IN_PROGRESS" -> binding.tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                else -> binding.tvStatus.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
            }

            binding.btnRestore.setOnClickListener { onRestoreClick(backup) }
        }

        private fun formatFileSize(bytes: Long): String {
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            return if (mb >= 1) String.format("%.2f MB", mb) else String.format("%.1f KB", kb)
        }
    }

    class BackupDiffCallback : DiffUtil.ItemCallback<Backup>() {
        override fun areItemsTheSame(oldItem: Backup, newItem: Backup) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Backup, newItem: Backup) = oldItem == newItem
    }
}
