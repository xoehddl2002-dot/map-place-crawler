package com.mapcrawler.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ErrorResponseDto(val status: Int, val message: String)

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(exception: ApiException): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(exception.status)
            .body(ErrorResponseDto(exception.status.value(), exception.message))

    @ExceptionHandler(CrawlerException::class)
    fun handleCrawlerException(exception: CrawlerException): ResponseEntity<ErrorResponseDto> {
        log.error("Upstream crawling request failed", exception)
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(ErrorResponseDto(HttpStatus.BAD_GATEWAY.value(), "upstream service failed"))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(exception: IllegalArgumentException): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ErrorResponseDto(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.message ?: "service unavailable"))
}
