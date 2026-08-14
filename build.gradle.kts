// build.gradle.kts (root)
// Islamic Hub — Native Android build configuration
// Top-level file — শুধুমাত্র plugin versions এখানে ডিক্লেয়ার করা হয়

plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24" apply false
}
