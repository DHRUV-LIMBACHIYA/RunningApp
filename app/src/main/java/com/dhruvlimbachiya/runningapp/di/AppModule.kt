package com.dhruvlimbachiya.runningapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dhruvlimbachiya.runningapp.db.RunDatabase
import com.dhruvlimbachiya.runningapp.others.Constants.KEY_IS_FIRST_TIME
import com.dhruvlimbachiya.runningapp.others.Constants.KEY_NAME
import com.dhruvlimbachiya.runningapp.others.Constants.KEY_WEIGHT
import com.dhruvlimbachiya.runningapp.others.Constants.RUN_DATABASE_NAME
import com.dhruvlimbachiya.runningapp.others.Constants.SHARED_PREF_NAME
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

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext app: Context
    ) = app.getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideRunnerName(
        sharedPreferences: SharedPreferences
    ) = sharedPreferences.getString(KEY_NAME,"") ?: ""

    @Singleton
    @Provides
    fun provideRunnerWeight(
        sharedPreferences: SharedPreferences
    ) = sharedPreferences.getFloat(KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun provideIsFirstTimeToggle(
        sharedPreferences: SharedPreferences
    ) = sharedPreferences.getBoolean(KEY_IS_FIRST_TIME,true)
}