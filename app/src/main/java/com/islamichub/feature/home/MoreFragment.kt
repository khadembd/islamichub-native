package com.islamichub.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.islamichub.R
import com.islamichub.databinding.FragmentMoreBinding
import com.islamichub.databinding.ItemMoreMenuBinding

class MoreFragment : Fragment() {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = listOf(
            MoreItem(R.drawable.ic_search,    "খুঁজুন")          { goto(R.id.search_dest) },
            MoreItem(R.drawable.ic_bookmark,  "বুকমার্ক")        { goto(R.id.bookmarks_dest) },
            MoreItem(R.drawable.ic_book,      "হাদিস")          { goto(R.id.hadith_dest) },
            MoreItem(R.drawable.ic_dua,       "দোয়া")           { goto(R.id.dua_dest) },
            MoreItem(R.drawable.ic_zikr,      "জিকির / তসবিহ") { goto(R.id.zikr_dest) },
            MoreItem(R.drawable.ic_star,      "আসমাউল হুসনা") { goto(R.id.asmaul_husna_dest) },
            MoreItem(R.drawable.ic_story,     "ইসলামিক গল্প") { goto(R.id.stories_dest) },
            MoreItem(R.drawable.ic_info,      "ভুল ধারণা")      { goto(R.id.misconceptions_dest) },
            MoreItem(R.drawable.ic_help,      "প্রশ্নোত্তর")    { goto(R.id.questions_dest) },
            MoreItem(R.drawable.ic_settings,  "সেটিংস")         { goto(R.id.settings_dest) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = MoreAdapter(items)
    }

    private fun goto(destId: Int) = findNavController().navigate(destId)

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

data class MoreItem(val iconRes: Int, val title: String, val onClick: () -> Unit)

class MoreAdapter(private val items: List<MoreItem>) : androidx.recyclerview.widget.RecyclerView.Adapter<MoreAdapter.VH>() {
    inner class VH(val binding: ItemMoreMenuBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMoreMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.menuIcon.setImageResource(item.iconRes)
        holder.binding.menuTitle.text = item.title
        holder.binding.root.setOnClickListener { item.onClick() }
    }
    override fun getItemCount() = items.size
}
