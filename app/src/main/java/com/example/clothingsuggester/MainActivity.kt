package com.example.clothingsuggester

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.example.clothingsuggester.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit


class MainActivity : AppCompatActivity() {
    private lateinit var weatherIcons: ArrayList<WeatherIcon>
    private lateinit var clothes: ArrayList<Cloth>
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val requestPermsContent = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms: Map<String, Boolean> ->
        if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true || perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            checkLocationOptionsAndGetUserCoordinates()
        } else {
            AlertDialog.Builder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                .setTitle("Location permission is needed")
                .setPositiveButton("OK") { dialog, p1 ->
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                    dialog.dismiss()

                }.setNegativeButton("Dismiss") { dialog, p1 ->
                    dialog.dismiss()
                }.show()
        }
    }
    private val locationsServicesContent =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                getUserCoordinates()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initData()
        checkLocationOptionsAndGetUserCoordinates()


    }

    private fun initData() {
        weatherIcons = ArrayList()
        weatherIcons.apply {
            add(WeatherIcon(R.drawable.i01, 1))
            add(WeatherIcon(R.drawable.i02, 2))
            add(WeatherIcon(R.drawable.i03, 3))
            add(WeatherIcon(R.drawable.i04, 4))
            add(WeatherIcon(R.drawable.i05, 5))
            add(WeatherIcon(R.drawable.i06, 6))
            add(WeatherIcon(R.drawable.i07, 7))
            add(WeatherIcon(R.drawable.i08, 8))
            add(WeatherIcon(R.drawable.i11, 11))
            add(WeatherIcon(R.drawable.i12, 12))
            add(WeatherIcon(R.drawable.i13, 13))
            add(WeatherIcon(R.drawable.i14, 14))
            add(WeatherIcon(R.drawable.i15, 15))
            add(WeatherIcon(R.drawable.i16, 16))
            add(WeatherIcon(R.drawable.i17, 17))
            add(WeatherIcon(R.drawable.i18, 18))
            add(WeatherIcon(R.drawable.i19, 19))
            add(WeatherIcon(R.drawable.i20, 20))
            add(WeatherIcon(R.drawable.i21, 21))
            add(WeatherIcon(R.drawable.i22, 22))
            add(WeatherIcon(R.drawable.i23, 23))
            add(WeatherIcon(R.drawable.i24, 24))
            add(WeatherIcon(R.drawable.i25, 25))
            add(WeatherIcon(R.drawable.i26, 26))
            add(WeatherIcon(R.drawable.i29, 29))
            add(WeatherIcon(R.drawable.i30, 30))
            add(WeatherIcon(R.drawable.i31, 31))
            add(WeatherIcon(R.drawable.i32, 32))
            add(WeatherIcon(R.drawable.i33, 33))
            add(WeatherIcon(R.drawable.i34, 34))
            add(WeatherIcon(R.drawable.i35, 35))
            add(WeatherIcon(R.drawable.i36, 36))
            add(WeatherIcon(R.drawable.i37, 37))
            add(WeatherIcon(R.drawable.i38, 38))
            add(WeatherIcon(R.drawable.i39, 39))
            add(WeatherIcon(R.drawable.i40, 40))
            add(WeatherIcon(R.drawable.i41, 41))
            add(WeatherIcon(R.drawable.i42, 42))
            add(WeatherIcon(R.drawable.i43, 43))
            add(WeatherIcon(R.drawable.i44, 44))
        }


        clothes = ArrayList()
        clothes.apply {
            add(Cloth(0, R.drawable.summertshirt1, R.drawable.summerpants1, WEATHER.HOT))
            add(Cloth(1, R.drawable.summertshirt2, R.drawable.summerpants2, WEATHER.HOT))
            add(Cloth(0, R.drawable.autumnshirt1, R.drawable.autumnpants1, WEATHER.MODERATE))
            add(Cloth(1, R.drawable.autumnshirt2, R.drawable.autumnpants2, WEATHER.MODERATE))
            add(Cloth(0, R.drawable.winterjacket1, R.drawable.winterpants1, WEATHER.COLD))
            add(Cloth(1, R.drawable.winterjacket2, R.drawable.winterpants2, WEATHER.COLD))


        }

    }

    @SuppressLint("MissingPermission")
    private fun getUserCoordinates() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
        } else {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (location != null) {
            val lat = location.latitude.toString()
            val lon = location.longitude.toString()
            getLocationInfo(lat, lon)
        }


    }

    private fun checkLocationOptionsAndGetUserCoordinates() {
        if (isLocationPermissionGranted()) {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
            val locationSettingsBuilder =
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val locationServices = LocationServices.getSettingsClient(this)
            locationServices.checkLocationSettings(locationSettingsBuilder.build()).addOnCompleteListener {
                if (it.isSuccessful) {
                    getUserCoordinates()
                }
            }.addOnFailureListener {
                if (it is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(it.resolution).build()
                        locationsServicesContent.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                    }
                } else {
                    AlertDialog.Builder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                        .setTitle("Location permission is needed")
                        .setPositiveButton("OK") { dialog, p1 ->
                            checkLocationOptionsAndGetUserCoordinates()
                            dialog.dismiss()
                        }.setNegativeButton("Dismiss") { dialog, p1 ->
                            dialog.dismiss()
                        }.show()
                }
            }
        } else {
            requestPermsContent.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }

    }

    private fun isLocationPermissionGranted() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun getLocationInfo(lat: String, lon: String) {
        val r = Request.Builder().url(
            HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .addPathSegments(PATH_SEARCH)
                .addQueryParameter("apikey", APIKEY)
                .addQueryParameter("q", "$lat,$lon")
                .build()
        )
            .build()
        val client = OkHttpClient()
        val call = client.newCall(r)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: ${e.message}", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val areaJsonObject = if (body != null) JSONObject(body) else null
                if (areaJsonObject != null) {
                    getWeatherConditionAndInitViews(areaJsonObject)
                }
            }
        })
    }

    private fun getWeatherConditionAndInitViews(areaJsonObject: JSONObject) {
        val cityKey = areaJsonObject.getString("Key")
        val city = areaJsonObject.getString("EnglishName")
        val country = areaJsonObject.getJSONObject("Country").getString("EnglishName")
        val area = areaJsonObject.getJSONObject("AdministrativeArea").getString("EnglishName")

        val r = Request.Builder().url(
            HttpUrl.Builder()
                .scheme("http")
                .host(HOST)
                .addPathSegments(PATH_CONDITION + "//${cityKey}")
                .addQueryParameter("apikey", APIKEY)
                .addQueryParameter("details", "true")
                //.addQueryParameter("q", cityKey)
                .build()
        )
            .build()
        val client = OkHttpClient()
        val call = client.newCall(r)
        runOnUiThread { }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: ${e.message}", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val weatherCondition = if (body != null) JSONArray(body).getJSONObject(0) else null
                if (weatherCondition != null) {
                    val weatherText = weatherCondition.getString("WeatherText")
                    val weatherIcon = weatherCondition.getInt("WeatherIcon")
                    val isDayTime = weatherCondition.getBoolean("IsDayTime")
                    val temp = weatherCondition.getJSONObject("Temperature")
                        .getJSONObject("Metric")
                        .getDouble("Value")
                        .toInt()
                    val feelsLike = weatherCondition.getJSONObject("RealFeelTemperature")
                        .getJSONObject("Metric")
                        .getDouble("Value")
                        .toInt()
                    val humidity = weatherCondition.getInt("RelativeHumidity")
                    val wind = weatherCondition.getJSONObject("Wind")
                        .getJSONObject("Speed")
                        .getJSONObject("Metric")
                        .getDouble("Value")
                        .toInt()
                    val uvIndex = weatherCondition.getString("UVIndexText")
                    val minMax = weatherCondition.getJSONObject("TemperatureSummary")
                        .getJSONObject("Past24HourRange")
                    val min = minMax.getJSONObject("Minimum")
                        .getJSONObject("Metric")
                        .getDouble("Value")
                        .toInt()
                    val max = minMax.getJSONObject("Maximum")
                        .getJSONObject("Metric")
                        .getDouble("Value")
                        .toInt()
                    val iconResource = weatherIcons.find { it.iconValue == weatherIcon }!!.iconResId
                    runOnUiThread {
                        binding.contentMain.apply {
                            locationTextView.text = "${city}, ${area}, $country"
                            temperatureTv.text = "${temp}°C"
                            highLowTextView.text = "${max}°C / ${min}°C Feels like ${feelsLike}°C"
                            weatherTextTv.text = weatherText
                            root.background = ResourcesCompat.getDrawable(
                                resources, if (isDayTime) R.drawable.background_day else R.drawable.background_night, null
                            )
                            weatherIconImageView.setImageResource(iconResource)
                            when (temp) {
                                in Int.MIN_VALUE..10 -> {
                                    updateOutfit(WEATHER.COLD)
                                }

                                in 11..20 -> {
                                    updateOutfit(WEATHER.MODERATE)
                                }

                                in 21..Int.MAX_VALUE -> {
                                    updateOutfit(WEATHER.HOT)
                                }
                            }
                        }
                    }
                }
            }

        })
    }

    private fun updateOutfit(weather: WEATHER) {
        val prefs = getSharedPreferences(KEY_PREF, Activity.MODE_PRIVATE)
        val currentOutfitId = prefs.getInt(KEY_PREF_OUTFIT_ID, -1)
        if (currentOutfitId != -1) {
            val timestamp = prefs.getLong(KEY_PREF_TIMESTAMP, -1)
            if (timestamp != -1L) {
                val currentDate = LocalDate.now()
                val oldDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
                val diff = ChronoUnit.DAYS.between(currentDate, oldDate)
                if (diff >= 1) {
                    val condition = prefs.getInt(KEY_PREF_OUTFIT_WEATHER, -1)
                    if (condition == weather.ordinal) {
                        val newOutfitId = if (currentOutfitId == 0) 1 else 0 
                        val suitableOutfit = clothes.filter { it.weather == weather && it.id == newOutfitId }[0]
                        updateSavedOutfit(prefs, suitableOutfit)
                    } else {
                        val suitableOutfit = clothes.filter { it.weather == weather }[0]
                        updateSavedOutfit(prefs, suitableOutfit)
                    }
                } else {
                    val suitableOutfit = clothes.filter { it.weather == weather }[0]
                    updateSavedOutfit(prefs, suitableOutfit)
                }
            }
        } else {
            val suitableOutfit = clothes.filter { it.weather == weather }[0]
            updateSavedOutfit(prefs, suitableOutfit)

        }


    }

    private fun updateSavedOutfit(prefs: SharedPreferences, suitableOutfit: Cloth) {
        prefs.edit().putInt(KEY_PREF_OUTFIT_ID, suitableOutfit.id).apply()
        prefs.edit().putInt(KEY_PREF_OUTFIT_WEATHER, suitableOutfit.weather.ordinal).apply()
        prefs.edit().putLong(KEY_PREF_TIMESTAMP, Instant.now().epochSecond).apply()
        binding.contentMain.topImageView.setImageResource(suitableOutfit.topResId)
        binding.contentMain.bottomImageView.setImageResource(suitableOutfit.bottomResId)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val HOST = "dataservice.accuweather.com"
        private const val PATH_SEARCH = "locations//v1//cities//geoposition//search"
        private const val PATH_CONDITION = "currentconditions//v1"
        private const val APIKEY = "3V7YuVcFgMrZFt9oLUTJAoVaFu8IXX2C"
        private const val KEY_PREF = "prefs"
        private const val KEY_PREF_OUTFIT_ID = "prefs_outfit_id"
        private const val KEY_PREF_OUTFIT_WEATHER = "prefs_outfit_weather"
        private const val KEY_PREF_TIMESTAMP = "prefs_timestamp"
    }

}


data class WeatherIcon(
    @DrawableRes val iconResId: Int,
    val iconValue: Int
)

data class Cloth(
    val id: Int,
    @DrawableRes val topResId: Int,
    @DrawableRes val bottomResId: Int,
    val weather: WEATHER
)


enum class WEATHER {
    HOT,
    MODERATE,
    COLD
}