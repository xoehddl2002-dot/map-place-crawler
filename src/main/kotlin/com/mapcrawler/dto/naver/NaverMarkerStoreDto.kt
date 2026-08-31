package com.mapcrawler.dto.naver

import com.mapcrawler.dto.StoreInfoDto

class NaverMarkerStoreData{
    var data:NaverMarkerStorePlaceDetail?=null
}

class NaverMarkerStorePlaceDetail{
    var placeDetail:NaverMarkerStoreDto?=null
}

class NaverMarkerStoreDto : StoreInfoDto {
    var address:Any? = null
    var beautyStyles:Any? = null
    var blogReviews:Any? = null
    var businessHour:Any? = null
    var businessType:String? = null
    var category:Any? = null
    var cordinate:Any? = null
    var id:String? = null
    var images:Any? = null
    var immersiveVideo3D:Any? = null
    var labels:Any? = null
    var name:String? = null
    var naverBookingMenu:Any? = null
    var opinet:Any? = null
    var panorama:Any? = null
    var reprPrice:Any? = null
    var visitorReviews:Any? = null

}

