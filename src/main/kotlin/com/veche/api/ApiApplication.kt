package com.veche.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * TODO()
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
class ApiApplication

/**
 * TODO()
 *
 * @param args TODO()
 */
fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
