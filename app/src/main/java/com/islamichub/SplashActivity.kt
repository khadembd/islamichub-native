package com.islamichub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.islamichub.core.utils.CrashHandler
import kotlin.random.Random

/**
 * SplashActivity — premium splash screen matching source islamic.html #appSplash.
 *
 * Shows for 2.5s with:
 *  - Mosque image background + gradient overlay
 *  - Animated logo
 *  - Rotating Quran quotes (one per launch)
 *  - Loading progress bar
 *
 * Then transitions to MainActivity.
 */
class SplashActivity : AppCompatActivity() {

    private val quranQuotes = listOf(
        "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ" to "পরম করুণাময় অসীম দয়ালু আল্লাহর নামে",
        "وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا" to "যে আল্লাহকে ভয় করে, তিনি তার জন্য বের হবার পথ করে দেন",
        "إِنَّ مَعَ الْعُسْرِ يُسْرًا" to "নিশ্চয়ই কষ্টের সাথেই রয়েছে স্বস্তি",
        "فَاذْكُرُونِي أَذْكُرْكُمْ" to "অতএব তোমরা আমাকে স্মরণ করো, আমিও তোমাদের স্মরণ করব",
        "وَأَقِيمُوا الصَّلَاةَ" to "এবং তোমরা নামাজ কায়েম করো"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        CrashHandler.install()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // Pick a random Quran quote
        val (arabic, bangla) = quranQuotes[Random.nextInt(quranQuotes.size)]
        findViewById<TextView>(R.id.splashQuote).text = arabic

        // Animate progress bar
        val progress = findViewById<ProgressBar>(R.id.splashProgress)
        val handler = Handler(Looper.getMainLooper())
        var p = 0
        val progressRunnable = object : Runnable {
            override fun run() {
                p += 4
                progress.progress = p
                if (p < 100) {
                    handler.postDelayed(this, 80)
                } else {
                    // Progress complete, launch MainActivity after short delay
                    handler.postDelayed({
                        try {
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                        } catch (e: Exception) {
                            // Defensive: in case activity launch fails
                            finishAffinity()
                        }
                    }, 200)
                }
            }
        }
        handler.postDelayed(progressRunnable, 100)
    }
}
