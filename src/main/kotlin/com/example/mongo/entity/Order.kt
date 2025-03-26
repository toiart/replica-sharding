package com.example.mongo.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "orders")
data class Order(
    @Id val id: String? = null,
    val userId: String,
    val productId: String,
    val createdAt: Instant = Instant.now()
)
