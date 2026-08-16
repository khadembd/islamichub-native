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
import dagger.hilt.android.AndroidEntryPoint

/**
 * MoreFragment — premium "All Features" screen.
 * Per source islamic.html sidebar: lists ALL 20+ features with icons + descriptions.
 * Premium background: bg_premium_more (sidebar_premium_bg.webp + ivory gradient).
 */
@AndroidEntryPoint
class MoreFragment : Fragment() {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val items = listOf(
                MoreItem(R.drawable.ic_quran,    "কুরআন পাঠ",        "১১৪ সূরা • অডিও সহ")      { goto(R.id.quran_reader_dest) },
                MoreItem(R.drawable.ic_book,     "হাদিস সংগ্রহ",    "৮টি বই • বুখারী থেকে আহমদ") { goto(R.id.hadith_api_dest) },
                MoreItem(R.drawable.ic_prayer,   "নামাজ শিক্ষা",     "ধাপে ধাপে • আরবি + বাংলা")  { goto(R.id.namaz_dest) },
                MoreItem(R.drawable.ic_dua,      "দোয়া",             "১২ শ্রেণী • ২৮+ দোয়া")     { goto(R.id.dua_dest) },
                MoreItem(R.drawable.ic_zikr,     "জিকির / তসবিহ",   "৬ ধরনের জিকির • কাউন্টার") { goto(R.id.zikr_dest) },
                MoreItem(R.drawable.ic_star,     "আসমাউল হুসনা",   "৯৯টি নাম • অর্থ ও আমল")    { goto(R.id.asmaul_husna_dest) },
                MoreItem(R.drawable.ic_qibla,    "কিবলা কম্পাস",    "সেন্সর + লোকেশন")          { goto(R.id.qibla_dest) },
                MoreItem(R.drawable.ic_prayer,   "নামাজের সময়",    "৫ ওয়াক্ত • হিজরি তারিখ")  { goto(R.id.prayer_dest) },
                MoreItem(R.drawable.ic_bookmark, "বুকমার্ক",         "সংরক্ষিত আয়াত/হাদিস")     { goto(R.id.bookmarks_dest) },
                MoreItem(R.drawable.ic_search,   "খুঁজুন",            "সব বিষয় • একসাথে")         { goto(R.id.search_dest) },
                MoreItem(R.drawable.ic_story,    "ইসলামিক গল্প",   "নবী • খলিফা • মেরাজ")     { goto(R.id.stories_dest) },
                MoreItem(R.drawable.ic_info,     "ভুল ধারণা",        "পরিষ্কার ধারণা")            { goto(R.id.misconceptions_dest) },
                MoreItem(R.drawable.ic_help,     "প্রশ্নোত্তর",      "বিভিন্ন বিষয়ে প্রশ্ন")     { goto(R.id.questions_dest) },
                MoreItem(R.drawable.ic_play,     "নামাজ অডিও",       "১৫টি MP3 • ব্যাকগ্রাউন্ড") { goto(R.id.audio_dest) },
                MoreItem(R.drawable.ic_prayer,   "নামাজ ট্র্যাকার",  "স্ট্রিক • সাপ্তাহিক ভিউ")  { goto(R.id.salah_tracker_dest) },
                MoreItem(R.drawable.ic_book,     "পড়া ট্র্যাকার",    "কুরআন পড়ার লগ")            { goto(R.id.reading_tracker_dest) },
                MoreItem(R.drawable.ic_mic,      "এআই স্কলার",       "Gemini/OpenRouter • প্রশ্নোত্তর") { goto(R.id.ai_scholar_dest) },
                MoreItem(R.drawable.ic_camera,   "ভিশন স্ক্যানার",   "ক্যামেরা • ছবি বিশ্লেষণ")   { goto(R.id.scanner_dest) },
                MoreItem(R.drawable.ic_mic,      "তাজবিদ চেকার",     "রেকর্ড • আবৃত্তি বিশ্লেষণ") { goto(R.id.tajweed_dest) },
                MoreItem(R.drawable.ic_quran,    "দৈনিক বিষয়বস্তু", "আজকের আয়াত/হাদিস/দোয়া")  { goto(R.id.daily_content_dest) },
                MoreItem(R.drawable.ic_compass,  "হিজরি তারিখ",      "ইসলামিক ক্যালেন্ডার")       { goto(R.id.hijri_dest) },
                MoreItem(R.drawable.ic_lock,     "অ্যাপ লক",          "বায়োমেট্রিক • পিন ফলব্যাক") { goto(R.id.app_lock_dest) },
                MoreItem(R.drawable.ic_prayer,   "প্রোফাইল",           "ব্যবহারকারী • পরিসংখ্যান • সিঙ্ক") { goto(R.id.profile_dest) },
                MoreItem(R.drawable.ic_settings, "সেটিংস",            "থিম • ভাষা • মাযহাব • নোটিফিকেশন") { goto(R.id.settings_dest) }
            )
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.setHasFixedSize(false)
            binding.recyclerView.adapter = MoreAdapter(items)
        } catch (e: Exception) {
            // Defensive: More menu must never crash
        }
    }

    private fun goto(destId: Int) {
        try {
            findNavController().navigate(destId)
        } catch (e: Exception) {
            // Defensive navigation
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

data class MoreItem(val iconRes: Int, val title: String, val subtitle: String, val onClick: () -> Unit)

class MoreAdapter(private val items: List<MoreItem>) : androidx.recyclerview.widget.RecyclerView.Adapter<MoreAdapter.VH>() {
    inner class VH(val binding: ItemMoreMenuBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMoreMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.menuIcon.setImageResource(item.iconRes)
        holder.binding.menuTitle.text = item.title
        holder.binding.menuSubtitle.text = item.subtitle
        holder.binding.root.setOnClickListener {
            try { item.onClick() } catch (_: Exception) {}
        }
    }
    override fun getItemCount() = items.size
}
