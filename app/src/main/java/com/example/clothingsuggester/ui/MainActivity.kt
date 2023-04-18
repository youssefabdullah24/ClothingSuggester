package com.example.clothingsuggester.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.clothingsuggester.R
import com.example.clothingsuggester.adapter.BottomAdapter
import com.example.clothingsuggester.adapter.TopAdapter
import com.example.clothingsuggester.databinding.ActivityMainBinding
import com.example.clothingsuggester.interactor.CityInfoCallback
import com.example.clothingsuggester.interactor.WeatherInfoCallback
import com.example.clothingsuggester.interactor.WeatherInteractor
import com.example.clothingsuggester.model.Bottom
import com.example.clothingsuggester.model.City
import com.example.clothingsuggester.model.Top
import com.example.clothingsuggester.model.WEATHER
import com.example.clothingsuggester.model.WeatherCondition
import com.example.clothingsuggester.model.WeatherIcon
import com.example.clothingsuggester.utils.Constants
import com.example.clothingsuggester.utils.DateManager
import com.example.clothingsuggester.utils.PrefsManager


class MainActivity : AppCompatActivity(), CityInfoCallback, WeatherInfoCallback {
    private lateinit var binding: ActivityMainBinding

    private lateinit var prefsManager: PrefsManager
    private lateinit var weatherIcons: ArrayList<WeatherIcon>
    private lateinit var topAdapter: TopAdapter
    private lateinit var bottomAdapter: BottomAdapter
    private lateinit var tops: ArrayList<Top>
    private lateinit var bottoms: ArrayList<Bottom>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        WeatherInteractor.getCityInfo(this)


    }

    private fun initData() {
        prefsManager = PrefsManager(getSharedPreferences(Constants.KEY_PREF, MODE_PRIVATE))
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
        tops = arrayListOf(
            Top(R.drawable.summertshirt1, WEATHER.HOT),
            Top(R.drawable.summertshirt2, WEATHER.HOT),
            Top(R.drawable.autumnshirt1, WEATHER.MODERATE),
            Top(R.drawable.autumnshirt2, WEATHER.MODERATE),
            Top(R.drawable.winterjacket1, WEATHER.COLD),
            Top(R.drawable.winterjacket2, WEATHER.COLD)
        )
        bottoms = arrayListOf(
            Bottom(R.drawable.summerpants1, WEATHER.HOT),
            Bottom(R.drawable.summerpants2, WEATHER.HOT),
            Bottom(R.drawable.autumnpants1, WEATHER.MODERATE),
            Bottom(R.drawable.autumnpants2, WEATHER.MODERATE),
            Bottom(R.drawable.winterpants1, WEATHER.COLD),
            Bottom(R.drawable.winterpants2, WEATHER.COLD)
        )
        topAdapter = TopAdapter()
        bottomAdapter = BottomAdapter()
        binding.topRecyclerView.adapter = topAdapter
        binding.bottomRecyclerView.adapter = bottomAdapter
        topAdapter.submitList(tops)
        bottomAdapter.submitList(bottoms)

    }

    private fun updateOutfitBasedOnWeather(weather: WEATHER) {
        /* var savedWeatherOrdinal: Int
         var savedTopId: Int
         var savedBottomId: Int
         var savedOldTopId: Int
         var savedOldBottomId: Int
         var savedTimestamp: Long
         prefs.apply {
             savedWeatherOrdinal = getInt(KEY_PREF_OUTFIT_WEATHER, -1)
             savedTimestamp = getLong(KEY_PREF_TIMESTAMP, -1L)
             savedTopId = getInt(KEY_PREF_OUTFIT_TOP_ID, -1)
             savedBottomId = getInt(KEY_PREF_OUTFIT_BOTTOM_ID, -1)
             savedOldTopId = getInt(KEY_PREF_OUTFIT_TOP_OLD_ID, -1)
             savedOldBottomId = getInt(KEY_PREF_OUTFIT_BOTTOM_OLD_ID, -1)
         }*/
        if (prefsManager.savedWeatherOrdinal != weather.ordinal) {
            val top = tops.find { it.weather == weather }!!
            val bottom = bottoms.find { it.weather == weather }!!
            updateOutfit(
                top,
                bottom,
                prefsManager.savedOldTopId,
                prefsManager.savedOldBottomId
            )
            prefsManager.saveOutfit(top, bottom, weather)
        } else {
            if (prefsManager.savedTimestamp != -1L
                && prefsManager.savedTopId != -1
                && prefsManager.savedBottomId != -1
            ) {
                val currentDate = DateManager.getDate()
                val oldDate = DateManager.getDateFromTimestamp(prefsManager.savedTimestamp)
                val diff = DateManager.getDaysBetween(currentDate, oldDate)
                if (diff >= 1) {
                    // different day
                    val newTop = tops.find {
                        it.topResId != prefsManager.savedTopId
                                && it.weather == weather
                    }!!
                    val newBottom = bottoms.find {
                        it.bottomResId != prefsManager.savedBottomId
                                && it.weather == weather
                    }!!
                    updateOutfit(
                        newTop,
                        newBottom,
                        prefsManager.savedOldTopId,
                        prefsManager.savedOldBottomId
                    )
                    prefsManager.saveOutfit(newTop, newBottom, weather)
                } else {
                    // same day
                    val top = tops.find { it.topResId == prefsManager.savedTopId }!!
                    val bottom = bottoms.find { it.bottomResId == prefsManager.savedBottomId }!!
                    updateOutfit(
                        top,
                        bottom,
                        prefsManager.savedOldTopId,
                        prefsManager.savedOldBottomId
                    )
                }
            } else {
                // no data.
                val top = tops.find { it.weather == weather }!!
                val bottom = bottoms.find { it.weather == weather }!!
                updateOutfit(
                    top,
                    bottom,
                    prefsManager.savedOldTopId,
                    prefsManager.savedOldBottomId
                )
                prefsManager.saveOutfit(top, bottom, weather)
            }
        }
    }

    private fun updateOutfit(top: Top, bottom: Bottom, oldTopId: Int, oldBottomId: Int) {
        runOnUiThread {
            val topItem = tops.find { it.topResId == top.topResId }!!
            val bottomItem = bottoms.find { it.bottomResId == bottom.bottomResId }!!

            topItem.isSelected = true
            bottomItem.isSelected = true
            if (oldTopId != -1 && oldBottomId != -1) {
                val oldTopItem = tops.find { it.topResId == oldTopId }!!
                val oldBottomItem = bottoms.find { it.bottomResId == oldBottomId }!!
                oldTopItem.isOld = true
                oldBottomItem.isOld = true
            }
            topAdapter.submitList(ArrayList(tops))
            bottomAdapter.submitList(ArrayList(bottoms))
            binding.apply {
                topRecyclerView.adapter = topAdapter
                bottomRecyclerView.adapter = bottomAdapter
                topRecyclerView.smoothScrollToPosition(tops.indexOf(topItem))
                bottomRecyclerView.smoothScrollToPosition(bottoms.indexOf(bottomItem))
            }
        }
    }

    private fun initViews(city: City, weatherCondition: WeatherCondition) {
        val iconResource = weatherIcons.find { it.iconValue == weatherCondition.weatherIconCode }!!.iconResId
        runOnUiThread {
            binding.apply {
                locationTextView.text =
                    getString(R.string.current_location, city.cityName, city.areaName, city.countryName)
                temperatureTv.text = getString(R.string.current_temperature, weatherCondition.temp)
                highLowTextView.text =
                    getString(R.string.high_low_temperature, weatherCondition.max, weatherCondition.min, weatherCondition.feelsLike)
                weatherTextTv.text = weatherCondition.weatherText
                root.background = ResourcesCompat.getDrawable(
                    resources,
                    if (weatherCondition.isDayTime) R.drawable.background_day else R.drawable.background_night,
                    null
                )
                weatherIconImageView.setImageResource(iconResource)
            }
        }
        when (weatherCondition.temp) {
            in Int.MIN_VALUE..10 -> {
                updateOutfitBasedOnWeather(WEATHER.COLD)
            }

            in 11..20 -> {
                updateOutfitBasedOnWeather(WEATHER.MODERATE)
            }

            in 21..Int.MAX_VALUE -> {
                updateOutfitBasedOnWeather(WEATHER.HOT)
            }
        }

    }

    override fun onCityInfoSuccess(city: City) {
        WeatherInteractor.getWeatherInfo(city, this)
    }

    override fun onCityInfoFailure(message: String) {
        Log.d(TAG, "onCityInfoFailure: $message")
    }

    override fun onWeatherInfoSuccess(city: City, weatherCondition: WeatherCondition) {
        initViews(city, weatherCondition)

    }

    override fun onWeatherInfoFailure(message: String) {
        Log.d(TAG, "onWeatherInfoFailure: $message")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}