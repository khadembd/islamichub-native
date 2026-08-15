package com.islamichub.feature.home

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.islamichub.R
import com.islamichub.databinding.FragmentHomeBinding
import com.islamichub.databinding.ItemPrayerDotBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * HomeFragment — premium landing screen with crash-proofing.
 *
 * All UI setup and ViewModel observation wrapped in try/catch.
 * Prayer progress rendering is defensive — if binding fails, screen stays empty
 * but app does not crash.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private var countdownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try { setupGreeting() } catch (e: Exception) { /* Defensive */ }
        try { setupFeatureGrid() } catch (e: Exception) { /* Defensive */ }
        try { observeViewModel() } catch (e: Exception) { /* Defensive */ }
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 4..11  -> R.string.greeting_morning
            in 12..15 -> R.string.greeting_afternoon
            in 16..19 -> R.string.greeting_evening
            else      -> R.string.greeting_night
        }
        binding.greetingText.setText(greeting)

        val today = LocalDate.now()
        val bnDate = today.format(DateTimeFormatter.ofPattern("d MMMM, yyyy", Locale("bn")))
        binding.todayDate.text = bnDate
    }

    private fun setupFeatureGrid() {
        val features = listOf(
            FeatureItem(R.drawable.ic_quran,    "কুরআন",          "১১৪ সূরা")     { goto(R.id.quran_dest) },
            FeatureItem(R.drawable.ic_prayer,   "নামাজ",           "শিক্ষা")        { goto(R.id.namaz_dest) },
            FeatureItem(R.drawable.ic_qibla,    "কিবলা",          "কম্পাস")        { goto(R.id.qibla_dest) },
            FeatureItem(R.drawable.ic_dua,      "দোয়া",           "শ্রেণীবদ্ধ")   { goto(R.id.dua_dest) },
            FeatureItem(R.drawable.ic_zikr,     "জিকির",          "তসবিহ")         { goto(R.id.zikr_dest) },
            FeatureItem(R.drawable.ic_star,     "আসমাউল হুসনা", "৯৯ নাম")        { goto(R.id.asmaul_husna_dest) },
            FeatureItem(R.drawable.ic_book,     "হাদিস",          "বুখারী")        { goto(R.id.hadith_dest) },
            FeatureItem(R.drawable.ic_story,    "গল্প",            "নবী ও খলিফা") { goto(R.id.stories_dest) },
            FeatureItem(R.drawable.ic_help,     "প্রশ্নোত্তর",    "বিভিন্ন")       { goto(R.id.questions_dest) },
            FeatureItem(R.drawable.ic_info,     "ভুল ধারণা",      "পরিষ্কার")     { goto(R.id.misconceptions_dest) },
            FeatureItem(R.drawable.ic_search,   "খুঁজুন",          "সব বিষয়")      { goto(R.id.search_dest) },
            FeatureItem(R.drawable.ic_bookmark, "বুকমার্ক",       "সংরক্ষিত")      { goto(R.id.bookmarks_dest) }
        )
        val adapter = FeatureAdapter(features)
        binding.featureGrid.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.featureGrid.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.nextPrayerInfo.observe(viewLifecycleOwner) { info ->
            try {
                binding.nextPrayerName.text = info?.name ?: "—"
                binding.nextPrayerTime.text = info?.timeText ?: "—"
                if (info != null) startCountdown(info.timeMillis)
            } catch (e: Exception) { /* Defensive */ }
        }

        viewModel.prayerProgress.observe(viewLifecycleOwner) { rows ->
            try { renderPrayerProgress(rows) } catch (e: Exception) { /* Defensive */ }
        }

        viewModel.dailyAyah.observe(viewLifecycleOwner) { ayah ->
            try {
                binding.dailyAyahArabic.text = ayah?.arabic.orEmpty()
                binding.dailyAyahTranslation.text = ayah?.meaning.orEmpty()
                binding.dailyAyahRef.text = ayah?.transliteration.orEmpty()
            } catch (e: Exception) { /* Defensive */ }
        }

        viewModel.dailyHadith.observe(viewLifecycleOwner) { hadith ->
            try {
                binding.dailyHadithArabic.text = hadith?.arabic.orEmpty()
                binding.dailyHadithTranslation.text = hadith?.bangla.orEmpty()
                binding.dailyHadithRef.text = hadith?.reference.orEmpty()
            } catch (e: Exception) { /* Defensive */ }
        }
    }

    private fun renderPrayerProgress(rows: List<PrayerProgressRow>) {
        val bindings = listOf(
            ItemPrayerDotBinding.bind(binding.prayerFajr.root),
            ItemPrayerDotBinding.bind(binding.prayerDhuhr.root),
            ItemPrayerDotBinding.bind(binding.prayerAsr.root),
            ItemPrayerDotBinding.bind(binding.prayerMaghrib.root),
            ItemPrayerDotBinding.bind(binding.prayerIsha.root)
        )
        rows.forEachIndexed { idx, row ->
            val b = bindings.getOrNull(idx) ?: return@forEachIndexed
            b.prayerDotName.text = row.name
            b.prayerDotTime.text = row.timeText
            val color = when (row.state) {
                PrayerState.CURRENT   -> requireContext().getColor(R.color.primary)
                PrayerState.NEXT      -> requireContext().getColor(R.color.gold)
                PrayerState.COMPLETED -> requireContext().getColor(R.color.status_success)
                PrayerState.UPCOMING  -> requireContext().getColor(R.color.divider_strong)
            }
            b.prayerDotBg.background.setTint(color)
            b.prayerCheckIcon.visibility = if (row.state == PrayerState.COMPLETED) View.VISIBLE else View.GONE
        }
    }

    private fun startCountdown(nextPrayerMillis: Long) {
        countdownTimer?.cancel()
        val now = System.currentTimeMillis()
        val remaining = nextPrayerMillis - now
        if (remaining <= 0) {
            binding.countdownText.text = ""
            return
        }
        countdownTimer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    val totalSec = millisUntilFinished / 1000
                    val h = totalSec / 3600
                    val m = (totalSec % 3600) / 60
                    val s = totalSec % 60
                    binding.countdownText.text = "শেষ হবে ${String.format("%02d:%02d:%02d", h, m, s)}"
                } catch (e: Exception) { /* Defensive */ }
            }
            override fun onFinish() {
                try { binding.countdownText.text = "" } catch (e: Exception) {}
            }
        }.start()
    }

    private fun goto(destId: Int) {
        try {
            findNavController().navigate(destId)
        } catch (e: Exception) {
            // Defensive navigation — prevents crashes if destination is not in current graph
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
        _binding = null
    }
}

data class FeatureItem(
    val iconRes: Int,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

enum class PrayerState { CURRENT, NEXT, COMPLETED, UPCOMING }

data class PrayerProgressRow(
    val name: String,
    val timeText: String,
    val state: PrayerState
)
