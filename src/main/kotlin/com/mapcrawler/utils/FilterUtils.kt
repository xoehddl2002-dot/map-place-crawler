package com.mapcrawler.utils

object FilterUtils {
    fun filteringInvalidChar_tel(tel: String?): String {
        return tel?.filter(Char::isDigit).orEmpty()
    }

    fun filteringInvalidChar(str: String?): String {
        return str
            ?.replace(Regex("[\\p{Cc}\\p{Cf}]"), " ")
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            .orEmpty()
    }
}
