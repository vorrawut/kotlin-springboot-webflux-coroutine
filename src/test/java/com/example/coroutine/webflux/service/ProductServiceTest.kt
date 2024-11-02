package com.example.coroutine.webflux.service

import com.example.coroutine.webflux.exception.ProductNotFoundException
import com.example.coroutine.webflux.model.Product
import com.example.coroutine.webflux.repository.ProductRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just

class ProductServiceTest {

    private val productRepository: ProductRepository = mockk()
    private val productService = ProductService(productRepository)

    private val sampleProduct = Product(id = "1", name = "Sample Product", description = "Sample Description", price = 100.0)

    @BeforeEach
    fun setup() {
        clearMocks(productRepository) // Clears previous interactions to start fresh for each test
    }

    @Test
    fun `should return product by ID when found`() = runBlocking {
        // Arrange
        coEvery { productRepository.findById("1") } returns just(sampleProduct)

        // Act
        val product = productService.getProductById("1")

        // Assert
        assertNotNull(product)
        assertEquals("Sample Product", product.name)
        assertEquals("Sample Description", product.description)
        assertEquals(100.0, product.price)
    }

    @Test
    fun `should throw ProductNotFoundException when product by ID not found`() = runBlocking {
        // Arrange
        coEvery { productRepository.findById("999") } returns empty()

        // Act & Assert
        val exception = assertThrows<ProductNotFoundException> {
            productService.getProductById("999")
        }
        assertEquals("Product with ID 999 not found", exception.message)
    }

    @Test
    fun `should add new product when name is unique`() = runBlocking {
        // Arrange
        coEvery { productRepository.findByName(sampleProduct.name) } returns empty()
        coEvery { productRepository.save(sampleProduct) } returns just(sampleProduct)

        // Act
        val savedProduct = productService.addProduct(sampleProduct)

        // Assert
        assertNotNull(savedProduct)
        assertEquals("Sample Product", savedProduct.name)
        coVerify { productRepository.save(sampleProduct) }
    }

    @Test
    fun `should throw IllegalArgumentException when adding a duplicate product`() = runBlocking {
        // Arrange
        coEvery { productRepository.findByName(sampleProduct.name) } returns just(sampleProduct)

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            productService.addProduct(sampleProduct)
        }
        assertEquals("Product with name 'Sample Product' already exists.", exception.message)
    }

    @Test
    fun `should delete product by ID`(): Unit = runBlocking {
        // Arrange
        coEvery { productRepository.deleteById("1") } returns empty()

        // Act
        productService.deleteProductById("1")

        // Assert
        coVerify { productRepository.deleteById("1") }
    }
}
