package com.islamichub.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.islamichub.databinding.ItemFeatureBinding

class FeatureAdapter(private val items: List<FeatureItem>) : RecyclerView.Adapter<FeatureAdapter.VH>() {

    inner class VH(val binding: ItemFeatureBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        with(holder.binding) {
            featureIcon.setImageResource(item.iconRes)
            featureTitle.text = item.title
            featureSubtitle.text = item.subtitle
            root.setOnClickListener { item.onClick() }
        }
    }

    override fun getItemCount() = items.size
}
