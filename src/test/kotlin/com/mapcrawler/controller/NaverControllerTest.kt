package com.mapcrawler.controller

import com.mapcrawler.exception.CrawlerException
import com.mapcrawler.utils.NaverUtils
import com.mapcrawler.utils.WorkPoolUtils
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NaverControllerTest {
    @Test
    fun `around는 외부 topic id 대신 변환된 내부 코드를 전달한다`() {
        val naverUtils = Mockito.mock(NaverUtils::class.java)
        val workPool = WorkPoolUtils(mutableMapOf("svr1" to 0))
        Mockito.`when`(naverUtils.around("svr1", "700005", "37.5", "127.0"))
            .thenReturn(emptyList())

        NaverController(naverUtils, workPool).around("topic1", "37.5", "127.0")

        Mockito.verify(naverUtils).around("svr1", "700005", "37.5", "127.0")
        assertEquals(0, workPool.getWorkMap().getValue("svr1"))
    }

    @Test
    fun `수집 중 예외가 발생해도 워커 점유 수를 반납한다`() {
        val naverUtils = Mockito.mock(NaverUtils::class.java)
        val workPool = WorkPoolUtils(mutableMapOf("svr1" to 0))
        Mockito.`when`(naverUtils.list("svr1", "카페"))
            .thenThrow(CrawlerException("failed"))

        assertFailsWith<CrawlerException> {
            NaverController(naverUtils, workPool).list("카페")
        }

        assertEquals(0, workPool.getWorkMap().getValue("svr1"))
    }
}
