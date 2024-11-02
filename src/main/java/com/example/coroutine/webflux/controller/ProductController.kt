package com.example.coroutine.webflux.controller

import com.example.coroutine.webflux.model.Product
import com.example.coroutine.webflux.service.ProductService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    @GetMapping("/{id}")
    suspend fun getProductById(@PathVariable id: String): Product? {
        return productService.getProductById(id)
    }

    @GetMapping("/name/{name}")
    suspend fun getProductByName(@PathVariable name: String): Product? {
        return productService.getProductByName(name)
    }

    @PostMapping
    suspend fun addProduct(@RequestBody product: Product): Product {
        return productService.addProduct(product)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteProductById(@PathVariable id: String) {
        productService.deleteProductById(id)
    }
}