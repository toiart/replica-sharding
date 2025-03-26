package com.example.mongo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShardingApplication

fun main(args: Array<String>) {
	runApplication<ShardingApplication>(*args)
}
