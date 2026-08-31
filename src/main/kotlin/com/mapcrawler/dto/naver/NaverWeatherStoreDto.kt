package com.mapcrawler.dto.naver

import com.mapcrawler.dto.StoreInfoDto

class NaverWeatherStoreDto : StoreInfoDto {
    var regionCode: String? = null
    var largeAreaName: String? = null
    var middleAreaName: String? = null
    var smallAreaName: String? = null
}