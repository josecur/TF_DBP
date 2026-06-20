package com.mindshift

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MindshiftBackendKotlinApplication

fun main(args: Array<String>) {
	runApplication<MindshiftBackendKotlinApplication>(*args)
}
