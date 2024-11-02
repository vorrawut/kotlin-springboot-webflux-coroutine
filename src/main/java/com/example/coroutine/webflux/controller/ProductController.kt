package com.example.coroutine.webflux.controller

import com.example.coroutine.webflux.exception.ProductNotFoundException
import com.example.coroutine.webflux.model.Product
import com.example.coroutine.webflux.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    @PostMapping
    suspend fun addProduct(@RequestBody product: Product): ResponseEntity<Any> {
        return try {
            val savedProduct = productService.addProduct(product)
            ResponseEntity.ok(savedProduct)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/{id}")
    suspend fun getProductById(@PathVariable id: String): ResponseEntity<Any> {
        return try {
            val product = productService.getProductById(id)
            ResponseEntity.ok(product)
        } catch (e: ProductNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/name/{name}")
    suspend fun getProductByName(@PathVariable name: String): ResponseEntity<Any> {
        val product = productService.getProductByName(name)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Product with name '$name' not found"))
        }
    }

    @DeleteMapping("/{id}")
    suspend fun deleteProductById(@PathVariable id: String): ResponseEntity<Any> {
        return try {
            productService.deleteProductById(id)
            ResponseEntity.status(HttpStatus.NO_CONTENT).build() // No content when delete is successful
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to "Failed to delete product with id '$id'"))
        }
    }
}
