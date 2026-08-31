package com.mapcrawler.dto.naver

import com.mapcrawler.dto.StoreInfoDto

class NaverWidgetStoreDto : StoreInfoDto {
    var index: String? = null
    var rank: String? = null
    var id: String? = null
    var name: String? = null
    var tel: String? = null
    var isCallLink: Boolean? = null
    var virtualTel: String? = null
    var virtualTelDisplay: String? = null
    var ppc: String? = null
    var category: MutableList<String>? = null
    var categoryPath: MutableList<MutableList<String>>? = null
    var rcode: String? = null
    var businessStatus: BusinessStatus? = null
    var naviInfoText: Any? = null
    var address: String? = null
    var roadAddress: String? = null
    var abbrAddress: String? = null
    var shortAddress: MutableList<String>? = null
    var display: String? = null
    var telDisplay: String? = null
    var context: MutableList<Any>? = null
    var reviewCount: Int? = null
    var placeReviewCount: Int? = null
    var ktCallMd: String? = null
    var coupon: String? = null
    var thumUrl: String? = null
    var thumUrls: MutableList<String>? = null
    var type: String? = null
    var isSite: String? = null
    var posExact: String? = null
    var x: String? = null
    var y: String? = null
    var itemLevel: String? = null
    var isAdultBusiness: Boolean? = null
    var streetPanorama: StreetPanorama? = null
    var skyPanorama: SkyPanorama? = null
    var insidePanorama: Any? = null
    var interiorPanorama: Any? = null
    var indoorPanorama: Any? = null
    var theme: Any? = null
    var poiInfo: PoiInfo? = null
    var homePage: String? = null
    var description: String? = null
    var entranceCoords: EntranceCoords? = null
    var isPollingPlace: Boolean? = null
    var bizhourInfo: String? = null
    var menuInfo: String? = null
    var petrolInfo: Any? = null
    var couponUrl: Any? = null
    var couponUrlMobile: Any? = null
    var hasCardBenefit: Boolean? = null
    var menuExist: String? = null
    var hasNaverBooking: Boolean? = null
    var naverBookingUrl: String? = null
    var naverEasySmartOrder: Boolean? = null
    var reservationLabel: ReservationLabel? = null
    var reservation: Reservation? = null
    var hasBroadcaseInfo: Boolean? = null
    var broadcaseInfo: Any? = null
    var shopWindowInfo: Any? = null
    var hasNPay: Boolean? = null
    var carWash: String? = null
    var parkingPrice: Any? = null
    var card: Any? = null
    var distance: String? = null
    var marker: String? = null
    var markerSelected: String? = null
    var markerId: String? = null
    var microReview: MutableList<Any>? = null
    var michelinGuide: Any? = null
    var indoor: Any? = null
    var markerLabel: Any? = null
    var subway: Any? = null
    var evChargerVendor: Any? = null
    var evChargerPublic: Any? = null
    var evChargerParking: Any? = null
    var evChargerInfo: Any? = null

    class BusinessStatus {
        var requestTime: String? = null
        var status: Any? = null
        var businessHours: String? = null
        var breakTime: String? = null
        var lastOrder: String? = null
    }

    class StreetPanorama {
        var id: String? = null
        var pan: String? = null
        var tilt: String? = null
        var lng: String? = null
        var lat: String? = null
        var fov: String? = null
    }

    class SkyPanorama {
        var id: String? = null
        var pan: String? = null
        var tilt: String? = null
        var lng: String? = null
        var lat: String? = null
        var fov: String? = null
    }

    class PoiInfo {
        var relation: Any? = null
        var hasRelation: Boolean? = null
        var road: Any? = null
        var hasRoad: Boolean? = null
        var land: Any? = null
        var hasLand: Boolean? = null
        var polygon: Any? = null
        var hasPolygon: Boolean? = null
    }

    class EntranceCoords {
        var car: MutableList<Any>? = null
        var walk: MutableList<Any>? = null
    }

    class ReservationLabel {
        var standard: Boolean? = null
        var preOrder: Boolean? = null
        var table: Boolean? = null
        var takeout: Boolean? = null
    }

    class Reservation {
        var benefit: String? = null
    }
}

