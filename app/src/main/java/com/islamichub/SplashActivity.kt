package com.islamichub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.islamichub.core.utils.CrashHandler
import kotlin.random.Random

/**
 * SplashActivity — premium splash screen matching source islamic.html #appSplash.
 *
 * Shows for ~2.5s with:
 *  - Mosque image background + gradient overlay
 *  - Animated logo (scale + fade in)
 *  - Rotating Quran quotes (one per launch)
 *  - Loading progress bar (animated 0-100%)
 *
 * Then transitions to MainActivity.
 *
 * পুরোপুরি নেটিভ — system SplashScreen API ব্যবহার করা হয় না যাতে
 * আমরা full control পাই image background + quote + animation এর উপর।
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
        try {
            setContentView(R.layout.splash_screen)
            animateSplash()
        } catch (e: Exception) {
            // Defensive: if splash layout fails, jump directly to MainActivity
            launchMain()
        }
    }

    private fun animateSplash() {
        // Pick a random Quran quote
        val (arabic, _) = quranQuotes[Random.nextInt(quranQuotes.size)]
        findViewById<TextView>(R.id.splashQuote).text = arabic

        // Animate logo with scale + fade in
        val logoFrame = findViewById<View>(R.id.splashQuote)
        try {
            val scaleAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            scaleAnim.duration = 800
            logoFrame.startAnimation(scaleAnim)
        } catch (e: Exception) {
            // Defensive: animation may fail on some devices
        }

        // Animate progress bar
        val progress = findViewById<ProgressBar>(R.id.splashProgress)
        val handler = Handler(Looper.getMainLooper())
        var p = 0
        val progressRunnable = object : Runnable {
            override fun run() {
                try {
                    p += 5
                    progress.progress = p
                    if (p < 100) {
                        handler.postDelayed(this, 100)
                    } else {
                        // Progress complete, launch MainActivity after short delay
                        handler.postDelayed({
                            launchMain()
                        }, 300)
                    }
                } catch (e: Exception) {
                    // Defensive: if animation loop fails, just launch main
                    launchMain()
                }
            }
        }
        handler.postDelayed(progressRunnable, 200)
    }

    private fun launchMain() {
        try {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } catch (e: Exception) {
            // Last resort: finish activity to avoid black screen
            finishAffinity()
        }
    }
}
