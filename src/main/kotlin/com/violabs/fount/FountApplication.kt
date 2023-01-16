package com.violabs.fount

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FountApplication

fun main(args: Array<String>) {
	runApplication<FountApplication>(*args)
}
