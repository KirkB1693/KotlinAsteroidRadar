package com.example.asteroidradar.repository

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.asteroidradar.Constants.API_KEY
import com.example.asteroidradar.Constants.PICTURE_MEDIA_TYPE
import com.example.asteroidradar.Constants.PICTURE_THUMBNAIL
import com.example.asteroidradar.Constants.PICTURE_TITLE
import com.example.asteroidradar.Constants.PICTURE_URL
import com.example.asteroidradar.PictureOfDay
import com.example.asteroidradar.R
import com.example.asteroidradar.api.AsteroidApi
import com.example.asteroidradar.api.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection
bhbhjkij
class PictureOfTheDayRepository(private val application: Application) {

    private var sharedPreferences =
        application.getSharedPreferences(
            application.getString(R.string.picture_preference_file_key),
            MODE_PRIVATE
        )
    private val cw = ContextWrapper(application.applicationContext)
    private val path = cw.getDir("images", MODE_PRIVATE).toString() + "pictureoftheday.jpg"
    private val _pictureOfTheDayDrawablePath = MutableLiveData<String>()
    val pictureOfTheDayDrawablePath: LiveData<String>
        get() = _pictureOfTheDayDrawablePath

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay


    suspend fun refreshPictureOfTheDay() {
        var newPicture: PictureOfDay
        var newPath = ""
        if (isNetworkAvailable(application)) {
            @Suppress("BlockingMethodInNonBlockingContext")
            withContext(Dispatchers.IO) {

                val pictureOfTheDay = AsteroidApi.retrofitMoshiService.getNASAImageOfTheDay(API_KEY)
                if (pictureOfTheDay.url.isNotEmpty()) {
                    newPicture = pictureOfTheDay
                    val `in`: InputStream?
                    val bmp: Bitmap?
                    try {
                        val con: HttpsURLConnection =
                            if (pictureOfTheDay.mediaType == "image") {  // if it's an image use the url as given
                                URL(pictureOfTheDay.url).openConnection() as HttpsURLConnection
                            } else {    // if it's a video, use the thumbnail image if it exists, otherwise strip the video id out and create a new url for the image thumbnail
                                URL(getPictureUrlFromVideo(pictureOfTheDay)).openConnection() as HttpsURLConnection
                            }
                        con.doInput = true
                        con.connect()
                        if (con.responseCode == HttpsURLConnection.HTTP_OK) {
                            //download
                            `in` = con.inputStream
                            bmp = BitmapFactory.decodeStream(`in`)
                            `in`.close()
                            val file = File(path)
                            try {
                                // Get the file output stream
                                val stream: OutputStream = FileOutputStream(file)

                                // Compress bitmap
                                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                                // Flush the stream
                                stream.flush()

                                // Close stream
                                stream.close()
                                newPath = path
                                val editor = sharedPreferences.edit()
                                editor.putString(PICTURE_MEDIA_TYPE, pictureOfTheDay.mediaType)
                                editor.putString(PICTURE_TITLE, pictureOfTheDay.title)
                                editor.putString(PICTURE_URL, pictureOfTheDay.url)
                                editor.putString(PICTURE_THUMBNAIL, pictureOfTheDay.thumbnailUrl)
                                editor.apply()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            newPath = ""
                        }
                    } catch (ex: Exception) {
                        Log.e("Exception", ex.toString())
                    }
                } else {
                    newPath = path
                    newPicture = PictureOfDay(
                        sharedPreferences.getString(PICTURE_MEDIA_TYPE, "")!!,
                        sharedPreferences.getString(PICTURE_TITLE, "")!!,
                        sharedPreferences.getString(PICTURE_URL, "")!!,
                        sharedPreferences.getString(PICTURE_THUMBNAIL, null)
                    )
                }
            }
        } else {
            newPath = path
            newPicture = PictureOfDay(
                sharedPreferences.getString(PICTURE_MEDIA_TYPE, "")!!,
                sharedPreferences.getString(PICTURE_TITLE, "")!!,
                sharedPreferences.getString(PICTURE_URL, "")!!,
                sharedPreferences.getString(PICTURE_THUMBNAIL, null)
            )
        }
        if (newPicture.title == "") {
            newPicture.title = "No Internet!!!\nPlease connect and then restart the app."
        }
        _pictureOfDay.value = newPicture
        _pictureOfTheDayDrawablePath.value = newPath
    }

    private fun getPictureUrlFromVideo(pictureOfTheDay: PictureOfDay): String {
        if (!pictureOfTheDay.thumbnailUrl.isNullOrEmpty()) {  // if the thumbnail url exists use it
            return pictureOfTheDay.thumbnailUrl
        } else {  // otherwise strip video id out of video url and return the proper thumbnail for the video
            val videoId: String = if (pictureOfTheDay.url.contains(
                    "embed/",
                    ignoreCase = true
                )
            ) {  // if video url is embed format strip out video id
                val uri: Uri = Uri.parse(pictureOfTheDay.url)
                uri.lastPathSegment.toString()
            } else {  // otherwise strip out the video id using the v query parameter
                val uri: Uri = Uri.parse(pictureOfTheDay.url)
                uri.getQueryParameter("v").toString()
            }
            return "https://img.youtube.com/vi/$videoId/0.jpg"
        }
    }


}