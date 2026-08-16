package com.islamichub.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * FirebaseModule — Hilt DI module providing Firebase Firestore instance.
 * Firebase is auto-initialized by the google-services plugin at app startup
 * using `app/google-services.json` (package_name: `com.islamic.islam`).
 *
 * applicationId in build.gradle.kts matches `com.islamic.islam` so Firebase
 * accepts the connection.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
}
