package com.goofygoobers.geoart.directions.di

import com.goofygoobers.geoart.directions.data.remote.GooglePlacesApi
import com.goofygoobers.geoart.directions.data.repository.GooglePlacesInfoRepositoryImplementation
import com.goofygoobers.geoart.directions.domain.repository.GooglePlacesInfoRepository
import com.goofygoobers.geoart.directions.domain.usecase.GetDirectionInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// This module provides dependencies related to Google Places.
@Module
@InstallIn(SingletonComponent::class)
object GooglePlacesModule {
    @Provides
    @Singleton
    fun provideGetDirectionInfo(repository: GooglePlacesInfoRepository): GetDirectionInfo {
        return GetDirectionInfo(repository = repository)
    }

    @Provides
    @Singleton
    fun provideDirectionInfoRepository(api: GooglePlacesApi): GooglePlacesInfoRepository {
        return GooglePlacesInfoRepositoryImplementation(api = api)
    }

    @Provides
    @Singleton
    fun provideGooglePlacesApi(): GooglePlacesApi {
        return Retrofit.Builder()
            .baseUrl(GooglePlacesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GooglePlacesApi::class.java)
    }
}
