package com.mapcrawler.controller

import com.mapcrawler.utils.WorkPoolUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CommonController(val workPoolUtils: WorkPoolUtils)
{
    @GetMapping("/sessions")
    fun sessions(): Map<String, Int> = workPoolUtils.getWorkMap()
}
