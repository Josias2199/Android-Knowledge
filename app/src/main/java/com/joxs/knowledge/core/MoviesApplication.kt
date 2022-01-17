package com.joxs.knowledge.core

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.joxs.knowledge.data.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoviesApplication: Application()