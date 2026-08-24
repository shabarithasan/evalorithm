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
import com.evalorithm.data.model.Question
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class QuestionAdapter(
    private val onItemClick: (Question) -> Unit,
    private val onEditClick: (Question) -> Unit,
    private val onDuplicateClick: (Question) -> Unit,
    private val onArchiveClick: (Question) -> Unit,
    private val onDeleteClick: (Question) -> Unit
) : ListAdapter<Question, QuestionAdapter.QuestionViewHolder>(QuestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardQuestion)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvQuestionTitle)
        private val tvSubject: TextView = itemView.findViewById(R.id.tvQuestionSubject)
        private val chipType: Chip = itemView.findViewById(R.id.chipType)
        private val chipDifficulty: Chip = itemView.findViewById(R.id.chipDifficulty)
        private val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
        private val btnMenu: ImageButton = itemView.findViewById(R.id.btnMenu)

        fun bind(question: Question) {
            tvTitle.text = question.title
            tvSubject.text = question.subjectName ?: "No Subject"

            chipType.text = question.questionType
            chipDifficulty.text = question.difficulty
            chipStatus.text = question.status

            when (question.status.lowercase()) {
                "approved" -> {
                    chipStatus.setChipBackgroundColorResource(R.color.green_100)
                    chipStatus.setTextColor(itemView.context.getColor(R.color.green_600))
                }
                "pending" -> {
                    chipStatus.setChipBackgroundColorResource(R.color.orange_100)
                    chipStatus.setTextColor(itemView.context.getColor(R.color.orange_600))
                }
                "rejected" -> {
                    chipStatus.setChipBackgroundColorResource(R.color.red_100)
                    chipStatus.setTextColor(itemView.context.getColor(R.color.red_600))
                }
            }

            when (question.difficulty.lowercase()) {
                "easy" -> {
                    chipDifficulty.setChipBackgroundColorResource(R.color.green_100)
                    chipDifficulty.setTextColor(itemView.context.getColor(R.color.green_600))
                }
                "medium" -> {
                    chipDifficulty.setChipBackgroundColorResource(R.color.orange_100)
                    chipDifficulty.setTextColor(itemView.context.getColor(R.color.orange_600))
                }
                "hard" -> {
                    chipDifficulty.setChipBackgroundColorResource(R.color.red_100)
                    chipDifficulty.setTextColor(itemView.context.getColor(R.color.red_600))
                }
            }

            cardView.setOnClickListener { onItemClick(question) }

            btnMenu.setOnClickListener { v ->
                val popup = PopupMenu(v.context, v)
                popup.menuInflater.inflate(R.menu.menu_question_item, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit -> { onEditClick(question); true }
                        R.id.action_duplicate -> { onDuplicateClick(question); true }
                        R.id.action_archive -> { onArchiveClick(question); true }
                        R.id.action_delete -> { onDeleteClick(question); true }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    class QuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Question, newItem: Question) = oldItem == newItem
    }
}
