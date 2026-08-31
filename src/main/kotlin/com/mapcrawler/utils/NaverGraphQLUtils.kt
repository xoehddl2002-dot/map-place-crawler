package com.mapcrawler.utils

import com.google.gson.Gson
import org.json.JSONObject
import org.springframework.core.io.ClassPathResource


interface BasePostData
class NaverPlaceListPostData(private val operationName: String, private val query: String) : BasePostData {
    class Variables {
        class Input {
            val adult: Boolean = false
            val spq: Boolean = false
            val queryRank: String = ""
            val sortingOrder: String = "distance"
            val deviceType: String = "pc"

            var query: String? = null
            var display: Int? = null
            var start: Int? = null
        }

        val useReverseGeocode: Boolean = false
        val isNmap: Boolean = false
        val isBounds = false
        var input: Input = Input()
    }

    var variables: Variables = Variables()

    override fun toString(): String {
        var json: JSONObject? = JSONObject()
        if (json != null) {
            json.put("operationName", operationName)
            val input = JSONObject()
            input.put("adult", variables.input.adult)
            input.put("spq", variables.input.spq)
            input.put("queryRank", variables.input.queryRank)
            input.put("sortingOrder", variables.input.sortingOrder)
            input.put("deviceType", variables.input.deviceType)
            input.put("query", variables.input.query)
            input.put("display", variables.input.display)
            input.put("start", variables.input.start)

            val variables = JSONObject()
            variables.put("useReverseGeocode", this.variables.useReverseGeocode)
            variables.put("isNmap", this.variables.isNmap)
            variables.put("isBounds", this.variables.isBounds)
            variables.put("input", input)

            json.put("variables", variables)
            json.put("query", query)
        }
        val jsonStr = json.toString()
        return jsonStr
    }
}

class NaverPhotoViewerItemsPostData(val operationName: String, val query: String) : BasePostData {
    class Input(val businessId: String) {
        class Cursors {
            val id: String = "imgSas"
        }

        val businessType: String = "restaurant"
        val cursors: MutableList<Cursors> = mutableListOf(Cursors())
        val excludeAuthorIds: MutableList<Any> = mutableListOf()
        val excludeSection: MutableList<Any> = mutableListOf()
        val filter: String = "외부"
    }

    class Variables {
        lateinit var input: Input
    }

    var variables: Variables = Variables()

    override fun toString(): String {
        return Gson().toJson(this)
    }
}

class NaverRestaurantsPostData(val operationName: String, val query: String) : BasePostData {
    class Variables {
        class Input {
            var adult: Boolean = false
            var businessType: String = "place"
            var deviceType: String = "pc"
            var display: Int? = null
            var filterKtOtherLocal: Boolean = false
            var query: String? = null
            var queryRank: String = ""
            var spq: Boolean = false
            var start: Int? = null
            var x:String? = null
            var y:String? = null
        }
        val input: Input = Input()
        val isBounds: Boolean = false
        val isNmap: Boolean = false
        var useReverseGeocode: Boolean = false
    }

    var variables: Variables = Variables()

    override fun toString(): String {
        return Gson().toJson(this)
    }
}


object NaverGraphQLUtils {
    fun getPlacesListPostData(): NaverPlaceListPostData {
        val queryPath = "graphql/getPlacesList.graphql"
        val query = String(ClassPathResource(queryPath).contentAsByteArray)
        return NaverPlaceListPostData("getPlacesList", query)
    }

    fun getRestaurantListPostData(): NaverRestaurantsPostData {
        val queryPath = "graphql/getRestaurantList.graphql"
        val query = String(ClassPathResource(queryPath).contentAsByteArray)
        return NaverRestaurantsPostData("getPlacesList", query)
    }

    fun getPhotoViewerItemsPostData(code: String): NaverPhotoViewerItemsPostData {
        val queryPath = "graphql/getPhotoViewerItems.graphql"
        val query = String(ClassPathResource(queryPath).contentAsByteArray)

        return NaverPhotoViewerItemsPostData("getPhotoViewerItems", query).apply {
            this.variables.apply {
                this.input = NaverPhotoViewerItemsPostData.Input(code)
            }
        }
    }
}