// app/build.gradle.kts
// Islamic Hub — Native Android application module
// অ্যাপ্লিকেশন মডিউল: Kotlin + Jetpack + Material 3 + Room + Hilt + Media3 + CameraX

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.islamichub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.islamichub"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }

        // Large resources compression for APK size
        resourceConfigurations += listOf("en", "bn")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Unsigned release build for now — CI will produce unsigned APK
            // For play store signing, configure signingConfig in a separate
            // keystore.properties-driven block.
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
        }
    }
    androidResources {
        // Bengali + Arabic text needs no compression to render correctly
        noCompress += listOf("json", "mp3")
    }
}

dependencies {
    // AndroidX core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")

    // Material 3 (View-based)
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Retrofit / OkHttp (for AI Scholar / Hadith API)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Media3 (audio playback for namaz / quran)
    implementation("androidx.media3:media3-exoplayer:1.4.0")
    implementation("androidx.media3:media3-ui:1.4.0")
    implementation("androidx.media3:media3-session:1.4.0")
    implementation("androidx.media3:media3-common:1.4.0")

    // CameraX (vision scanner)
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Biometric (app lock)
    implementation("androidx.biometric:biometric:1.1.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Image loading — Coil
    implementation("io.coil-kt:coil:2.6.0")

    // WebView (used only for legacy admin reference page — NOT for the main app shell)
    // The plan §2 explicitly forbids WebView/Capacitor for app UI. This dependency
    // exists solely so the admin.html reference can be opened as a debug-only
    // operator screen. It is excluded from release builds via build variant.
    debugImplementation("androidx.webkit:webkit:1.11.0")

    // Splashscreen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    // NOTE: Firebase Android SDK is intentionally NOT enabled in v1.
    // The original source's google-services.json is bound to applicationId
    // `com.islamic.islam`; our native app uses `com.islamichub`. To enable
    // Firebase, generate a new google-services.json for `com.islamichub`,
    // drop it into app/, uncomment the lines below, and add the
    // `com.google.gms.google-services` plugin at the top of this file.
    // implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    // implementation("com.google.firebase:firebase-firestore-ktx")
    // implementation("com.google.firebase:firebase-analytics-ktx")

    // Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
