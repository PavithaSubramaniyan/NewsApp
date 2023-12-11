package com.example.zohoproject.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.zohoproject.R
import com.example.zohoproject.apiService.RetrofitService
import com.example.zohoproject.constant.Constants
import com.example.zohoproject.pojo.AirQualityResponse
import com.example.zohoproject.pojo.WeatherResponse
import com.github.matteobattilana.weather.PrecipType
import com.github.matteobattilana.weather.WeatherView

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WeatherActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var temperatureTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var airQualityTextView: TextView
    private lateinit var climateImageView: ImageView
    private lateinit var newsFeedButton:Button
    private lateinit var progressBar: ProgressBar


    private lateinit var weatherView: WeatherView
    private val WEATHER_API_KEY = "6bf99c8abc99081a9a7838829d96ee1e"
    private var weatherApiCalled = false
    private lateinit var graphicsLayout: MotionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        locationTextView = findViewById(R.id.locationTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        weatherView = findViewById(R.id.weather_view)
        airQualityTextView = findViewById(R.id.airQualityTextView)
        newsFeedButton = findViewById(R.id.newsFeedButton)
        climateImageView = findViewById(R.id.weatherIconImageView)
        graphicsLayout = findViewById(R.id.graphicsLayout)
        progressBar = findViewById(R.id.progressBar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()

        newsFeedButton.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        climateImageView.setOnClickListener {
            Handler().postDelayed({
            climateImageView.visibility = View.GONE
            graphicsLayout.visibility = View.VISIBLE
            graphicsLayout.transitionToEnd()
            graphicsLayout.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                    // Transition started
                }

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                    // Transition changing
                }

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    // Transition completed
                }

                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                    // Transition triggered
                }
            })
        }, 100)
        }
        graphicsLayout.setOnClickListener {
            climateImageView.visibility = View.VISIBLE
            graphicsLayout.visibility = View.GONE
        }

    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (!weatherApiCalled) {
                fetchLocationAndData()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun fetchLocationAndData() {
        progressBar.visibility = View.VISIBLE
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    if (!weatherApiCalled) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val weatherResponse = RetrofitService.weatherApi.getWeather(
                                    it.latitude,
                                    it.longitude,
                                    WEATHER_API_KEY
                                )
                                withContext(Dispatchers.Main) {
                                    handleWeatherResponse(weatherResponse)
                                    weatherApiCalled = true
                                }
                                progressBar.visibility = View.GONE
                            } catch (e: Exception) {
                                e.printStackTrace()
                                progressBar.visibility = View.GONE
                            }
                        }
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val airQualityResponse = RetrofitService.airQualityApi.getAirQuality()
                            withContext(Dispatchers.Main) {
                                handleAirQualityResponse(airQualityResponse)
                            }
                            progressBar.visibility = View.GONE

                        } catch (e: Exception) {
                            e.printStackTrace()
                            progressBar.visibility = View.GONE

                        }
                    }
                } ?: run {
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                progressBar.visibility = View.GONE
            }
    }
    private fun handleWeatherResponse(response: WeatherResponse?) {
        if (response != null) {
            val temperature = response.main?.temp
            val description = response.weather?.firstOrNull()?.description
            val location = response.name
            val weatherId = response.weather?.firstOrNull()?.id
            runOnUiThread {
                temperatureTextView.text = "Temperature: $temperature°C"
                descriptionTextView.text = "Description: $description"
                locationTextView.text = "Location: $location"
                updateWeatherView(weatherId)

            }
        } else {
            runOnUiThread {
                Constants.showToast(applicationContext,"Failed to fetch weather data" )
            }
        }
    }
    private fun handleAirQualityResponse(response: AirQualityResponse?) {
        if (response != null) {
            val firstParameter = response.results?.firstOrNull()
            val airQuality = firstParameter?.name

            runOnUiThread {
                airQualityTextView.text = "Air Quality: $airQuality"
            }
        } else {
            runOnUiThread {
                Constants.showToast(applicationContext, "Failed to fetch air quality data")
            }
        }
    }

private fun updateWeatherView(weatherCode: Int?) {

    when (weatherCode) {
        in 200..232 -> {
            weatherView.setWeatherData(PrecipType.RAIN)
            climateImageView.setImageResource(R.drawable.rain)
        }
        in 300..531 -> {
            weatherView.setWeatherData(PrecipType.RAIN)
            climateImageView.setImageResource(R.drawable.rain)
        }
        in 600..622 -> {
            weatherView.setWeatherData(PrecipType.SNOW)
            climateImageView.setImageResource(R.drawable.snow)
        }
        800 -> {
            weatherView.setWeatherData(PrecipType.CLEAR)
            climateImageView.setImageResource(R.drawable.clouds)

        }
        else -> {
            weatherView.setWeatherData(PrecipType.CLEAR)
            climateImageView.setImageResource(R.drawable.clouds)
        }
    }
}

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
    override fun onResume() {
        super.onResume()
        weatherApiCalled = false
    }
}