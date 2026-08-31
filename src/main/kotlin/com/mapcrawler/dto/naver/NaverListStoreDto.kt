package com.mapcrawler.dto.naver

import com.mapcrawler.dto.StoreInfoDto

class NaverListStoreDto : StoreInfoDto {
    var id: String? = null
    var name: String? = null
    var normalizedName: String? = null
    var category: String? = null
    var detailCid: DetailCidDto? = null
    var categoryCodeList: MutableList<String>? = null
    var dbType: String? = null
    var distance: String? = null
    var roadAddress: String? = null
    var address: String? = null
    var fullAddress: String? = null
    var commonAddress: String? = null
    var bookingUrl: String? = null
    var phone: String? = null
    var virtualPhone: String? = null
    var businessHours: String? = null
    var daysOff: Any? = null
    var imageUrl: String? = null
    var imageCount: Int? = null
    var x: String? = null
    var y: String? = null
    var poiInfo: Any? = null
    var subwayId: Any? = null
    var isPublicGas: Any? = null
    var isDelivery: Boolean? = null
    var isTableOrder: Boolean? = null
    var isPreOrder: Boolean? = null
    var isTakeOut: Boolean? = null
    var isCvsDelivery: Boolean? = null
    var hasBooking: Boolean? = null
    var naverBookingCategory: String? = null
    var bookingDisplayName: String? = null
    var bookingBusinessId: String? = null
    var bookingVisitId: String? = null
    var bookingPickupId: String? = null
    var easyOrder: Any? = null
    var baemin: Any? = null
    var yogiyo: Any? = null
    var isPollingStation: Boolean? = null
    var hasNPay: Boolean? = null
    var talktalkUrl: String? = null
    var visitorReviewCount: String? = null
    var visitorReviewScore: String? = null
    var blogCafeReviewCount: String? = null
    var bookingReviewCount: String? = null
    var streetPanorama: StreetPanoramaDto? = null
    var naverBookingHubId: Any? = null
    var bookingHubUrl: Any? = null
    var bookingHubButtonName: Any? = null
    var newOpening: Boolean? = null
    var newBusinessHours: NewBusinessHoursDto? = null
    var coupon: Any? = null

    class DetailCidDto {
        var c0: String? = null
        var c1: String? = null
        var c2: String? = null
        var c3: String? = null
    }

    class StreetPanoramaDto {
        var id: String? = null
        var pan: String? = null
        var tilt: String? = null
        var lat: String? = null
        var lon: String? = null
    }

    class NewBusinessHoursDto {
        var status: String? = null
        var description: String? = null
        var dayOff: Any? = null
        var dayOffDescription: Any? = null
    }
}

