package com.mapcrawler.utils

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

open class PlaywrightUtils(private val serviceWorkMap: Map<String, String>) {
    val log: Logger = LoggerFactory.getLogger(javaClass)
    val TIMEOUT: Double = 1000 * 30.toDouble()

    private fun getServiceWorkType(workId: String): String? {
        return serviceWorkMap[workId]
    }

    protected fun getPlaywright(): Playwright {
        return Playwright.create()
    }

    protected fun getBrowser(workId: String, playwright: Playwright): Browser? {
        try {
            val wsEndpoint = getServiceWorkType(workId)
            val browser = playwright.chromium().connect(wsEndpoint)
            return browser
        } catch (e: Exception) {
            log.error("원격 브라우저 연결 실패: {}", e.javaClass.simpleName)
            return null
        }
    }

    protected fun getPage(browser: Browser): Page? {
        try {
            val context = browser.newContext()

            val page = context.newPage()
            page.setExtraHTTPHeaders(mutableMapOf("Accept-Language" to "ko-KR"))
            page.setDefaultNavigationTimeout(TIMEOUT)
            return page
        } catch (e: Exception) {
            log.error("브라우저 페이지 생성 실패: {}", e.javaClass.simpleName)
            return null
        }
    }

    protected fun allClose(playwright: Playwright, browser: Browser?) {
        if (browser != null) {
            runCatching { browser.contexts().toList() }
                .onFailure { log.warn("브라우저 컨텍스트 조회 실패: {}", it.javaClass.simpleName) }
                .getOrDefault(emptyList())
                .forEachIndexed { index, context ->
                    runCatching { context.close() }
                        .onFailure {
                            log.warn("브라우저 컨텍스트 정리 실패(index={}): {}", index, it.javaClass.simpleName)
                        }
                }

            runCatching { browser.close() }
                .onFailure { log.warn("브라우저 정리 실패: {}", it.javaClass.simpleName) }
        }

        runCatching { playwright.close() }
            .onFailure { log.warn("Playwright 정리 실패: {}", it.javaClass.simpleName) }
    }

    protected fun sleep(time: Long) {
        TimeUnit.MILLISECONDS.sleep(time)
    }


}
