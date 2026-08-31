package com.mapcrawler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MapPlaceCrawlerApplication

fun main(args: Array<String>) {
	runApplication<MapPlaceCrawlerApplication>(*args)
}
