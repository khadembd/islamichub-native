# app/proguard-rules.pro
# Islamic Hub — ProGuard / R8 rules
# Hilt, Room, Kotlinx Serialization, Retrofit, Media3 এর জন্য rules

# --- Kotlin / Kotlinx Serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep serializers for @Serializable classes
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.islamichub.**$$serializer { *; }
-keepclassmembers class com.islamichub.** {
    *** Companion;
}
-keepclasseswithmembers class com.islamichub.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# --- Hilt ---
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# --- Retrofit / OkHttp ---
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepattributes Signature, Exceptions

# --- Media3 ---
-dontwarn androidx.media3.**

# --- Model classes (data classes for JSON parsing) ---
-keep class com.islamichub.data.model.** { *; }

# --- Kotlin coroutines ---
-keepclassmembers class kotlinx.coroutines.** { *; }
