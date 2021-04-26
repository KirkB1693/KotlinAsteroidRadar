package com.example.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.example.asteroidradar.Asteroid
import com.example.asteroidradar.database.getDatabase
import com.example.asteroidradar.repository.AsteroidsRepository
import com.example.asteroidradar.repository.PictureOfTheDayRepository
import kotlinx.coroutines.launch

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database, application)
    private val pictureOfTheDayRepository = PictureOfTheDayRepository(application)

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()

    val navigateToSelectedAsteroid: LiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    init {
        _status.value = AsteroidApiStatus.LOADING
        viewModelScope.launch {
            pictureOfTheDayRepository.refreshPictureOfTheDay()
            asteroidsRepository.refreshAsteroids()
            _status.value = AsteroidApiStatus.DONE
        }
    }

    var asteroidsList = asteroidsRepository.asteroids
    val pictureOfTheDay = pictureOfTheDayRepository.pictureOfDay
    val pictureOfTheDayDrawablePath = pictureOfTheDayRepository.pictureOfTheDayDrawablePath


    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun showWeek() {
        asteroidsList = asteroidsRepository.asteroidsForWeek
    }

    fun showToday() {
        asteroidsList = asteroidsRepository.asteroidsForToday
    }

    fun showAllSaved() {
        asteroidsList = asteroidsRepository.asteroidsAllSaved
    }


}