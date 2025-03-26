package com.example.mongo.controller

import com.example.mongo.entity.Order
import com.example.mongo.repository.OrderRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(private val orderRepository: OrderRepository) {

    @PostMapping
    fun createOrder(@RequestBody order: Order): Order {
        return orderRepository.save(order)
    }

    @GetMapping("/{userId}")
    fun getOrdersByUser(@PathVariable userId: String): List<Order> {
        return orderRepository.findByUserId(userId)
    }
}

