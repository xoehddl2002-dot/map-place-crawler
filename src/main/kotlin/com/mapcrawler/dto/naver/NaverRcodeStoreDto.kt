package com.mapcrawler.dto.naver

import com.mapcrawler.dto.StoreInfoDto

class NaverRcodeStoreDto : StoreInfoDto {
    var rank: Int? = null
    var rankDiff: Int? = null
    var id: String? = null
    var name: String? = null
    var categoryName: String? = null
    var address: String? = null
    var x: String? = null
    var y: String? = null
    var description: String? = null
    var cardType: Int? = null
    var reviewCount: Int? = null
    var saveCnt: Int? = null
    var averagePrice: Int? = null
    var ratingInfo: RatingInfo? = null
    var microReviews: MutableList<Any>? = null
    var cardDescription: String? = null
    var keywords: MutableList<Any>? = null
    var labels: MutableList<Any>? = null
    var tags: MutableList<Any>? = null
    var images: MutableList<String>? = null

    class RatingInfo {
        var total: String? = null
        var receipt: Any? = null
        var booking: Any? = null
    }
}

