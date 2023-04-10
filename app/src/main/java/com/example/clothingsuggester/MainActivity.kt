package com.example.clothingsuggester

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.clothingsuggester.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        getLocationInfo("30.033333", "31.233334")


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
                        binding.apply {
                            locationTextView.text = getString(R.string.current_location, city, area, country)
                            temperatureTv.text = getString(R.string.current_temperature, temp)
                            highLowTextView.text = getString(R.string.high_low_temperature, max, min, feelsLike)
                            weatherTextTv.text = weatherText
                            root.background = ResourcesCompat.getDrawable(
                                resources, if (isDayTime) R.drawable.background_day else R.drawable.background_night, null
                            )
                            weatherIconImageView.setImageResource(iconResource)
                        }
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

        })
    }

    private fun updateOutfit(weather: WEATHER) {
        val prefs = getSharedPreferences(KEY_PREF, Activity.MODE_PRIVATE)
        val currentOutfitId = prefs.getInt(KEY_PREF_OUTFIT_ID, -1)
        val timestamp = prefs.getLong(KEY_PREF_TIMESTAMP, -1)
        if (currentOutfitId != -1 && timestamp != -1L) {
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
                val suitableOutfit = clothes.filter { it.weather == weather && it.id == currentOutfitId }[0]
                binding.apply {
                    topImageView.setImageResource(suitableOutfit.topResId)
                    bottomImageView.setImageResource(suitableOutfit.bottomResId)
                    recommendedOutfitTextView.visibility = View.VISIBLE
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
        binding.apply {
            topImageView.setImageResource(suitableOutfit.topResId)
            bottomImageView.setImageResource(suitableOutfit.bottomResId)
            recommendedOutfitTextView.visibility = View.VISIBLE
        }
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