package fr.papycha.htcweatherclock.clock

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ClockFormatter {
    fun time(epochMillis: Long, use24Hour: Boolean, locale: Locale, zone: TimeZone): String = SimpleDateFormat(if (use24Hour) "HH:mm" else "hh:mm", locale).apply { timeZone = zone }.format(Date(epochMillis))
    fun date(epochMillis: Long, locale: Locale, zone: TimeZone): String = SimpleDateFormat("EEE d MMM", locale).apply { timeZone = zone }.format(Date(epochMillis))
}
