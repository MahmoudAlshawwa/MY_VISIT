package com.example.myvisit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_splash_screen.*


private lateinit var current: LatLng
private var granted = false

class SplashScreen : AppCompatActivity() {
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Glide.with(applicationContext).load(R.drawable.location).into(location_im)
        startAnim()
        Handler(Looper.myLooper()!!).postDelayed({

            Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {

                        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            checkGPSEnable()

                            Log.e("hzm", "checkGPSEnable()")
                        } else {
                            getLastLocation()
                            stopAnim()

                            var t = Thread({
                                // Thread.sleep(2000)
                                val i = Intent(applicationContext, MainActivity::class.java)
                                startActivity(i)
                                finish()
                            })
                            t.start()



                        }
                    }


                    override fun onPermissionDenied(response: PermissionDeniedResponse) {

                        Log.e("hzm", "Acciess denied")
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token!!.continuePermissionRequest()
                    }
                }).check()

        }, 2000)

    }

    override fun onRestart() {
        super.onRestart()
        if (granted) {
            stopAnim()

            val i = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(i)
            finish()
        }

    }

    fun startAnim() {
        avi.show()
    }

    fun stopAnim() {
        avi.hide()
    }


    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.e("hzm", " locationServices here")
            return
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
            var location: Location? = task.result
            if (location == null) {
                requestNewLocationData()
            } else {
                current = LatLng(location.latitude, location.longitude)
                Log.e("hzm", current.toString())
                val sharedPreference = getSharedPreferences("sh_app", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("latitude", location.latitude.toString())
                editor.putString("longitude", location.longitude.toString())
                editor.commit()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            current = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            Log.e("hzm", current.toString())
            val sharedPreference = getSharedPreferences("sh_app", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("latitude", mLastLocation.latitude.toString())
            editor.putString("longitude", mLastLocation.longitude.toString())
            editor.commit()
        }
    }

    private fun checkGPSEnable() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id
                ->
                granted = true
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
                Toast.makeText(this, "Turn on GPS ...", Toast.LENGTH_LONG).show()
                checkGPSEnable()

            })
        val alert = dialogBuilder.create()
        alert.show()
    }


}

