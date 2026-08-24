package com.evalorithm.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.R
import com.evalorithm.data.model.QuestionCategory
import com.google.android.material.card.MaterialCardView

class CategoryAdapter(
    private val onEditClick: (QuestionCategory) -> Unit,
    private val onDeleteClick: (QuestionCategory) -> Unit
) : ListAdapter<QuestionCategory, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardCategory)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvCategoryDescription)
        private val tvQuestionCount: TextView = itemView.findViewById(R.id.tvQuestionCount)
        private val btnMenu: ImageButton = itemView.findViewById(R.id.btnCategoryMenu)

        fun bind(category: QuestionCategory) {
            tvCategoryName.text = category.categoryName
            tvDescription.text = category.description ?: "No description"
            tvQuestionCount.text = category.questionCount.toString()

            btnMenu.setOnClickListener { v ->
                val popup = PopupMenu(v.context, v)
                popup.menuInflater.inflate(R.menu.menu_category_item, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit -> { onEditClick(category); true }
                        R.id.action_delete -> { onDeleteClick(category); true }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<QuestionCategory>() {
        override fun areItemsTheSame(oldItem: QuestionCategory, newItem: QuestionCategory) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: QuestionCategory, newItem: QuestionCategory) = oldItem == newItem
    }
}
