package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.data.model.SystemSetting
import com.evalorithm.databinding.ItemSettingBinding

class SettingAdapter(
    private val onSaveClick: (SystemSetting, String) -> Unit
) : ListAdapter<SystemSetting, SettingAdapter.SettingViewHolder>(SettingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val binding = ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SettingViewHolder(private val binding: ItemSettingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(setting: SystemSetting) {
            binding.tvCategory.text = setting.category
            binding.tvSettingKey.text = setting.settingKey
            binding.etSettingValue.setText(setting.settingValue)
            binding.tvDescription.text = setting.description ?: ""

            binding.btnSave.setOnClickListener {
                val newValue = binding.etSettingValue.text.toString().trim()
                onSaveClick(setting, newValue)
            }
        }
    }

    class SettingDiffCallback : DiffUtil.ItemCallback<SystemSetting>() {
        override fun areItemsTheSame(oldItem: SystemSetting, newItem: SystemSetting) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SystemSetting, newItem: SystemSetting) = oldItem == newItem
    }
}
