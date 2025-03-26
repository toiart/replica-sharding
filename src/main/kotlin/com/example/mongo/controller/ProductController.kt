package com.example.mongo.controller

import com.example.mongo.entity.Product
import com.example.mongo.repository.ProductRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(private val productRepository: ProductRepository) {

    @PostMapping
    fun createProduct(@RequestBody product: Product): ResponseEntity<Product> {
        return ResponseEntity.ok(productRepository.save(product))
    }

    @GetMapping
    fun getProductsByPriceRange(
        @RequestParam minPrice: Double,
        @RequestParam maxPrice: Double
    ): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productRepository.findByPriceBetween(minPrice, maxPrice))
    }
}
