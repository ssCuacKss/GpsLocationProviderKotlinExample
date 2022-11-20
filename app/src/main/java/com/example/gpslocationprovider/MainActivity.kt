package com.example.gpslocationprovider

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val locator: LocationClass
        val latitude: TextView = findViewById(R.id.latitudeVal)
        val longitude: TextView = findViewById(R.id.longitudeVal)
        val clicker: Button = findViewById(R.id.getCoordinates)
        val start: Button = findViewById(R.id.start)
        val stop: Button = findViewById(R.id.stop)
        locator = LocationClass(this,latitude,longitude)

        clicker.setOnClickListener {
            locator.getLocation()
        }

        start.setOnClickListener {
            locator.startUpdates()
        }

        stop.setOnClickListener {
            locator.stopUpdates()
        }

    }

}


