package com.mapcrawler.exception

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()

    @Test
    fun `입력 오류는 실제 HTTP 400으로 변환한다`() {
        val response = handler.handleApiException(ApiException(HttpStatus.BAD_REQUEST, "invalid"))

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(400, response.body?.status)
    }

    @Test
    fun `외부 수집 오류는 상세 원인을 숨기고 HTTP 502로 변환한다`() {
        val response = handler.handleCrawlerException(CrawlerException("secret upstream detail"))

        assertEquals(HttpStatus.BAD_GATEWAY, response.statusCode)
        assertEquals("upstream service failed", response.body?.message)
    }
}
