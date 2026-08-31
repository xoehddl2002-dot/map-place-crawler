package com.mapcrawler.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mapcrawler.dto.StoreInfoDto
import com.mapcrawler.dto.naver.*
import com.mapcrawler.exception.CrawlerException
import com.microsoft.playwright.Page
import com.microsoft.playwright.Route
import com.microsoft.playwright.Route.ResumeOptions
import com.microsoft.playwright.options.RequestOptions
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Date;
import java.util.Objects
import kotlin.text.toInt


class NaverUtils(serviceWorkMap: Map<String, String>) : PlaywrightUtils(serviceWorkMap) {

    private fun encodeQuery(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)

    private fun requestInterceptor(router: Route, postData: BasePostData) {
        val headers = router.request().headers()
        headers["content-type"] = "application/json"
        val resumeObject = ResumeOptions().apply {
            this.url = router.request().url()
            this.method = "POST"
            this.postData = "[$postData]"
            this.headers = headers

        }
        router.resume(resumeObject)
    }

    fun getRequestHeader(workId: String): Map<String, String>? {
        var capturedHeaders: Map<String, String>? = null
        val playwright = getPlaywright()
        val browser = getBrowser(workId, playwright)
        if (browser == null) {
            log.info("브라우저 실행 실패!")
            allClose(playwright, browser)
            return null
        }
        val page = getPage(browser)
        if (page == null) {
            log.info("페이지 생성 실패")
            allClose(playwright, browser)
            return null
        }
        try {
            page.route("**") { route ->
                if (route.request().url().startsWith("https://map.naver.com")) {
                    capturedHeaders = route.request().headers().toMap()
                }
                route.resume()
            }

            page.navigate("https://map.naver.com/p?c=15.00,0,0,0,dh")
            page.close()
        } catch (e: Exception) {
            throw CrawlerException("failed to acquire Naver request headers", e)
        } finally {
            allClose(playwright, browser)
        }
        return capturedHeaders
    }

    fun around(workId: String, topicCode: String, lat: String, lng: String): List<StoreInfoDto>? {

        val list = mutableListOf<StoreInfoDto>()

        val playwright = getPlaywright()
        val browser = getBrowser(workId, playwright)
        if (browser == null) {
            log.info("브라우저 생성 실패")
            allClose(playwright, browser)
            return null
        }
        val page = getPage(browser)
        if (page == null) {
            log.info("페이지 생성 실패")
            allClose(playwright, browser)
            return null
        }
        try {
            var start = 1
            val display = 30
            val gson = GsonBuilder().serializeNulls().create()

            while (true) {
                val response =
                    page.navigate("https://s.search.naver.com/p/around/search.naver?view=main&tab=theme&lat=${lat}&lng=${lng}&type=api&start=${start}&display=${display}&topicid=${topicCode}&f_sort=distance")
                val responseBodyFinished = response.finished()
                val responseOk = response.ok()
                log.info("${response.url()} 결과, $responseBodyFinished $responseOk")
                if (response.status() !in 200..300) {
                    break
                }
                val jsonStr = page.evaluate(
                    """
                        ()=>{
                            const json = JSON.parse(document.querySelector('body').innerText)
                            const contentList=json['result']['dom']['main']['contents']
                            document.querySelector("body").innerHTML=contentList
                            const list=document.querySelectorAll("ul.smartaround_list_wrap > li")
                            if(list&&list.length>0){
                                const arr=Array.from(list).map(elem=>{
                                  return {
                                    x:elem.getAttribute("data-px"),
                                    y:elem.getAttribute("data-py"),
                                    address:elem.getAttribute("data-address"),
                                    id:elem.getAttribute("data-sid"),
                                    name:elem.getAttribute("data-name")
                                  }
                                })
                                return JSON.stringify(arr)
                            }else{
                                return []
                            }
                        }
                    """
                ).toString()

                if (jsonStr.isEmpty() || !jsonStr.startsWith("[")) {
                    break
                }
                val naverAroundType = object : TypeToken<MutableList<NaverAroundStoreDto>>() {}
                val aroundResult = gson.fromJson(jsonStr, naverAroundType)
                list.addAll(aroundResult)

                if (aroundResult.size < display) {
                    break
                }
                start += display
            }
            page.close()
        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Naver around places", e)
        } finally {
            allClose(playwright, browser)
        }
        return list.toList()
    }

    fun marker(code: String, header: Map<String, String>): StoreInfoDto? {
        var result: StoreInfoDto? = null
        try {
            val jsonStr =
                Jsoup.connect("https://map.naver.com/p/api/place/summary/$code").timeout(TIMEOUT.toInt())
                    .headers(header).ignoreContentType(true)
                    .get().text()
            val gson = GsonBuilder().serializeNulls().create()

            val jsonObj = gson.fromJson(jsonStr, NaverMarkerStoreData::class.java)
            result=jsonObj.data?.placeDetail
        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Naver marker", e)
        }
        return result
    }

    fun rcode(type: String, rcode: String, limit: Int, header: Map<String, String>): List<StoreInfoDto> {
        val list = mutableListOf<StoreInfoDto>()
        try {
            val jsonStr =
                Jsoup.connect("https://map.naver.com/p/api/smart-around/timeline/places?rcode=$rcode&type=$type&limit=$limit")
                    .timeout(TIMEOUT.toInt()).headers(header).ignoreContentType(true).get().text()

            val naverRcodeType = object : TypeToken<MutableList<NaverRcodeResult>>() {}
            val gson = GsonBuilder().serializeNulls().create()

            val rcodeResult = gson.fromJson(jsonStr, naverRcodeType)
            rcodeResult.forEach {
                it.result?.list?.let { tempList ->
                    list.addAll(tempList)
                }
            }
        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Naver rcode places", e)
        }
        return list
    }

    fun geocode(x: String, y: String, header: Map<String, String>): List<StoreInfoDto> {
        val result = mutableListOf<StoreInfoDto>()
        try {
            val jsonStr =
                Jsoup.connect("https://map.naver.com/p/api/location/geocode?coords=${x},${y}&orders=admcode,legalcode")
                    .timeout(TIMEOUT.toInt()).headers(header).ignoreContentType(true).get().text()
            val gson = GsonBuilder().serializeNulls().create()

            val naverGeocode = gson.fromJson(jsonStr, NaverGeocodeResult::class.java)
            naverGeocode.results?.let {
                result.addAll(it)
            }

        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Naver geocode", e)
        }
        return result
    }

    fun weather(code: String, header: Map<String, String>): StoreInfoDto? {
        var result: StoreInfoDto? = null
        try {
            val jsonStr =
                Jsoup.connect("https://map.naver.com/p/api/weather/today/$code").timeout(TIMEOUT.toInt())
                    .headers(header).ignoreContentType(true)
                    .get().text()
            val gson = GsonBuilder().serializeNulls().create()

            result = gson.fromJson(jsonStr, NaverWeatherStoreDto::class.java)

        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Naver weather", e)
        }
        return result
    }

    fun widget(workId: String, code: String): StoreInfoDto? {
        var result: StoreInfoDto? = null
        val playwright = getPlaywright()
        val browser = getBrowser(workId, playwright)
        if (browser == null) {
            log.info("브라우저 생성 실패")
            allClose(playwright, browser)
            return null
        }
        val context=browser.newContext()
        val page=context.newPage()
        //val page = getPage(browser)
        if (page == null) {
            log.info("페이지 생성 실패")
            allClose(playwright, browser)
            return null
        }
        try {
            page.navigate("https://map.naver.com/p/smart-around/place/${code}")
            val t=Date().time
            log.warn("https://pages.map.naver.com/save-widget/api/maps-search/place?id=${code}&t=$t")
            val response = context.request().get("https://pages.map.naver.com/save-widget/api/maps-search/place?id=${code}&t=$t",
                RequestOptions.create().setHeader("Referer", page.url()))
            //val response =
              //  page.navigate("https://pages.map.naver.com/save-widget/api/maps-search/place?id=${code}&t=$t")

            //val responseBodyFinished = response.finished()
            val responseOk = response.ok()

            log.info("${response.url()} 결과값, $responseOk")

            if (response.status() == 200) {
                val innerText =response.text()

                val jsonStr: String = innerText.toString()//.replace("${'\\'}${'"'}","${'"'}")
                val gson = GsonBuilder().serializeNulls().create()

                val widgetResult = gson.fromJson(jsonStr, NaverWidgetResult::class.java)
                widgetResult.result?.place?.list?.firstOrNull()?.let{
                    result = it
                }
            }else{
                log.info("${response.status()},${response.text()}")
                log.info("통신 실패")
            }

            page.close()
        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Naver widget", e)
        } finally {
            allClose(playwright, browser)
        }
        return result

    }

    fun search(workId: String, query: String): NaverBusinesses? {
        var totalCount = 0
        val list = mutableListOf<NaverListStoreDto>()

        val playwright = getPlaywright()
        val browser = getBrowser("${workId}h", playwright)
        if (browser == null) {
            log.info("브라우저 생성 실패")
            allClose(playwright, browser)
            return null
        }

        val page = getPage(browser)
        if (page == null) {
            log.info("페이지 생성 실패")
            allClose(playwright, browser)
            return null
        }

        try {
            page.waitForLoadState()
            val res =
                page.navigate("https://m.place.naver.com/place/list?query=${encodeQuery(query)}&sortingOrder=distance")

            val responseBodyFinished = res.finished()
            val responseOk = res.ok()
            log.info("${res.url()} 결과, $responseBodyFinished $responseOk")

            page.waitForLoadState()

            var start = 1
            val display = 100

            val postData: NaverPlaceListPostData = NaverGraphQLUtils.getPlacesListPostData()

            val input = postData.variables.input
            input.display = display
            input.query = query

            page.route("https://api.place.naver.com/place/graphql") { router ->
                requestInterceptor(router, postData)
            }
            val gson = GsonBuilder().serializeNulls().create()

            input.start = start

            val response = page.navigate("https://api.place.naver.com/place/graphql")

            val subResponseBodyFinished = response.finished()
            val subResponseOk = response.ok()
            log.info("${response.url()} 결과, $subResponseBodyFinished $subResponseOk")

            val jsonStr: String = response.text()
            if (jsonStr.startsWith("[")) {
                val naverListType = object : TypeToken<MutableList<NaverListResult>>() {}
                val naverResult = gson.fromJson(jsonStr, naverListType)
                val businesses = naverResult.firstOrNull { it.data?.businesses != null }?.data?.businesses
                if (businesses != null) {
                    //totalCount는 검색결과에 대한 결과값이 모두 동일함
                    totalCount = businesses.total ?: 0
                    businesses.items?.run { list.addAll(this) }
                }
            }


            page.close()
        } catch (e: Exception) {
            throw CrawlerException("failed to search Naver places", e)
        } finally {
            allClose(playwright, browser)
        }
        return NaverBusinesses(
            total = totalCount, items = list.toList()
        )

    }

    fun list(workId: String, query: String): NaverBusinesses? {
        var totalCount = 0
        val list = mutableListOf<NaverListStoreDto>()

        val playwright = getPlaywright()
        val browser = getBrowser("${workId}h", playwright)
        if (browser == null) {
            log.info("브라우저 생성 실패")
            allClose(playwright, browser)
            return null
        }

        val page = getPage(browser)
        if (page == null) {
            log.info("페이지 생성 실패")
            allClose(playwright, browser)
            return null
        }

        try {
            page.waitForLoadState()
            val res =
                page.navigate("https://m.place.naver.com/place/list?query=${encodeQuery(query)}&sortingOrder=distance")

            val responseBodyFinished = res.finished()
            val responseOk = res.ok()
            log.info("${res.url()} 결과, $responseBodyFinished $responseOk")

            page.waitForLoadState()

            var start = 1
            val display = 100

            val postData: NaverPlaceListPostData = NaverGraphQLUtils.getPlacesListPostData()

            val input = postData.variables.input
            input.display = display
            input.query = query

            page.route("https://api.place.naver.com/place/graphql") { router ->
                requestInterceptor(router, postData)
            }
            val gson = GsonBuilder().serializeNulls().create()

            while (true) {
                input.start = start

                val response = page.navigate("https://api.place.naver.com/place/graphql")

                val subResponseBodyFinished = response.finished()
                val subResponseOk = response.ok()
                log.info("${response.url()} 결과, $subResponseBodyFinished $subResponseOk")

                val jsonStr: String = response.text()
                if (!jsonStr.startsWith("[")) {
                    break
                }
                val naverListType = object : TypeToken<MutableList<NaverListResult>>() {}
                val naverResult = gson.fromJson(jsonStr, naverListType)
                val businesses = naverResult.firstOrNull { it.data?.businesses != null }?.data?.businesses
                if (businesses != null) {
                    //totalCount는 검색결과에 대한 결과값이 모두 동일함
                    totalCount = businesses.total ?: 0
                    businesses.items?.run { list.addAll(this) }
                } else {
                    break
                }

                start += display
                if (start > 300) {
                    break
                }
                if (start > totalCount) {
                    break
                }
                sleep(1500)
            }
            page.close()
        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Naver place list", e)
        } finally {
            allClose(playwright, browser)
        }
        return NaverBusinesses(
            total = totalCount, items = list.toList()
        )
    }


    fun newRestaurants(workId: String, query: String): NaverRestaurantBusiness? {
        var totalCount = 0
        val list = mutableListOf<NaverListStoreDto>()

        val playwright = getPlaywright()
        val browser = getBrowser("${workId}h", playwright)
        if (browser == null) {
            log.info("브라우저 생성 실패")
            allClose(playwright, browser)
            return null
        }

        val page = getPage(browser)

        if (page == null) {
            log.error("페이지 생성 실패")
            allClose(playwright, browser)
            return null
        }


        try {
            page.waitForLoadState()

            val res = page.navigate("https://m.place.naver.com/place/list?query=${encodeQuery(query)}")

            //요청에 대한 완료 대기->결과 처리 여부(필수!!)
            val responseBodyFinished = res.finished()
            val responseOk = res.ok()
            log.info("${res.url()} 결과, $responseBodyFinished : $responseOk")

            page.waitForLoadState()

            val postData: NaverRestaurantsPostData = NaverGraphQLUtils.getRestaurantListPostData()
            page.route("https://api.place.naver.com/place/graphql") { router ->
                requestInterceptor(router, postData)
            }

            var start = 1
            val display = 100
            val maxDisplay = 300
            postData.variables.input.apply {
                this.display = display
                this.query = query
            }
            val gson = GsonBuilder().serializeNulls().create()
            while (true) {
                postData.variables.input.start = start
                log.info("postData : ${ObjectMapper().writeValueAsString(postData)}")

                val response = page.navigate("https://api.place.naver.com/place/graphql")

                //요청에 대한 완료 대기->결과 처리 여부(필수!!)
                val subResponseBodyFinished = response.finished()
                val subResponseOk = response.ok()
                log.info("${response.url()} 결과, $subResponseBodyFinished : $subResponseOk")

                val jsonStr: String = response.text()
                if (!jsonStr.startsWith("[")) {
                    break
                }
                val naverListType = object : TypeToken<MutableList<NaverRestaurantResult>>() {}
                val naverResult = gson.fromJson(jsonStr, naverListType)
                val restaurants = naverResult.firstOrNull { it.data?.placeList != null }?.data?.placeList?.businesses

                if (restaurants != null) {
                    //totalCount는 검색결과에 대한 결과값이 모두 동일함
                    totalCount = restaurants.total ?: 0

                    restaurants.items?.run { list.addAll(this) }
                } else {
                    break
                }
                start += display
                if (start > maxDisplay) {
                    break
                }
                if (start > totalCount) {
                    break
                }
                sleep(1500)
            }
            page.close()

        } catch (e: Exception) {
            throw CrawlerException("failed to crawl new Naver restaurants", e)
        } finally {
            allClose(playwright, browser)
        }
        return NaverRestaurantBusiness(
            total = totalCount,
            items = list.toList()
        )
    }

    fun images(workId: String, code: String): List<StoreInfoDto>? {
        val list = mutableListOf<StoreInfoDto>()

        val playwright = getPlaywright()
        val browser = getBrowser("${workId}h", playwright)
        if (browser == null) {
            log.info("브라우저 생성 실패")
            allClose(playwright, browser)
            return null
        }
        try {
            val context = browser.newContext()
            val page = context.newPage()
            page.navigate("https://map.naver.com/p/smart-around/place/${code}")
            page.waitForLoadState()

            val gson = GsonBuilder().serializeNulls().create()
            val t=Date().time
            log.warn("https://pages.map.naver.com/save-widget/api/maps-search/place?id=${code}&t=$t")


            val response = context.request().get("https://pages.map.naver.com/save-widget/api/maps-search/place?id=${code}&t=$t",
                RequestOptions.create().setHeader("Referer", page.url()))

            //val responseBodyFinished = response.finished()
            val responseOk = response.ok()
            log.info("${response.url()} 경과값, $responseOk")

            if (response.status() == 200) {

                val innerText = response.text()

                val jsonStr = innerText.toString()//.replace("${'\\'}${'"'}","${'"'}")

                val widgetResult = gson.fromJson(jsonStr, NaverWidgetResult::class.java)
                widgetResult.result?.place?.list?.let { tempList ->
                    tempList.firstOrNull()?.thumUrls?.map {
                        NaverImagesStoreDto().apply {
                            this.type = 1
                            this.url = it
                            this.id = code
                        }
                    }?.let { list.addAll(it) }

                }


                val postData = NaverGraphQLUtils.getPhotoViewerItemsPostData(code)
                page.route("https://api.place.naver.com/place/graphql") { router ->
                    requestInterceptor(router, postData)
                }
                val response2 = page.navigate("https://api.place.naver.com/place/graphql")

                val response2BodyFinished = response2.finished()
                val response2Ok = response2.ok()

                log.info("${response2.url()} 결과값, $response2BodyFinished, $response2Ok")

                if (!response2Ok && response2.status() != 200) {
                    log.info("통신 오류")
                    return null
                }


                val jsonStr2 = response2.text()
                val naverPhotosType = object : TypeToken<MutableList<NaverPhotoViewerResult>>() {}
                val naverResult = gson.fromJson(jsonStr2, naverPhotosType)

                naverResult.forEach {
                    it.data?.photoViewer?.photos?.let { photos ->
                        list.addAll(photos.map { p ->
                            NaverImagesStoreDto().apply {
                                this.type = 2
                                this.url = p.originalUrl
                                this.id = code
                            }
                        })
                    }
                }
            }else{
                log.info("통신 오류")
            }
            page.close()
        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Naver images", e)
        } finally {
            allClose(playwright, browser)
        }
        return list
    }
    
}




data class NaverTotalCountResult(val result: NaverResultTotalCount? = null)
data class NaverResultTotalCount(val totalCount: Int? = null)

data class NaverListResult(val data: NaverListData?)
data class NaverListData(val businesses: NaverBusinesses?)
data class NaverBusinesses(
    val total: Int?,
    val items: List<NaverListStoreDto>?,
    val searchGuide: Any? = null,
    val queryString: String? = null,
    val siteSort: String? = null
)

data class NaverRestaurantResult(val data: NaverRestaurantData?)
data class NaverRestaurantData(val placeList: NaverRestaurants?)
data class NaverRestaurants(val businesses: NaverRestaurantBusiness?)
data class NaverRestaurantBusiness(
    val total: Int?,
    val items: List<NaverListStoreDto>? = listOf(),
    val searchGuide: Any? = null,
    val queryString: String? = null,
    val siteSort: String? = null,
    var isFormPrism: Boolean? = null
)


data class NaverWidgetResult(val result: NaverResultWidget? = null)
data class NaverResultWidget(val metaInfo: Any? = null, val place: NaverPlaceWidget?)
data class NaverPlaceWidget(
    val page: Int? = null,
    val totalCount: Int? = null,
    val boundary: MutableList<String>? = null,
    val feedback: MutableList<String>? = null,
    val options: Any? = null,
    val filters: Any? = null,
    val reSearch: Any? = null,
    val isSiteSortAvailable: Boolean? = null,
    val specializedSearch: Any? = null,
    val hasPollingPlace: Boolean? = null,
    val isAdultKeyword: Boolean? = null,
    val containAdultContents: Boolean? = null,
    val address: Any? = null,
    val brandPromotion: Any? = null,
    val list: MutableList<NaverWidgetStoreDto>? = null
)

data class NaverGeocodeResult(val results: List<NaverGeocodeStoreDto>? = null, val status: NaverStatus? = null)

data class NaverStatus(
    val code: Int? = null,
    val message: String? = null,
    val name: String? = null
)


data class NaverRcodeResult(val result: NaverResultRcode? = null)

data class NaverResultRcode(val meta: Any? = null, val list: List<NaverRcodeStoreDto>? = null)

data class NaverPhotoViewerResult(val data: NaverDataPhoto? = null)
data class NaverDataPhoto(val photoViewer: NaverPhotoViewer? = null)
data class NaverPhotoViewer(
    val cursor: MutableList<Any>? = null,
    val photos: MutableList<NaverPhotos>? = null
)

data class NaverPhotos(
    val viewId: String? = null,
    val originalUrl: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val title: String? = null,
    val desc: Any? = null,
    val link: String? = null,
    val date: String? = null,
    val photoType: String? = null,
    val mediaType: Any? = null,
    val option: Any? = null,
    val to: Any? = null,
    val relation: String? = null,
    val logId: Any? = null,
    val author: Any? = null,
    val votedKeywords: Any? = null,
    val visitCount: Any? = null,
    val originType: Any? = null,
    val isFollowing: Any? = null,
    val businessName: Any? = null,
    val rating: Any? = null,
    val externalLink: Any? = null,
    val sourceTitle: String? = null,
    val moment: Any? = null,
    val video: Any? = null,
    val clip: Any? = null
)
