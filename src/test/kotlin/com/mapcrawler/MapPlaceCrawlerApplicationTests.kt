package com.mapcrawler

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class MapPlaceCrawlerApplicationTests {

	@Autowired
	lateinit var mockMvc: MockMvc

	@Test
	fun contextLoads() {
	}

	@Test
	fun `잘못된 요청은 실제 HTTP 400으로 응답한다`() {
		mockMvc.get("/naver/list")
			.andExpect {
				status { isBadRequest() }
				jsonPath("$.status") { value(400) }
			}
	}

}
