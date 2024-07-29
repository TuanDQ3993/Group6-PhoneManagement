package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Products;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        Products product1 = new Products();
        product1.setProductName("Product 1");
        product1.setPrice(BigDecimal.valueOf(100.00));
        product1.setQuantity(10);
        product1.setCreatedAt(new Date());
        productRepository.save(product1);

        Products product2 = new Products();
        product2.setProductName("Product 2");
        product2.setPrice(BigDecimal.valueOf(200.00));
        product2.setQuantity(20);
        product2.setCreatedAt(new Date());
        productRepository.save(product2);
    }

    @Test
    void getListProduct() {
        List<Products> products = productRepository.getListProduct();
        assertEquals(2, products.size());
        assertEquals("Product 1", products.get(0).getProductName());
        assertEquals("Product 2", products.get(1).getProductName());
    }

}