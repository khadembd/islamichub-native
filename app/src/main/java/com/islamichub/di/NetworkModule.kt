package com.islamichub.di

import com.islamichub.data.api.AlQuranApi
import com.islamichub.data.api.AladhanApi
import com.islamichub.data.api.HadithApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * NetworkModule — Retrofit clients for all external APIs.
 *
 * APIs used:
 *  - Aladhan (prayer times): https://api.aladhan.com/v1/
 *  - AlQuran Cloud (Quran): https://api.alquran.cloud/v1/
 *  - Hadith API (fawazahmed0): https://cdn.jsdelivr.net/gh/
 *  - Gemini AI (AI Scholar, Tajweed, Vision): https://generativelanguage.googleapis.com/
 *
 * No API keys hardcoded — user-supplied keys stored in DataStore.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val ALADHAN_BASE = "https://api.aladhan.com/"
    private const val QURAN_BASE = "https://api.alquran.cloud/"
    private const val HADITH_BASE = "https://cdn.jsdelivr.net/gh/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("aladhan")
    fun provideAladhanRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(ALADHAN_BASE)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    @Named("quran")
    fun provideQuranRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(QURAN_BASE)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    @Named("hadith")
    fun provideHadithRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(HADITH_BASE)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideAladhanApi(@Named("aladhan") retrofit: Retrofit): AladhanApi =
        retrofit.create(AladhanApi::class.java)

    @Provides
    @Singleton
    fun provideAlQuranApi(@Named("quran") retrofit: Retrofit): AlQuranApi =
        retrofit.create(AlQuranApi::class.java)

    @Provides
    @Singleton
    fun provideHadithApi(@Named("hadith") retrofit: Retrofit): HadithApi =
        retrofit.create(HadithApi::class.java)
}
