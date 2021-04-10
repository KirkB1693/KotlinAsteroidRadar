package com.example.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asteroidradar.api.AsteroidApi
import com.example.asteroidradar.api.parseAsteroidsJsonResult
import com.example.asteroidradar.Asteroid
import com.example.asteroidradar.Constants.API_KEY
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<AsteroidApiStatus>()

    val status : LiveData<AsteroidApiStatus>
        get() = _status


    private val  _asteroids = MutableLiveData<List<Asteroid>>()

    val asteroids : LiveData<List<Asteroid>>
        get() = _asteroids


    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()

    val navigateToSelectedAsteroid : LiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    init {
        getAsteroidsList()
    }

    private fun getAsteroidsList() {
        viewModelScope.launch {
            _status.value = AsteroidApiStatus.LOADING
            try {
                val response = AsteroidApi.retrofitScalarService.getAsteroids(API_KEY)
                Log.i("retrofitScalarService", response)
                _asteroids.value = parseAsteroidsJsonResult(JSONObject(response)).toList()
                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                _status.value = AsteroidApiStatus.ERROR
                _asteroids.value = ArrayList()
            }
        }
    }


    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}