package com.mapcrawler.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

@Configuration
@EnableScheduling
class SchedulerConfig:SchedulingConfigurer {
    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val scheduler= ThreadPoolTaskScheduler()
        scheduler.poolSize=1
        scheduler.setThreadNamePrefix("my-shceduled-task-pool")
        scheduler.initialize()
        taskRegistrar.setScheduler(scheduler)
    }
}