package com.joxs.knowledge.app

import android.app.Application
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.joxs.knowledge.data.local.AppDatabase
import com.joxs.knowledge.data.remote.ApiClient
import com.joxs.knowledge.data.remote.ApiConstants
import com.joxs.knowledge.data.remote.ApiService

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(ApiClient.client)
            .addConverterFactory(GsonConverterFactory.create(ApiClient.gson))
            .build()

    @Provides
    @Singleton
    fun provideMovieApi(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(app: Application) : AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "movies_database")
            .build()
}