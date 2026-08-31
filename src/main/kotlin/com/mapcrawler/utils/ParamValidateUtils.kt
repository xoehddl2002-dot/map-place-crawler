package com.mapcrawler.utils

object ParamValidateUtils {

    fun isValidateCode(code: String): Boolean {
        return code.length in 8..10 && code.all(Char::isDigit)
    }

    fun isValidateRcode(rcode: String): Boolean {
        return rcode.length in 5..10 && rcode.all(Char::isDigit)
    }

    fun isValidLatitude(value: String): Boolean =
        value.toDoubleOrNull()?.let { it in -90.0..90.0 } == true

    fun isValidLongitude(value: String): Boolean =
        value.toDoubleOrNull()?.let { it in -180.0..180.0 } == true

    fun normalizeQuery(query: String?, maxLength: Int = 100): String? =
        query?.trim()?.takeIf { it.isNotEmpty() && it.length <= maxLength }

    private val rcodeTypeMap = mapOf(
        "type1" to "SAVED_RANK",
        "type2" to "HOTSPOT",
        "type3" to "JUST_OPENED",
        "type4" to "SAVED_RANK,HOTSPOT,JUST_OPENED",
    )

    fun getRcodeType(typeId: String): String? {
        return rcodeTypeMap[typeId]
    }

    private val topicCodeMap = mapOf(
        "topic1" to "700005",//최신오픈
        "topic2" to "701001",//분위기좋은
        "topic3" to "700000",//TV출연맛집
        "topic4" to "700006",//착한가격
        "topic5" to "700008",//24시간영업
        "topic6" to "700012",//혼밥혼술
        "topic7" to "700007",//고급정찬
        "topic8" to "700010",//데이트맛집
    )

    fun getTopicCode(topicKey: String): String? {
        return topicCodeMap[topicKey]
    }
}
