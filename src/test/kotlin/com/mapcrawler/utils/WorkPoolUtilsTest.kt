package com.mapcrawler.utils

import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class WorkPoolUtilsTest {

    private fun pool(vararg ids: String) =
        WorkPoolUtils(ids.associateWith { 0 }.toMutableMap())

    @Test
    fun `작업을 할당하면 해당 노드의 점유 수가 증가한다`() {
        val workPool = pool("svr1")

        val workId = workPool.getWorkId()

        assertEquals("svr1", workId)
        assertEquals(1, workPool.getWorkMap()["svr1"])
    }

    @Test
    fun `작업을 반납하면 점유 수가 감소한다`() {
        val workPool = pool("svr1")

        val workId = workPool.getWorkId()
        workPool.resetWorkId(workId)

        assertEquals(0, workPool.getWorkMap()["svr1"])
    }

    @Test
    fun `점유 수가 0 미만으로 내려가지 않는다`() {
        val workPool = pool("svr1")

        workPool.resetWorkId("svr1")

        assertEquals(0, workPool.getWorkMap()["svr1"])
    }

    @Test
    fun `가장 한가한 노드에 작업이 분배된다`() {
        val workPool = pool("svr1", "svr2", "svr3")

        // 3개 노드에 3개의 작업을 할당하면 각 노드가 정확히 1개씩 맡아야 한다.
        repeat(3) { workPool.getWorkId() }

        assertTrue(workPool.getWorkMap().values.all { it == 1 })
    }

    @Test
    fun `동시 요청에서도 총 점유 수가 일치한다`() {
        val workPool = pool("svr1", "svr2")
        val executor = Executors.newFixedThreadPool(16)
        val start = CountDownLatch(1)

        try {
            val futures = (1..1_000).map {
                executor.submit<String> {
                    start.await()
                    workPool.getWorkId()
                }
            }
            start.countDown()
            val workIds = futures.map { it.get(10, TimeUnit.SECONDS) }

            assertEquals(1_000, workPool.getWorkMap().values.sum())
            workIds.forEach(workPool::resetWorkId)
            assertTrue(workPool.getWorkMap().values.all { it == 0 })
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `작업 중 예외가 발생해도 lease를 반납한다`() {
        val workPool = pool("svr1")

        assertFailsWith<IllegalStateException> {
            workPool.withWorkId { error("boom") }
        }

        assertEquals(0, workPool.getWorkMap().getValue("svr1"))
    }

    @Test
    fun `워커가 없으면 명확한 오류를 반환한다`() {
        val workPool = pool()

        assertFailsWith<IllegalArgumentException> { workPool.getWorkId() }
    }
}
