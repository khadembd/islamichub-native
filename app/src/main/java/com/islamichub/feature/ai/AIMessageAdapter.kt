package com.islamichub.feature.ai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.islamichub.databinding.ItemAiMessageBinding

class AIMessageAdapter : ListAdapter<AIMessage, AIMessageAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemAiMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemAiMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val msg = getItem(position)
        with(holder.binding) {
            messageText.text = msg.content
            val isUser = msg.role == AIMessage.Role.USER
            // Align: user right, assistant left
            val params = root.layoutParams as ViewGroup.MarginLayoutParams
            if (isUser) {
                root.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#FF6D45C7")
                )
                messageText.setTextColor(android.graphics.Color.WHITE)
                params.setMargins(80, 8, 8, 8)
            } else {
                root.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#FFF7F4F8")
                )
                messageText.setTextColor(android.graphics.Color.parseColor("#FF24212B"))
                params.setMargins(8, 8, 80, 8)
            }
            root.layoutParams = params
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<AIMessage>() {
            override fun areItemsTheSame(a: AIMessage, b: AIMessage) = a.id == b.id
            override fun areContentsTheSame(a: AIMessage, b: AIMessage) = a == b
        }
    }
}
