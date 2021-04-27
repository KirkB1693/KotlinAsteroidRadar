package com.example.asteroidradar.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.asteroidradar.Asteroid
import com.example.asteroidradar.Constants.API_KEY
import com.example.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.example.asteroidradar.api.AsteroidApi
import com.example.asteroidradar.api.isNetworkAvailable
import com.example.asteroidradar.api.parseAsteroidsJsonResult
import com.example.asteroidradar.database.AsteroidsDatabase
import com.example.asteroidradar.database.asDatabaseModel
import com.example.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepository(private val database: AsteroidsDatabase, private val context: Context) {
    private var today : String
    private var sevenDaysFromToday : String

    init {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.getDefault())
        today = dateFormat.format(calendar.time)
        calendar.add(Calendar.DATE, 6)
        sevenDaysFromToday = dateFormat.format(calendar.time)
    }

    /**
     * A list of asteroids that can be shown on the screen.
     */
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids(today)) {
            it.asDomainModel()
        }

    val asteroidsForWeek: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getWeekOfAsteroids(today, sevenDaysFromToday)) {
            it.asDomainModel()
        }

    val asteroidsForToday: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getTodaysAsteroids(today)) {
            it.asDomainModel()
        }

    val asteroidsAllSaved: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the videos for use, observe [asteroids]
     */
    suspend fun refreshAsteroids() {
        if (isNetworkAvailable(context)) {
            withContext(Dispatchers.IO) {
                val calendar = Calendar.getInstance()
                val currentTime = calendar.time
                val dateFormat = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.getDefault())
                val todayFormatted = dateFormat.format(currentTime)
                val asteroidList = parseAsteroidsJsonResult(
                    JSONObject(
                        AsteroidApi.retrofitScalarService.getAsteroids(API_KEY, todayFormatted)
                    )
                ).toList()
                database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
                Log.i("AsteroidsRepository", "Asteroids added to database")
            }
        } else {
            Log.i("AsteroidsRepository", "No internet available, asteroids not updated")
        }
    }

    suspend fun deletePreviousDayAsteroids() {
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = calendar.time
            val dateFormat = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.getDefault())
            val yesterdayFormatted = dateFormat.format(yesterday)
            database.asteroidDao.deletePreviousDayAsteroids(yesterdayFormatted)
            Log.i("AsteroidsRepository", "Previous day asteroids deleted from database")
        }
    }
}