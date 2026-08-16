package com.islamichub.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.islamichub.databinding.ItemContentCardBinding

/**
 * Generic ContentCard adapter — used by all simple feature list screens.
 * Each item shows: arabic (large), title (bold), subtitle (small), bangla body.
 */
data class ContentCardItem(
    val id: String,
    val title: String,
    val arabic: String = "",
    val subtitle: String = "",
    val body: String = "",
    val reference: String = "",
    val onClick: (() -> Unit)? = null
)

class ContentCardAdapter : ListAdapter<ContentCardItem, ContentCardAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemContentCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemContentCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            cardTitle.text = item.title
            if (item.arabic.isNotBlank()) {
                cardArabic.text = item.arabic
                cardArabic.visibility = android.view.View.VISIBLE
            } else cardArabic.visibility = android.view.View.GONE

            if (item.subtitle.isNotBlank()) {
                cardSubtitle.text = item.subtitle
                cardSubtitle.visibility = android.view.View.VISIBLE
            } else cardSubtitle.visibility = android.view.View.GONE

            if (item.body.isNotBlank()) {
                cardBody.text = item.body
                cardBody.visibility = android.view.View.VISIBLE
            } else cardBody.visibility = android.view.View.GONE

            if (item.reference.isNotBlank()) {
                cardReference.text = item.reference
                cardReference.visibility = android.view.View.VISIBLE
            } else cardReference.visibility = android.view.View.GONE

            root.setOnClickListener { item.onClick?.invoke() }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ContentCardItem>() {
            override fun areItemsTheSame(a: ContentCardItem, b: ContentCardItem) = a.id == b.id
            override fun areContentsTheSame(a: ContentCardItem, b: ContentCardItem) = a == b
        }
    }
}
