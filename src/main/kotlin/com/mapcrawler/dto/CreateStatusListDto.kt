package com.mapcrawler.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateStatusListDto(val status: Int, val message: String? = null) {
    var storeList: MutableList<StoreInfoDto> = mutableListOf()
    var totalCount: Int? = null
}