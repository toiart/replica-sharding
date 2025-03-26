package com.example.mongo.repository

import com.example.mongo.entity.Product
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ProductRepository : MongoRepository<Product, String> {
    @Query("{ 'price': { '\$gte': ?0, '\$lte': ?1 } }")
    fun findByPriceBetween(minPrice: Double, maxPrice: Double): List<Product>
}
