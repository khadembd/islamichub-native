package com.islamichub.feature.audio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.islamichub.core.ui.ContentCardItem

class AudioViewModel : ViewModel() {

    private val _audioList = MutableLiveData<List<ContentCardItem>>()
    val audioList: LiveData<List<ContentCardItem>> = _audioList

    init {
        _audioList.value = listOf(
            ContentCardItem(id = "azan2", title = "আজান", subtitle = "নামাজের ডাক"),
            ContentCardItem(id = "takbir_tahrimah", title = "তাকবীরে তাহরীমা", subtitle = "নামাজ শুরুর তাকবীর", arabic = "اللَّهُ أَكْبَرُ"),
            ContentCardItem(id = "tasmiah", title = "তাসমিয়া", subtitle = "বিসমিল্লাহ", arabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"),
            ContentCardItem(id = "taawwuz", title = "তাআউয", subtitle = "আউযুবিল্লাহ", arabic = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ"),
            ContentCardItem(id = "dua_al_istiftah", title = "দোয়া-এ-ইস্তিফতাহ", subtitle = "নামাজের প্রারম্ভিক দোয়া"),
            ContentCardItem(id = "fatiha", title = "সূরা আল-ফাতিহা", subtitle = "৭ আয়াত", arabic = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ"),
            ContentCardItem(id = "ikhlas", title = "সূরা আল-ইখলাস", subtitle = "৪ আয়াত", arabic = "قُلْ هُوَ اللَّهُ أَحَدٌ"),
            ContentCardItem(id = "ruku", title = "রুকুর তাসবিহ", subtitle = "সুবহানা রাব্বিয়াল আযীম", arabic = "سُبْحَانَ رَبِّيَ الْعَظِيمِ"),
            ContentCardItem(id = "sajdah", title = "সিজদার তাসবিহ", subtitle = "সুবহানা রাব্বিয়াল আলা", arabic = "سُبْحَانَ رَبِّيَ الْأَعْلَى"),
            ContentCardItem(id = "jalsah", title = "জালসার দোয়া", subtitle = "আত্তাহিয়্যাতু", arabic = "التَّحِيَّاتُ لِلَّهِ"),
            ContentCardItem(id = "qawamah", title = "কাওমার দোয়া", subtitle = "রুকু থেকে ওঠার দোয়া", arabic = "سَمِعَ اللَّهُ لِمَنْ حَمِدَهُ"),
            ContentCardItem(id = "qunut", title = "দোয়া-এ-কুনুত", subtitle = "বিতর নামাজের দোয়া"),
            ContentCardItem(id = "tashahud", title = "তাশাহুদ", subtitle = "আত্তাহিয়্যাতু", arabic = "التَّحِيَّاتُ الْمُبَارَكَاتُ"),
            ContentCardItem(id = "salat_alan_nabi_darud", title = "দরুদ শরীফ", subtitle = "সালাত আলান নাবী", arabic = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ"),
            ContentCardItem(id = "salam", title = "সালাম", subtitle = "নামাজ শেষের সালাম", arabic = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ")
        )
    }
}
