package com.example.coroutine.webflux.repository

import com.example.coroutine.webflux.model.Product
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ProductRepository : ReactiveMongoRepository<Product, String> {
    fun findByName(name: String): Mono<Product>
}