package com.mapcrawler.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ParamValidateUtilsTest {

    @Test
    fun `장소 코드는 8자에서 10자까지 허용한다`() {
        assertTrue(ParamValidateUtils.isValidateCode("12345678"))
        assertTrue(ParamValidateUtils.isValidateCode("1234567890"))
        assertFalse(ParamValidateUtils.isValidateCode("1234567"))
        assertFalse(ParamValidateUtils.isValidateCode("12345678901"))
        assertFalse(ParamValidateUtils.isValidateCode("1234567a"))
    }

    @Test
    fun `행정구역 코드는 5자 이상이어야 한다`() {
        assertTrue(ParamValidateUtils.isValidateRcode("11680"))
        assertFalse(ParamValidateUtils.isValidateRcode("1168"))
        assertFalse(ParamValidateUtils.isValidateRcode("1168a"))
    }

    @Test
    fun `외부에 노출되는 타입 ID 를 내부 코드로 변환한다`() {
        assertEquals("SAVED_RANK", ParamValidateUtils.getRcodeType("type1"))
        assertEquals("SAVED_RANK,HOTSPOT,JUST_OPENED", ParamValidateUtils.getRcodeType("type4"))
        assertNull(ParamValidateUtils.getRcodeType("unknown"))
    }

    @Test
    fun `외부에 노출되는 토픽 ID 를 내부 코드로 변환한다`() {
        assertEquals("700005", ParamValidateUtils.getTopicCode("topic1"))
        assertNull(ParamValidateUtils.getTopicCode("topic99"))
    }

    @Test
    fun `좌표 범위와 검색어 길이를 검증한다`() {
        assertTrue(ParamValidateUtils.isValidLatitude("37.5"))
        assertFalse(ParamValidateUtils.isValidLatitude("91"))
        assertTrue(ParamValidateUtils.isValidLongitude("127.0"))
        assertFalse(ParamValidateUtils.isValidLongitude("181"))
        assertEquals("강남역 카페", ParamValidateUtils.normalizeQuery("  강남역 카페  "))
        assertNull(ParamValidateUtils.normalizeQuery(" "))
        assertNull(ParamValidateUtils.normalizeQuery("a".repeat(101)))
    }
}
