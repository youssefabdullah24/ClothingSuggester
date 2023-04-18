package com.example.clothingsuggester.interactor

import com.example.clothingsuggester.model.City
import com.example.clothingsuggester.model.WeatherCondition
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object WeatherInteractor {
    private const val HOST = "dataservice.accuweather.com"
    private const val PATH_SEARCH = "locations//v1//cities//geoposition//search"
    private const val PATH_CONDITION = "currentconditions//v1"
    private const val APIKEY = "WUK60M7WW3ekJX5BoqJhqXskVo4zkKxm"

    fun getCityInfo(cityInfoCallback: CityInfoCallback) {
        val r = Request.Builder().url(
            HttpUrl.Builder().scheme("http").host(HOST).addPathSegments(PATH_SEARCH)
                .addQueryParameter("apikey", APIKEY).addQueryParameter("q", "30.033333,31.233334")
                .build()
        ).build()
        val call = OkHttpClient().newCall(r)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cityInfoCallback.onCityInfoFailure(e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val areaJsonObject = if (body != null) JSONObject(body) else null
                if (areaJsonObject != null) {
                    val cityKey = areaJsonObject.getString("Key")
                    val cityName = areaJsonObject.getString("EnglishName")
                    val countryName = areaJsonObject.getJSONObject("Country").getString("EnglishName")
                    val areaName = areaJsonObject.getJSONObject("AdministrativeArea").getString("EnglishName")
                    val city = City(cityKey, cityName, countryName, areaName)
                    cityInfoCallback.onCityInfoSuccess(city)
                }

            }

        })

    }


    fun getWeatherInfo(city:City, weatherInfoCallback: WeatherInfoCallback) {
        val r = Request.Builder().url(
            HttpUrl.Builder().scheme("http").host(HOST)
                .addPathSegments(PATH_CONDITION + "//${city.cityKey}")
                .addQueryParameter("apikey", APIKEY).addQueryParameter("details", "true").build()
        ).build()
        val client = OkHttpClient()
        val call = client.newCall(r)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                weatherInfoCallback.onWeatherInfoFailure(e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val weatherConditionJson = if (body != null) JSONArray(body).getJSONObject(0) else null
                if (weatherConditionJson != null) {
                    val weatherText = weatherConditionJson.getString("WeatherText")
                    val weatherIconCode = weatherConditionJson.getInt("WeatherIcon")
                    val isDayTime = weatherConditionJson.getBoolean("IsDayTime")
                    val temp = weatherConditionJson.getJSONObject("Temperature").getJSONObject("Metric")
                        .getDouble("Value").toInt()
                    val feelsLike = weatherConditionJson.getJSONObject("RealFeelTemperature")
                        .getJSONObject("Metric").getDouble("Value").toInt()
                    val minMax = weatherConditionJson.getJSONObject("TemperatureSummary")
                        .getJSONObject("Past24HourRange")
                    val min =
                        minMax.getJSONObject("Minimum").getJSONObject("Metric").getDouble("Value")
                            .toInt()
                    val max =
                        minMax.getJSONObject("Maximum").getJSONObject("Metric").getDouble("Value")
                            .toInt()
                    val weatherCondition = WeatherCondition(
                        weatherText,
                        weatherIconCode,
                        isDayTime,
                        temp,
                        feelsLike,
                        min,
                        max
                    )
                    weatherInfoCallback.onWeatherInfoSuccess(city,weatherCondition)
                }
            }
        })
    }
}

interface CityInfoCallback {
    fun onCityInfoSuccess(city: City)
    fun onCityInfoFailure(message: String)
}

interface WeatherInfoCallback {
    fun onWeatherInfoSuccess(city: City,weatherCondition: WeatherCondition)
    fun onWeatherInfoFailure(message: String)
}
