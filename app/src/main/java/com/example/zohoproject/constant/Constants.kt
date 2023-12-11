package com.example.zohoproject.constant

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import android.net.ConnectivityManager

class Constants {
    companion object{
        const val WEATHER_URL = "https://api.openweathermap.org/data/2.5/"
        const val AIRQUALITY_URL = "https://api.openaq.org/v2/"
        fun showToast(applicationContext: Context, message: String) {

            val toast = Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_LONG
            )
            toast.show()

            val handler = Handler()
            handler.postDelayed({ toast.cancel() }, 2000)
        }
    }

}