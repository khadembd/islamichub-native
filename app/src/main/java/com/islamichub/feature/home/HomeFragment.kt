package com.islamichub.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.islamichub.R
import com.islamichub.core.ui.ContentCardItem
import com.islamichub.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * HomeFragment — landing screen.
 * Shows: today's date, next prayer, daily ayah, feature grid.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDate()
        setupFeatureGrid()
        observeViewModel()
    }

    private fun setupDate() {
        val today = LocalDate.now()
        val bnDate = today.format(DateTimeFormatter.ofPattern("d MMMM, yyyy", Locale("bn")))
        binding.todayDate.text = bnDate
    }

    private fun setupFeatureGrid() {
        val features = listOf(
            FeatureItem(R.drawable.ic_quran,   "কুরআন",          "১১৪ সূরা")   { goto(R.id.quran_dest) },
            FeatureItem(R.drawable.ic_prayer,  "নামাজ শিক্ষা",   "ধাপে ধাপে")  { goto(R.id.namaz_dest) },
            FeatureItem(R.drawable.ic_prayer,  "নামাজের সময়",   "৫ ওয়াক্ত")  { goto(R.id.prayer_dest) },
            FeatureItem(R.drawable.ic_qibla,   "কিবলা",          "কম্পাস")     { goto(R.id.qibla_dest) },
            FeatureItem(R.drawable.ic_dua,     "দোয়া",           "শ্রেণীবদ্ধ") { goto(R.id.dua_dest) },
            FeatureItem(R.drawable.ic_zikr,    "জিকির / তসবিহ", "কাউন্টার")   { goto(R.id.zikr_dest) },
            FeatureItem(R.drawable.ic_star,    "আসমাউল হুসনা", "৯৯ নাম")     { goto(R.id.asmaul_husna_dest) },
            FeatureItem(R.drawable.ic_book,    "হাদিস",          "বুখারী ও অন্যান্য") { goto(R.id.hadith_dest) },
            FeatureItem(R.drawable.ic_story,   "ইসলামিক গল্প",  "নবী ও খলিফা") { goto(R.id.stories_dest) },
            FeatureItem(R.drawable.ic_help,    "প্রশ্নোত্তর",    "বিভিন্ন বিষয়") { goto(R.id.questions_dest) },
            FeatureItem(R.drawable.ic_info,    "ভুল ধারণা",      "পরিষ্কার ধারণা") { goto(R.id.misconceptions_dest) },
            FeatureItem(R.drawable.ic_bookmark,"বুকমার্ক",      "সংরক্ষিত")   { goto(R.id.bookmarks_dest) },
        )
        val adapter = FeatureAdapter(features)
        binding.featureGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.featureGrid.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.nextPrayerInfo.observe(viewLifecycleOwner) { info ->
            binding.nextPrayerName.text = info
        }
        viewModel.dailyAyah.observe(viewLifecycleOwner) { ayah ->
            binding.dailyAyahArabic.text = ayah?.arabic.orEmpty()
            binding.dailyAyahTranslation.text = ayah?.bangla.orEmpty()
        }
    }

    private fun goto(destId: Int) {
        findNavController().navigate(destId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class FeatureItem(
    val iconRes: Int,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)
