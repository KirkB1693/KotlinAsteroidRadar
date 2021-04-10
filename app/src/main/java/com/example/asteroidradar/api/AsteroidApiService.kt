package com.example.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.asteroidradar.Constants.BASE_URL
import com.example.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit_scalar = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

private val retrofit_moshi = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(@Query("api_key") apiKey : String) : String

    @GET( "planetary/apod")
    suspend fun getNASAImageOfTheDay(@Query("api_key") apiKey: String) : PictureOfDay
}

object AsteroidApi {
    val retrofitScalarService : AsteroidApiService by lazy { retrofit_scalar.create(AsteroidApiService::class.java) }
    val retrofitMoshiService : AsteroidApiService by lazy { retrofit_moshi.create(AsteroidApiService::class.java) }
}