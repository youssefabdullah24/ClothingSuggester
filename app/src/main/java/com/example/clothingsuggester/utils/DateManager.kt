package com.example.clothingsuggester.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object DateManager {
    fun getDaysBetween(firstDateTime: LocalDateTime, secondDateTime: LocalDateTime): Long {
        return ChronoUnit.DAYS.between(firstDateTime, secondDateTime)
    }

    fun getDate(): LocalDateTime = LocalDateTime.now()

    fun getDateFromTimestamp(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timestamp),
            ZoneId.systemDefault()
        )
    }
}