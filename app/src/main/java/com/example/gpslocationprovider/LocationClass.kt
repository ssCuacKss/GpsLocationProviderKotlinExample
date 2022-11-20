package com.example.gpslocationprovider

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.location.*


class LocationClass(context: Context,latitude: TextView, longitude:TextView):AppCompatActivity() {

    private var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationManager: LocationManager
    private var locationRequest: LocationRequest
    private var thisContext: Context
    private var gpsStatus = false
    private var locationCallback: LocationCallback
    private var requestingUpdates: Boolean = false
    private val currentLocationRequest:CurrentLocationRequest
    var latitude: TextView
    var longitude: TextView

    init {
        thisContext = context
        this.latitude = latitude
        this.longitude = longitude

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        locationManager = thisContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationRequest= LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).apply{
            setMinUpdateDistanceMeters(0F)
            setIntervalMillis(2000)
            setMinUpdateIntervalMillis(1000)
            setGranularity(Granularity.GRANULARITY_FINE)
        }.build()

        currentLocationRequest = CurrentLocationRequest.Builder().apply {
            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            setMaxUpdateAgeMillis(500)
            setGranularity(Granularity.GRANULARITY_FINE)
        }.build()

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {

                for(location in locationResult.locations){
                    if(location?.latitude.toString()!="null" || location?.longitude.toString()!="null") {
                        latitude.text = location.latitude.toString()
                        longitude.text = location.longitude.toString()
                    }
                }
            }
        }


    }



    @SuppressLint("MissingPermission")
    fun getLocation() {
        setupPermissions()
        checkGpsStatus()


        fusedLocationProviderClient.getCurrentLocation(currentLocationRequest,null).addOnSuccessListener { location : Location ? ->

            if(location?.latitude.toString()!="null" || location?.longitude.toString()!="null"){
                latitude.text= location?.latitude.toString()
                longitude.text=location?.longitude.toString()
            }
        }
    }

    private fun setupPermissions(){
        if(ContextCompat.checkSelfPermission(thisContext,Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(thisContext as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),1)
        }
    }

    private fun checkGpsStatus(){
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!gpsStatus) {
            Toast.makeText(
                thisContext,
                "Los servicios de ubicación están desactivados",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(thisContext, intent, null)
        }

    }

    @SuppressLint("MissingPermission")
    fun startUpdates(){
        setupPermissions()
        checkGpsStatus()
        if(!requestingUpdates){
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
            requestingUpdates = true
        }
    }

    fun stopUpdates(){
        if(requestingUpdates) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            requestingUpdates = false
        }
    }

    // Estas dos funciones no creo que funcionen como deben al no pasarle a la clase el control como una activdad,

    public override fun onResume() {
        super.onResume()
        if(requestingUpdates) {
            startUpdates()
        }
    }

    public override fun onPause() {
        super.onPause()
        stopUpdates()
    }

}