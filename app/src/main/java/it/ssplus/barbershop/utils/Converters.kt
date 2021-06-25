package it.ssplus.barbershop.utils

import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    private val df = SimpleDateFormat("d/M/yyyy")

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun dateToTime(value: Date?): String? {
        return if (value != null) {
            df.format(value)
        } else {
            null
        }
    }


}