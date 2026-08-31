package com.mapcrawler.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FilterUtilsTest {

    @Test
    fun `전화번호에서 하이픈과 공백을 제거한다`() {
        assertEquals("0212345678", FilterUtils.filteringInvalidChar_tel("02-1234-5678"))
        assertEquals("0212345678", FilterUtils.filteringInvalidChar_tel(" (02) 1234-5678 "))
    }

    @Test
    fun `전화번호가 null 이면 빈 문자열을 반환한다`() {
        assertEquals("", FilterUtils.filteringInvalidChar_tel(null))
    }

    @Test
    fun `장소명에 의미 있는 특수문자를 보존한다`() {
        assertEquals("스타벅스* 강남점!", FilterUtils.filteringInvalidChar("스타벅스* 강남점!"))
        assertEquals("Cafe #24", FilterUtils.filteringInvalidChar("Cafe #24"))
    }

    @Test
    fun `제어문자는 공백으로 바꾸고 주소 구분자는 보존한다`() {
        assertEquals("서울시, 강남구 역삼동 123-45", FilterUtils.filteringInvalidChar("서울시, 강남구\n역삼동 123-45"))
    }

    @Test
    fun `문자열이 null 이면 빈 문자열을 반환한다`() {
        assertEquals("", FilterUtils.filteringInvalidChar(null))
    }
}
