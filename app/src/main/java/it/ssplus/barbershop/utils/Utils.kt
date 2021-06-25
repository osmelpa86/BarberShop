package it.ssplus.barbershop.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun getColorName(context: Context, pos: Int): String {
        return context.getString(Constants.colorNames[pos])
    }

    fun formatToCoinString(value: Float): String {
        return String.format("\$%.2f", value)
    }

    fun formatToCoinNumberString(value: Float): String {
        return String.format("%.2f", value)
    }

    fun formatDateToDatabaseString(date: Date): String {
        return SimpleDateFormat(Constants.DateFormats.database).format(date)
    }

    fun parseDateToDatabaseFormat(date: String): Date {
        return SimpleDateFormat(Constants.DateFormats.database).parse(date)
    }

    fun formatDateToDisplayString(date: Date): String {
        return SimpleDateFormat(Constants.DateFormats.display).format(date)
    }

    fun parseDateToDisplayFormat(date: String): Date {
        return SimpleDateFormat(Constants.DateFormats.display).parse(date)
    }
}