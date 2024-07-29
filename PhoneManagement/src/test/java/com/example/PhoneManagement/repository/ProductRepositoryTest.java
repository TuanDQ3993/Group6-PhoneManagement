package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Products;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setCategoryName("Electronics");
        categoryRepository.save(category);

        Products product1 = new Products();
        product1.setProductName("Product 1");
        product1.setPrice(BigDecimal.valueOf(100.00));
        product1.setQuantity(10);
        product1.setCreatedAt(new Date());
        product1.setCategory(category);
        productRepository.save(product1);

        Products product2 = new Products();
        product2.setProductName("Product 2");
        product2.setPrice(BigDecimal.valueOf(200.00));
        product2.setQuantity(20);
        product2.setCategory(category);
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

    @Test
    void findByCategoryCategoryId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Products> productPage = productRepository.findByCategoryCategoryId(1, pageable);
        assertNotNull(productPage);
        assertEquals(2, productPage.getTotalElements());

        List<Products> products = productPage.getContent();
        assertEquals(2, products.size());
    }

}