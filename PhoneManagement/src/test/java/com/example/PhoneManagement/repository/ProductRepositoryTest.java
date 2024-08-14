package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.entity.ProductInfo;
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
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ColorRepository colorRepository;

    @BeforeEach
    void setUp() {
        // Xóa dữ liệu trước khi thêm dữ liệu mới
        productRepository.deleteAll();
        productColorRepository.deleteAll();
        categoryRepository.deleteAll();

        // Tạo category
        Category category = new Category();
        category.setCategoryName("Smartphones");
        category.setDeleted(true);
        categoryRepository.save(category);

        // Tạo màu sắc
        Colors color = new Colors();
        color.setColorName("Red");
        colorRepository.save(color);

        // Tạo sản phẩm
        Products product1 = new Products();
        product1.setProductName("Product A");
        product1.setDescription("Description of Product A");
        product1.setQuantity(10);
        product1.setWarrantyPeriod(12);
        product1.setCreatedAt(new Date());
        product1.setCategory(category);
        product1.setBrandName("Apple");

        Products product2 = new Products();
        product2.setProductName("Product B");
        product2.setDescription("Description of Product B");
        product2.setQuantity(20);
        product2.setWarrantyPeriod(24);
        product2.setCreatedAt(new Date());
        product2.setCategory(category);
        product2.setBrandName("Samsung");

        productRepository.save(product1);
        productRepository.save(product2);

        // Tạo thông tin sản phẩm
        ProductInfo productInfo1 = new ProductInfo();
        productInfo1.setProducts(product1);
        productInfo1.setColors(color);
        productInfo1.setImage("imageA.png");
        productInfo1.setQuantity(5);
        productInfo1.setLastUpdated(new Date());
        productInfo1.setDeleted(true);

        ProductInfo productInfo2 = new ProductInfo();
        productInfo2.setProducts(product2);
        productInfo2.setColors(color);
        productInfo2.setImage("imageB.png");
        productInfo2.setQuantity(15);
        productInfo2.setLastUpdated(new Date());
        productInfo2.setDeleted(true);

        productColorRepository.save(productInfo1);
        productColorRepository.save(productInfo2);
    }

    @Test
    void testGetListProduct() {
        List<Products> productsList = productRepository.getListProduct();
        assertNotNull(productsList);
        assertFalse(productsList.isEmpty());
        assertEquals(2, productsList.size());
    }

    @Test
    void testFindByCategoryCategoryId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Products> productsPage = productRepository.findByCategoryCategoryId(1, pageable);
        assertNotNull(productsPage);
        assertFalse(productsPage.isEmpty());
        assertEquals(2, productsPage.getTotalElements());
    }

    @Test
    void testFindByProductNameContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Products> productsPage = productRepository.findByProductNameContainingIgnoreCase("Product", pageable);
        assertNotNull(productsPage);
        assertFalse(productsPage.isEmpty());
        assertEquals(2, productsPage.getTotalElements());
    }

    @Test
    void testFindByCategoryCategoryIdAndProductNameContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Products> productsPage = productRepository.findByCategoryCategoryIdAndProductNameContainingIgnoreCase(1, "Product A", pageable);
        assertNotNull(productsPage);
        assertFalse(productsPage.isEmpty());
        assertEquals(1, productsPage.getTotalElements());
    }

    @Test
    void testFindTop8ByOrderByCreatedAtDesc() {
        Pageable pageable = PageRequest.of(0, 8);
        Page<Products> productsPage = productRepository.findTop8ByOrderByCreatedAtDesc(pageable);
        assertNotNull(productsPage);
        assertFalse(productsPage.isEmpty());
        assertEquals(2, productsPage.getTotalElements());
    }

    @Test
    void testFindTop8ByCategoryIdOrderByCreatedAtDesc() {
        Pageable pageable = PageRequest.of(0, 8);
        Page<Products> productsPage = productRepository.findTop8ByCategoryIdOrderByCreatedAtDesc(1, pageable);
        assertNotNull(productsPage);
        assertFalse(productsPage.isEmpty());
        assertEquals(2, productsPage.getTotalElements());
    }
}