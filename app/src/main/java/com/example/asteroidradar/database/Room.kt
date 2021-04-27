package com.example.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import retrofit2.http.DELETE

private lateinit var INSTANCE : AsteroidsDatabase

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate >= :date ORDER BY closeApproachDate ASC")
    fun getAsteroids(date: String) : LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate = :date")
    fun getTodaysAsteroids(date: String) : LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate BETWEEN :today AND :seventhDay ORDER BY closeApproachDate ASC")
    fun getWeekOfAsteroids(today: String, seventhDay: String) : LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid ORDER BY closeApproachDate ASC")
    fun getAllAsteroids() : LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids : DatabaseAsteroid)

    @Query("DELETE FROM databaseasteroid WHERE closeApproachDate = :yesterday")
    fun deletePreviousDayAsteroids(yesterday : String)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}


fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java)
    {if (!::INSTANCE.isInitialized) {
        INSTANCE = Room.databaseBuilder(context.applicationContext,
            AsteroidsDatabase::class.java,
            "asteroids").build()
    }}
    return INSTANCE
}

