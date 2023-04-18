package com.example.clothingsuggester.model

data class WeatherCondition(
    val weatherText: String,
    val weatherIconCode: Int,
    val isDayTime: Boolean,
    val temp: Int,
    val feelsLike: Int,
    val min: Int,
    val max: Int
)