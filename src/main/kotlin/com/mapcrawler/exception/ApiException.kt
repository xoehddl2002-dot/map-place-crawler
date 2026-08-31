package com.mapcrawler.exception

import org.springframework.http.HttpStatus

class ApiException(
    val status: HttpStatus,
    override val message: String,
) : RuntimeException(message)

class CrawlerException(
    override val message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
