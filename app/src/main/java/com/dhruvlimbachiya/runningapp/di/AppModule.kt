package com.dhruvlimbachiya.runningapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dhruvlimbachiya.runningapp.db.RunDatabase
import com.dhruvlimbachiya.runningapp.others.Constants.RUN_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Function responsible for creating and providing RunDatabase instance.
    @Singleton
    @Provides
    fun provideRunDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RunDatabase::class.java,
        RUN_DATABASE_NAME
    ).build()

    // Function responsible for providing database's DAO(RunDao).
    @Singleton
    @Provides
    fun provideRunDao(runDatabase: RunDatabase) = runDatabase.getRunDao()
}