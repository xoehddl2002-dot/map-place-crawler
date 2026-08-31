package com.mapcrawler.controller

import com.mapcrawler.dto.CreateStatusDataDto
import com.mapcrawler.dto.CreateStatusListDto
import com.mapcrawler.dto.naver.NaverListResultStoreDto
import com.mapcrawler.dto.naver.NaverListStoreDto
import com.mapcrawler.exception.ApiException
import com.mapcrawler.exception.CrawlerException
import com.mapcrawler.utils.FilterUtils
import com.mapcrawler.utils.NaverRestaurantBusiness
import com.mapcrawler.utils.NaverUtils
import com.mapcrawler.utils.ParamValidateUtils
import com.mapcrawler.utils.WorkPoolUtils
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/naver")
class NaverController(
    private val naverUtils: NaverUtils,
    private val workPoolUtils: WorkPoolUtils,
) {
    companion object {
        private val headerLock = Any()

        @Volatile
        private var headers: Map<String, String>? = null

        fun clearHeaders() = synchronized(headerLock) {
            headers = null
        }
    }

    private fun requestHeaders(): Map<String, String> {
        headers?.let { return it }
        return synchronized(headerLock) {
            headers ?: workPoolUtils.withWorkId { naverUtils.getRequestHeader(it) }
                ?.also { headers = it }
                ?: throw CrawlerException("failed to acquire Naver request headers")
        }
    }

    private fun requireQuery(query: String?): String =
        ParamValidateUtils.normalizeQuery(query)
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "q must be between 1 and 100 characters")

    private fun requireCode(code: String) {
        if (!ParamValidateUtils.isValidateCode(code)) {
            throw ApiException(HttpStatus.BAD_REQUEST, "code must be 8 to 10 digits")
        }
    }

    private fun NaverListStoreDto.toResult(includeNewOpening: Boolean = false) =
        NaverListResultStoreDto().also { result ->
            result.telphone = FilterUtils.filteringInvalidChar_tel(phone)
            result.category = category
            result.title = FilterUtils.filteringInvalidChar(name)
            result.address = FilterUtils.filteringInvalidChar("$commonAddress $address")
            result.roadAddress = FilterUtils.filteringInvalidChar(roadAddress)
            result.link = bookingUrl
            result.mapx = x
            result.mapy = y
            result.imageCount = imageCount
            result.visitorReviewCount = visitorReviewCount?.replace(",", "")?.toIntOrNull() ?: 0
            result.blogCafeReviewCount = blogCafeReviewCount?.replace(",", "")?.toIntOrNull() ?: 0
            result.bookingReviewCount = bookingReviewCount?.replace(",", "")?.toIntOrNull() ?: 0
            result.id = id?.toString().orEmpty()
            result.imageUrl = imageUrl.orEmpty()
            if (includeNewOpening) result.newOpening = newOpening ?: false
        }

    @GetMapping("/list")
    fun list(q: String?): CreateStatusListDto {
        val query = requireQuery(q)
        val naverResult = workPoolUtils.withWorkId { naverUtils.list(it, query) }
            ?: throw CrawlerException("Naver list response was empty")
        return CreateStatusListDto(200).also { result ->
            result.totalCount = naverResult.total
            naverResult.items?.mapTo(result.storeList) { it.toResult() }
        }
    }

    @GetMapping("/restaurant/new")
    fun newRestaurant(q: String?): CreateStatusListDto {
        val query = requireQuery(q).replace(" 새로오픈", "").replace("새로오픈", "").trim()
        val restaurants: NaverRestaurantBusiness = workPoolUtils.withWorkId {
            naverUtils.newRestaurants(it, "$query 음식점 새로오픈")
        } ?: throw CrawlerException("Naver restaurant response was empty")
        return CreateStatusListDto(200).also { result ->
            result.totalCount = restaurants.total
            restaurants.items?.mapTo(result.storeList) { it.toResult(includeNewOpening = true) }
        }
    }

    @GetMapping("/geocode")
    fun geocode(x: String?, y: String?): CreateStatusListDto {
        if (x == null || !ParamValidateUtils.isValidLongitude(x) || y == null || !ParamValidateUtils.isValidLatitude(y)) {
            throw ApiException(HttpStatus.BAD_REQUEST, "x must be longitude and y must be latitude")
        }
        return CreateStatusListDto(200).also {
            it.storeList.addAll(naverUtils.geocode(x, y, requestHeaders()))
        }
    }

    @GetMapping("/weather/{code}")
    fun weather(@PathVariable code: String): CreateStatusDataDto {
        if (code.length != 8 || !code.all(Char::isDigit)) {
            throw ApiException(HttpStatus.BAD_REQUEST, "rcode must be 8 digits")
        }
        return CreateStatusDataDto(200).also {
            it.store = naverUtils.weather(code, requestHeaders())
                ?: throw CrawlerException("Naver weather response was empty")
        }
    }

    @GetMapping("/widget/{code}")
    fun widget(@PathVariable code: String): CreateStatusDataDto {
        requireCode(code)
        return CreateStatusDataDto(200).also { result ->
            result.store = workPoolUtils.withWorkId { naverUtils.widget(it, code) }
                ?: throw CrawlerException("Naver widget response was empty")
        }
    }

    @GetMapping("/rcode/{typeId}/{rcode}")
    fun rcode(@PathVariable typeId: String, @PathVariable rcode: String, limit: Int?): CreateStatusListDto {
        if (!ParamValidateUtils.isValidateRcode(rcode)) {
            throw ApiException(HttpStatus.BAD_REQUEST, "rcode must be 5 to 10 digits")
        }
        val rcodeType = ParamValidateUtils.getRcodeType(typeId)
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "invalid typeId")
        val limitNum = limit ?: 1000
        if (limitNum !in 1..1000) {
            throw ApiException(HttpStatus.BAD_REQUEST, "limit must be between 1 and 1000")
        }
        return CreateStatusListDto(200).also {
            it.storeList.addAll(naverUtils.rcode(rcodeType, rcode, limitNum, requestHeaders()))
        }
    }

    @GetMapping("/marker/{code}")
    fun marker(@PathVariable code: String): CreateStatusDataDto {
        requireCode(code)
        return CreateStatusDataDto(200).also {
            it.store = naverUtils.marker(code, requestHeaders())
                ?: throw CrawlerException("Naver marker response was empty")
        }
    }

    @GetMapping("/around/{topicId}")
    fun around(@PathVariable topicId: String, lat: String?, lng: String?): CreateStatusListDto {
        val topicCode = ParamValidateUtils.getTopicCode(topicId)
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "invalid topicId")
        if (lat == null || !ParamValidateUtils.isValidLatitude(lat)) {
            throw ApiException(HttpStatus.BAD_REQUEST, "lat must be between -90 and 90")
        }
        if (lng == null || !ParamValidateUtils.isValidLongitude(lng)) {
            throw ApiException(HttpStatus.BAD_REQUEST, "lng must be between -180 and 180")
        }
        val stores = workPoolUtils.withWorkId { naverUtils.around(it, topicCode, lat, lng) }
            ?: throw CrawlerException("Naver around response was empty")
        return CreateStatusListDto(200).also { it.storeList.addAll(stores) }
    }

    @GetMapping("/images/{code}")
    fun images(@PathVariable code: String): CreateStatusListDto {
        requireCode(code)
        val stores = workPoolUtils.withWorkId { naverUtils.images(it, code) }
            ?: throw CrawlerException("Naver images response was empty")
        return CreateStatusListDto(200).also { it.storeList.addAll(stores) }
    }

    @GetMapping("/search")
    fun search(q: String?): CreateStatusListDto {
        val query = requireQuery(q)
        val naverList = workPoolUtils.withWorkId { naverUtils.search(it, query) }
            ?: throw CrawlerException("Naver search response was empty")
        return CreateStatusListDto(200).also { result ->
            result.totalCount = naverList.total
            naverList.items?.mapTo(result.storeList) { it.toResult(includeNewOpening = true) }
        }
    }
}
