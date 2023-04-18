package com.example.clothingsuggester.model

import androidx.annotation.DrawableRes

data class Top(@DrawableRes val topResId: Int, val weather: WEATHER, var isSelected: Boolean = false, var isOld: Boolean = false)