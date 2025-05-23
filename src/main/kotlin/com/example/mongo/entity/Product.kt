package com.example.mongo.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "products")
data class Product(
    @Id val id: String? = null,
    val name: String,
    val price: Double  // Shard Key
)
