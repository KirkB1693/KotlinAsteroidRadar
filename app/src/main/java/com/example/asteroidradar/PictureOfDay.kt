package com.example.asteroidradar

import com.squareup.moshi.Json

data class PictureOfDay(@Json(name = "media_type") val mediaType: String, var title: String,
                        val url: String, @Json(name = "thumbnail_url") val thumbnailUrl: String?)