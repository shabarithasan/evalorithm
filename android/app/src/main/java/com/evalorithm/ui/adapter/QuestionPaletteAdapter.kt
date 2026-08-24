package com.evalorithm.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.databinding.ItemQuestionPaletteBinding

class QuestionPaletteAdapter(
    private val onQuestionClick: (Int) -> Unit
) : RecyclerView.Adapter<QuestionPaletteAdapter.PaletteViewHolder>() {

    private var totalQuestions = 0
    private var answers = mutableMapOf<Int, String>()
    private var currentIndex = 0

    fun setTotalQuestions(count: Int) {
        totalQuestions = count
        notifyDataSetChanged()
    }

    fun updateAnswer(index: Int, status: String) {
        answers[index] = status
        notifyItemChanged(index)
    }

    fun setCurrentIndex(index: Int) {
        val oldIndex = currentIndex
        currentIndex = index
        notifyItemChanged(oldIndex)
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        val binding = ItemQuestionPaletteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaletteViewHolder(binding)
    }

    override fun getItemCount() = totalQuestions

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        holder.bind(position, answers[position], position == currentIndex)
    }

    inner class PaletteViewHolder(private val binding: ItemQuestionPaletteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(index: Int, status: String?, isCurrent: Boolean) {
            binding.tvQuestionNumber.text = (index + 1).toString()

            val bgColor = when {
                isCurrent -> Color.parseColor("#1565C0")
                status == "ANSWERED" -> Color.parseColor("#4CAF50")
                status == "NOT_ANSWERED" -> Color.parseColor("#F44336")
                status == "MARKED_REVIEW" -> Color.parseColor("#9C27B0")
                else -> Color.parseColor("#9E9E9E")
            }

            val textColor = if (isCurrent) Color.WHITE else Color.BLACK

            binding.root.setCardBackgroundColor(bgColor)
            binding.tvQuestionNumber.setTextColor(textColor)

            binding.root.setOnClickListener {
                onQuestionClick(index)
            }
        }
    }
}
