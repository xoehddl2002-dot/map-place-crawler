package com.mapcrawler.dto.naver

import com.mapcrawler.dto.StoreInfoDto

class NaverGeocodeStoreDto : StoreInfoDto {
    var code: Code? = null
    var region: Region? = null

    class Code {
        var id: String? = null
        var mappingId: String? = null
        var type: String? = null
    }

    class Region {
        var area0: Area? = null
        var area1: Area? = null
        var area2: Area? = null
        var area3: Area? = null
        var area4: Area? = null
    }

    class Area {
        var coords: Coords? = null
        var name: String? = null
        var alias: String? = null
    }

    class Coords {
        class Center {
            var crs: String? = null
            var x: Double? = null
            var y: Double? = null
        }

        var center: Center? = null
    }
}

