package com.example.coroutine.webflux.controller

import com.example.coroutine.webflux.model.Product
import com.example.coroutine.webflux.repository.ProductRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val productRepository: ProductRepository
) {

    private val baseUri = "/products"

    @BeforeEach
    fun setup(): Unit = runBlocking {
        productRepository.deleteAll().awaitFirstOrNull()
    }

    @Test
    fun `should add a new product and retrieve by ID`(): Unit = runBlocking {
        val newProduct = Product(name = "Test Product", description = "Test Description", price = 99.99)

        // Add product
        val productId = webTestClient.post().uri(baseUri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(newProduct)
            .exchange()
            .expectStatus().isOk
            .expectBody(Product::class.java)
            .returnResult()
            .responseBody?.id

        // Retrieve product by ID
        productId?.let {
            webTestClient.get().uri("$baseUri/$it")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.name").isEqualTo("Test Product")
                .jsonPath("$.description").isEqualTo("Test Description")
                .jsonPath("$.price").isEqualTo(99.99)
        }
    }

    @Test
    fun `should add and verify new product details`(): Unit = runBlocking {
        val newProduct = Product(name = "New Product", description = "A new test product", price = 99.99)

        webTestClient.post().uri(baseUri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(newProduct)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("New Product")
            .jsonPath("$.description").isEqualTo("A new test product")
            .jsonPath("$.price").isEqualTo(99.99)
    }

    @Test
    fun `should return 404 for non-existent product ID`(): Unit = runBlocking {
        webTestClient.get().uri("$baseUri/999")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.error").isEqualTo("Product with ID 999 not found")
    }

    @Test
    fun `should handle duplicate product gracefully`(): Unit = runBlocking {
        val duplicateProduct = Product(name = "Duplicate Product", description = "Duplicate description", price = 49.99)

        // Add the product for the first time
        webTestClient.post().uri(baseUri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(duplicateProduct)
            .exchange()
            .expectStatus().isOk

        // Attempt to add the duplicate product
        webTestClient.post().uri(baseUri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(duplicateProduct)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Product with name 'Duplicate Product' already exists.")
    }
}
