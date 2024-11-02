package com.example.coroutine.webflux.service

import com.example.coroutine.webflux.exception.ProductNotFoundException
import com.example.coroutine.webflux.model.Product
import com.example.coroutine.webflux.repository.ProductRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    suspend fun getProductById(id: String): Product {
        return productRepository.findById(id).awaitSingleOrNull()
            ?: throw ProductNotFoundException("Product with ID $id not found")
    }

    suspend fun getProductByName(name: String): Product? {
        return productRepository.findByName(name).awaitSingle()
    }

    suspend fun addProduct(product: Product): Product {
        // Check if a product with the same name already exists
        val existingProduct = productRepository.findByName(product.name).awaitFirstOrNull()
        if (existingProduct != null) {
            throw IllegalArgumentException("Product with name '${product.name}' already exists.")
        }

        // Save the product if it does not exist
        return productRepository.save(product).awaitSingle()
    }

    suspend fun deleteProductById(id: String) {
        productRepository.deleteById(id).awaitSingleOrNull()
    }
}