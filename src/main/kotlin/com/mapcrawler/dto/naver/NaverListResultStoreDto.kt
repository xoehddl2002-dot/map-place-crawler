package com.mapcrawler.dto.naver

import com.mapcrawler.dto.StoreInfoDto

class NaverListResultStoreDto : StoreInfoDto {
    var telphone: String? = null
    var category: String? = null
    var title: String? = null
    var address: String? = null
    var roadAddress: String? = null
    var link: String? = null
    var mapx: String? = null
    var mapy: String? = null
    var imageCount: Int? = null
    var visitorReviewCount: Int? = null
    var blogCafeReviewCount: Int? = null
    var bookingReviewCount: Int? = null
    var id: String? = null
    var imageUrl: String? = null
    var newOpening: Boolean? = null
}