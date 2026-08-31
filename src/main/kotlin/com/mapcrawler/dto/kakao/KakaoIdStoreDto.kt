package com.mapcrawler.dto.kakao

import com.mapcrawler.dto.StoreInfoDto

class KakaoIdStoreDto : StoreInfoDto {
    var isMapUser: String? = null
    var isExist: Boolean? = null
    var basicInfo: BasicInfo? = null
    var findway: Any? = null
    var bokjiroInfo: Any? = null
    var photo: Any? = null
    var placeSubscribeInfo: Any? = null
    var trendRank: Any? = null
    var placeOwnerInfos: Any? = null
}

class BasicInfo {
    var cid: Int? = null
    var placenamefull: String? = null
    var phonenum: String? = null
    var address: Address? = null
    var wpointx: Int? = null
    var wpointy: Int? = null
    var roadview: Any? = null
    var category: Category? = null
    var feedback: Any? = null
    var payments: Any? = null
    var tags: MutableList<String>? = null
    var source: Any? = null
    var regions: MutableList<Any>? = null
    var isStation: Boolean? = null
}


class Category {
    var cateid: String? = null
    var catename: String? = null
    var cate1name: String? = null
    var fullCateIds: String? = null
}

class Address {
    var newaddr: Newaddr? = null
    var region: Region? = null
    var addrbunho: String? = null
}

class Newaddr {
    var newaddrfull: String? = null
    var bsizonno: String? = null
}

class Region {
    var name3: String? = null
    var fullname: String? = null
    var newaddrfullname: String? = null
}
