package com.mapcrawler.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mapcrawler.dto.StoreInfoDto
import com.mapcrawler.dto.kakao.KakaoIdStoreDto
import com.mapcrawler.dto.kakao.KakaoListStoreDto
import com.mapcrawler.exception.CrawlerException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class KakaoUtils(serviceWorkMap: Map<String, String>) : PlaywrightUtils(serviceWorkMap) {
    private fun encodeQuery(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)

    fun totalCount(workId: String, q: String): Int {
        var result = 0
        val playwright = getPlaywright()
        val browser = getBrowser(workId, playwright)
        if (browser == null) {
            allClose(playwright, browser)
            throw CrawlerException("failed to connect to browserless for Kakao total count")
        }
        val page = getPage(browser)
        if (page == null) {
            allClose(playwright, browser)
            throw CrawlerException("failed to create page for Kakao total count")
        }
        try {
            page.navigate("https://map.kakao.com/?q=${encodeQuery(q)}")
            sleep(1000)
            val jsonResult = page.evaluate(
                """
                    ()=>{
                        const urlParams = new URL(location.href).searchParams;
                        const q= urlParams.get('q');
                        const randomName='Jquery_'+parseInt(`${'$'}{Math.random()*100000000}`)
                        return new Promise(async resolve=>{
                          window[randomName]=function(data){
                            const pageCount=data['place_totalcount']??0
                            resolve(pageCount)
                          }
                          function nextPage(){
                            const script=document.createElement("script")
                            script.src=`https://search.map.kakao.com/mapsearch/map.daum?q=${'$'}{encodeURIComponent(q)}&msFlag=S&page=1&sort=2&callback=${'$'}{randomName}`
                            document.body.appendChild(script)
                          }
                          nextPage()
                        }).then((pageCount)=>{
                          return pageCount
                        })
                    }
                    """
            )
            result = jsonResult.toString().toInt()
            page.close()
        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Kakao total count", e)
        } finally {
            allClose(playwright, browser)
        }
        return result
    }

    fun list(workId: String, q: String): KakaoListResult {
        val result =KakaoListResult(0, mutableListOf())

        val playwright = getPlaywright()
        val browser = getBrowser(workId, playwright)
        if (browser == null) {
            allClose(playwright, browser)
            throw CrawlerException("failed to connect to browserless for Kakao list")
        }
        val page = getPage(browser)
        if (page == null) {
            allClose(playwright, browser)
            throw CrawlerException("failed to create page for Kakao list")
        }
        try {


            page.navigate("https://map.kakao.com/?q=${encodeQuery(q)}")
            sleep(1000)
            val jsonResult = page.evaluate(
                """
                    ()=>{
                        window['list']=[]
                        window['totalCount']=0
                        window['page']=1
                        const urlParams = new URL(location.href).searchParams;
                        const q= urlParams.get('q');
                        const randomName='Jquery_'+parseInt(`${'$'}{Math.random()*100000000}`)
                        return new Promise(async resolve=>{
                          window[randomName]=function(data){
                            const places=data['place']??[]
                            window['list']=window['list'].concat(places)
                            const list=window['list']
                            const placeTotalCount=data['place_totalcount']??0
                            window['totalCount']=placeTotalCount
                            if(places.length>0 && list.length<placeTotalCount && window['page']<45){
                              window['page']=window['page']+1
                              setTimeout(nextPage,500)
                            }else{
                              resolve({list:window['list'],totalCount:window['totalCount']})
                            }
                          }
                          function nextPage(){
                            const script=document.createElement("script")
                            script.src=`https://search.map.kakao.com/mapsearch/map.daum?q=${'$'}{encodeURIComponent(q)}&msFlag=S&page=${'$'}{window['page']}&sort=2&callback=${'$'}{randomName}`
                            document.body.appendChild(script)
                          }
                          nextPage()
                        }).then((result)=>{
                          return result
                        })
                    }
                    """
            )
            val gson = GsonBuilder().serializeNulls().create()
            val jsonStr = gson.toJson(jsonResult)

            val kakaoResult = gson.fromJson(jsonStr, KakaoListResult::class.java)

            result.totalCount=kakaoResult.totalCount
            kakaoResult.list?.let{list->result.list?.addAll(list)}

            page.close()
        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Kakao place list", e)
        } finally {
            allClose(playwright, browser)
        }
        return result
    }

    fun id(workId: String, code: String): StoreInfoDto? {
        var result: StoreInfoDto? = null

        val playwright = getPlaywright()
        val browser = getBrowser(workId, playwright)
        if (browser == null) {
            allClose(playwright, browser)
            throw CrawlerException("failed to connect to browserless for Kakao place")
        }
        val page = getPage(browser)
        if (page == null) {
            allClose(playwright, browser)
            throw CrawlerException("failed to create page for Kakao place")
        }
        try {
            val response = page.navigate("https://place.map.kakao.com/m/main/v/${code}")
            val responseBodyFinished = response.finished()
            val responseOk = response.ok()
            if (responseOk && response.status() == 200) {
                log.info("${response.url()} 결과, $responseBodyFinished, $responseOk")

                val body = page.querySelector("body")
                val innerText = body.evaluate("node=>node.innerText")
                val gson = GsonBuilder().serializeNulls().create()
                val jsonStr = innerText.toString()//.replace("${'\\'}${'"'}","${'"'}")
                result = gson.fromJson(jsonStr, KakaoIdStoreDto::class.java)
            }else{
                log.info("통신 실패")
            }
            page.close()
        } catch (e: Exception) {
            throw CrawlerException("failed to crawl Kakao place", e)
        } finally {
            allClose(playwright, browser)
        }

        return result
    }
}

data class KakaoListResult(
    var totalCount: Int? = null,
    val list:MutableList<KakaoListStoreDto>? = null
)
