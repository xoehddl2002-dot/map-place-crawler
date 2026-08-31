package com.mapcrawler.scheduler

import com.mapcrawler.controller.NaverController
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CommonScheduler {

    @Scheduled(cron = "1 1 0 * * *")
    fun naverRequestHeaderReset() {
        NaverController.clearHeaders()
    }
}
