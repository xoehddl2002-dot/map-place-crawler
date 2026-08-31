package com.mapcrawler.dto.kakao

import com.mapcrawler.dto.StoreInfoDto
import com.google.gson.annotations.SerializedName

class KakaoListStoreDto : StoreInfoDto {
    var confirmid: String? = null
    var x: Int? = null
    var y: Int? = null
    var lon: Double? = null
    var lat: Double? = null
    var name: String? = null
    var tel: String? = null
    var address: String? = null
    var reviewCount: Int? = null
    var homePage: String? = null
    var img: String? = null
    var sourceId: String? = null
    var source: String? = null
    var roadview: String? = null

    @SerializedName("full_category_ids")
    var fullCategoryIds: String? = null

    @SerializedName("last_cate_id")
    var lastCateId: String? = null

    @SerializedName("last_cate_name")
    var lastCateName: String? = null

    @SerializedName("last_cate_depth")
    var lastCateDepth: String? = null

    @SerializedName("cate_name_depth1")
    var cateNameDepth1: String? = null

    @SerializedName("cate_name_depth2")
    var cateNameDepth2: String? = null

    @SerializedName("cate_name_depth3")
    var cateNameDepth3: String? = null

    @SerializedName("cate_name_depth4")
    var cateNameDepth4: String? = null

    @SerializedName("cate_name_depth5")
    var cateNameDepth5: String? = null

    @SerializedName("hub_data")
    var hubData: String? = null
    var brand: String? = null
    var brandName: String? = null
    var oil1: String? = null
    var oil2: String? = null
    var oil3: String? = null
    var oil4: String? = null
    var oilTime: String? = null

    @SerializedName("oil_sel24")
    var oilSel24: String? = null
    var phoneSynonyms: String? = null

    @SerializedName("related_place")
    var relatedPlace: String? = null

    @SerializedName("new_address")
    var newAddress: String? = null
    var courseinfo: String? = null
    var qeoinfo: String? = null
    var requiringtime: String? = null

    @SerializedName("tvshow_info")
    var tvshowInfo: String? = null

    @SerializedName("tvshow_name")
    var tvshowName: String? = null

    @SerializedName("address_disp")
    var addressDisp: String? = null

    @SerializedName("new_address_disp")
    var newAddressDisp: String? = null
    var distance: String? = null
    var catetype: String? = null

    @SerializedName("new_zipcode")
    var newZipcode: String? = null

    @SerializedName("openoff_status")
    var openoffStatus: String? = null

    @SerializedName("shape_support_types")
    var shapeSupportTypes: String? = null

    @SerializedName("production_tags")
    var productionTags: MutableList<Any>? = null

    @SerializedName("rating_average")
    var ratingAverage: Double? = null

    @SerializedName("rating_count")
    var rationCount: Int? = null

    @SerializedName("addinfo_appointment")
    var addinfoAppointment: String? = null

    @SerializedName("addinfo_delivery")
    var addinfoDelivery: String? = null

    @SerializedName("addinfo_fordisabled")
    var addinfoFordisabled: String? = null

    @SerializedName("addinfo_nursery")
    var addinfoNursery: String? = null

    @SerializedName("addinfo_package")
    var addinfoPackage: String? = null

    @SerializedName("addinfo_parking")
    var addinfoParking: String? = null

    @SerializedName("addinfo_pet")
    var addinfoPet: String? = null

    @SerializedName("addinfo_smokingroom")
    var addinfoSmokingroom: String? = null

    @SerializedName("addinfo_wifi")
    var addinfoWifi: String? = null

    @SerializedName("cvs_lotto")
    var cvsLotto: String? = null

    @SerializedName("cvs_medicine")
    var cvsMedicine: String? = null

    @SerializedName("cvs_parcel")
    var cvsParcel: String? = null

    @SerializedName("cvs_withdrawal")
    var cvsWithdrawal: String? = null

    @SerializedName("meta_keywords_disp")
    var metaKeywordsDisp: String? = null

    @SerializedName("issue_keywords_disp")
    var issueKeywordsDisp: String? = null

    @SerializedName("pay_keywords_disp")
    var payKeywordsDisp: String? = null

    @SerializedName("oil_carwash")
    var oilCarwash: String? = null

    @SerializedName("oil_convenience")
    var oilConvenience: String? = null

    @SerializedName("oil_maintenance")
    var oilMaintenance: String? = null

    @SerializedName("oil_self")
    var oilSelf: String? = null

    @SerializedName("mobility_parking_exit_type")
    var mobilityParkingExitType: String? = null

    @SerializedName("display_restrict_type")
    var displayRestrictType: String? = null

    @SerializedName("display_name_info")
    var displayNameInfo: DisplayNameInfo? = null

    @SerializedName("knavi_guide_infos")
    var knaviGuideInfos: MutableList<Any>? = null

    @SerializedName("bizconnect_info")
    var bizconnectInfo: MutableList<Any>? = null

    @SerializedName("bizprice_info")
    var bizpriceInfo: MutableList<Any>? = null

    @SerializedName("benefit_info")
    var benefitInfo: MutableList<Any>? = null

    @SerializedName("mystore_type")
    var mystoreType: String? = null

    @SerializedName("storeview_id")
    var storeviewId: String? = null

    @SerializedName("grade_count")
    var gradeCount: String? = null

    @SerializedName("kplace_rating")
    var kplaceRating: String? = null

    @SerializedName("kplace_ratings_count")
    var kplaceRatingsCount: String? = null
}

class DisplayNameInfo {
    @SerializedName("dp_name1")
    var dpName1: String? = null
}