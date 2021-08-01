package it.ssplus.barbershop.utils

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun dateToTime(value: Date?): String? {
        return if (value != null) {
            formatDate(value)
        } else {
            null
        }
    }
}