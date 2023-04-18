package com.example.clothingsuggester.model

import androidx.annotation.DrawableRes

data class Bottom(@DrawableRes val bottomResId: Int, val weather: WEATHER, var isSelected: Boolean = false, var isOld: Boolean = false)