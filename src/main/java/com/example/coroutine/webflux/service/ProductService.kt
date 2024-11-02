package com.example.coroutine.webflux.service

import com.example.coroutine.webflux.model.Product
import com.example.coroutine.webflux.repository.ProductRepository
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    suspend fun getProductById(id: String): Product? {
        return productRepository.findById(id).awaitSingle()
    }

    suspend fun getProductByName(name: String): Product? {
        return productRepository.findByName(name).awaitSingle()
    }

    suspend fun addProduct(product: Product): Product {
        return productRepository.save(product).awaitSingle()
    }

    suspend fun deleteProductById(id: String) {
        productRepository.deleteById(id).awaitSingleOrNull()
    }
}