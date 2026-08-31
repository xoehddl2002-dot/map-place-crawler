package com.mapcrawler.controller

import com.mapcrawler.dto.CreateStatusDataDto
import com.mapcrawler.dto.CreateStatusListDto
import com.mapcrawler.exception.ApiException
import com.mapcrawler.exception.CrawlerException
import com.mapcrawler.utils.KakaoUtils
import com.mapcrawler.utils.ParamValidateUtils
import com.mapcrawler.utils.WorkPoolUtils
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/kakao")
class KakaoController(
    private val kakaoUtils: KakaoUtils,
    private val workPoolUtils: WorkPoolUtils,
) {
    private fun requireQuery(query: String?): String =
        ParamValidateUtils.normalizeQuery(query)
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "q must be between 1 and 100 characters")

    @GetMapping("/id/{code}")
    fun id(@PathVariable code: String): CreateStatusDataDto {
        if (!ParamValidateUtils.isValidateCode(code)) {
            throw ApiException(HttpStatus.BAD_REQUEST, "code must be 8 to 10 digits")
        }
        return CreateStatusDataDto(200).also { result ->
            result.store = workPoolUtils.withWorkId { kakaoUtils.id(it, code) }
                ?: throw CrawlerException("Kakao place response was empty")
        }
    }

    @GetMapping("/totalCount")
    fun totalCount(q: String?): CreateStatusListDto {
        val query = requireQuery(q)
        return CreateStatusListDto(200).also { result ->
            result.totalCount = workPoolUtils.withWorkId { kakaoUtils.totalCount(it, query) }
        }
    }

    @GetMapping("/list")
    fun list(q: String?): CreateStatusListDto {
        val query = requireQuery(q)
        val kakaoResult = workPoolUtils.withWorkId { kakaoUtils.list(it, query) }
        return CreateStatusListDto(200).also { result ->
            result.totalCount = kakaoResult.totalCount
            kakaoResult.list?.let(result.storeList::addAll)
        }
    }
}
