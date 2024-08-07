package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductColorRepositoryTest {

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private ProductRepository productsRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws ParseException {
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
        productColorRepository.deleteAll();
        productsRepository.deleteAll();
        userRepository.deleteAll();
        colorRepository.deleteAll();
        categoryRepository.deleteAll();

        // Tạo người dùng
        Users user = new Users();
        user.setUserName("haoquang@example.com");
        user.setPassword("123");
        user.setFullName("Quang Test");
        user.setAddress("Vinh Test");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        // Tạo màu sắc
        Colors color = new Colors();
        color.setColorName("Red");
        colorRepository.save(color);

        //Tao category
        Category category = new Category();
        category.setDeleted(true);
        category.setCategoryName("Smartphones");
        categoryRepository.save(category);

        // Tạo sản phẩm
        Products product = new Products();
        product.setProductName("Product A");
        product.setDescription("Description of Product A");
        product.setQuantity(10);
//        product.setPrice(new BigDecimal("100.00"));
        product.setWarrantyPeriod(12);
        product.setCreatedAt(new Date());
        product.setCategory(category);
        product.setBrandName("Apple");
        productsRepository.save(product);

        // Tạo màu sắc của sản phẩm
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProducts(product);
        productInfo.setColors(color);
        productInfo.setImage("image.png");
        productInfo.setQuantity(5);
        productInfo.setLastUpdated(new Date());
        productInfo.setDeleted(true);
        productColorRepository.save(productInfo);

        // Tạo đơn hàng
        Orders order = new Orders();
        order.setSalerId(1);
        order.setOrderDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-07-01"));
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setStatus("Completed");
        order.setUser(user);
        orderRepository.save(order);

        // Tạo chi tiết đơn hàng
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProductInfo(productInfo);
        orderDetail.setQuantity(2);
        orderDetail.setPrice(new BigDecimal("50.00"));
        orderDetailRepository.save(orderDetail);
    }


    @Test
    void findTop4ByCategoryIdOrderByCreatedAtDesc() {
        Pageable pageable = PageRequest.of(0, 4);
        Page<Products> productsPage = productColorRepository.findTop4ByCategoryIdOrderByCreatedAtDesc(1, pageable);
        assertNotNull(productsPage);
        assertFalse(productsPage.isEmpty());
        assertEquals(1, productsPage.getTotalElements());
        assertEquals("Product A", productsPage.getContent().get(0).getProductName());
    }

    @Test
    void getQuantityProduct() {
        int quantity = productColorRepository.getQuantityProduct(1);
        assertEquals(5, quantity);
    }
}