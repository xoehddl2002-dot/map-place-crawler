package com.mapcrawler.config

import com.mapcrawler.utils.KakaoUtils
import com.mapcrawler.utils.NaverUtils
import com.mapcrawler.utils.WorkPoolUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * browserless 노드 목록을 읽어 Playwright 접속용 WebSocket 엔드포인트 맵을 구성한다.
 *
 * - `svr{n}`  : headless 모드 엔드포인트
 * - `svr{n}h` : headful 모드 엔드포인트 (headless 탐지로 응답이 달라지는 페이지 대응)
 *
 * 노드를 추가/제거할 때 코드 수정 없이 `browserless.hosts` 설정만 바꾸면 되도록 했다.
 */
@Configuration
@ConfigurationProperties(prefix = "browserless")
class PropertiesConfig {
    lateinit var hosts: List<String>

    /** browserless 인증 토큰. 운영 값은 BROWSERLESS_TOKEN 환경변수로 주입한다. */
    lateinit var token: String

    /** 페이지 로딩이 느린 노드를 고려한 세션 타임아웃(ms) */
    var connectTimeoutMs: Long = 300_000

    private fun endpoint(host: String, headless: Boolean): String {
        val launch = if (headless) {
            """{"args":["--disable-dev-shm-usage"]}"""
        } else {
            """{"headless":false,"args":["--disable-dev-shm-usage"]}"""
        }
        return "ws://$host/chromium/playwright?token=$token&launch=$launch&timeout=$connectTimeoutMs"
    }

    @Bean
    @Primary
    fun serviceWorkMap(): Map<String, String> {
        return buildMap {
            hosts.forEachIndexed { index, host ->
                val id = index + 1
                put("svr$id", endpoint(host, headless = true))
                put("svr${id}h", endpoint(host, headless = false))
            }
        }
    }

    @Bean
    fun workPoolUtils(): WorkPoolUtils {
        val map = mutableMapOf<String, Int>()
        hosts.indices.forEach { map["svr${it + 1}"] = 0 }
        return WorkPoolUtils(map)
    }

    @Bean
    fun kakaoUtils(@Qualifier("serviceWorkMap") serviceWorkMap: Map<String, String>): KakaoUtils {
        return KakaoUtils(serviceWorkMap)
    }

    @Bean
    fun naverUtils(@Qualifier("serviceWorkMap") serviceWorkMap: Map<String, String>): NaverUtils {
        return NaverUtils(serviceWorkMap)
    }
}
