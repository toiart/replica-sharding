package com.example.mongo.repository

import com.example.mongo.entity.Order
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepository : MongoRepository<Order, String> {
    fun findByUserId(userId: String): List<Order>
}
