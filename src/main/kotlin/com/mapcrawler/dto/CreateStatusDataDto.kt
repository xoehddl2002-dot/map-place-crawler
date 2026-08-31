package com.mapcrawler.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateStatusDataDto(var status: Int, var message: String? = null) {

    var store: StoreInfoDto? = null
}