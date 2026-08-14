package com.islamichub.di

import android.content.Context
import androidx.room.Room
import com.islamichub.data.local.IslamicHubDatabase
import com.islamichub.data.local.dao.BookmarkDao
import com.islamichub.data.local.dao.SalahTrackerDao
import com.islamichub.data.local.dao.ZikrSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): IslamicHubDatabase =
        Room.databaseBuilder(ctx, IslamicHubDatabase::class.java, IslamicHubDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideBookmarkDao(db: IslamicHubDatabase): BookmarkDao = db.bookmarkDao()

    @Provides
    fun provideSalahTrackerDao(db: IslamicHubDatabase): SalahTrackerDao = db.salahTrackerDao()

    @Provides
    fun provideZikrSessionDao(db: IslamicHubDatabase): ZikrSessionDao = db.zikrSessionDao()
}
