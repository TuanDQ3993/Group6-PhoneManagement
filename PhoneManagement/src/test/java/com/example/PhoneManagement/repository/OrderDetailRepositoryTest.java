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
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productsRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private OrderRepository ordersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ColorRepository colorsRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        orderDetailRepository.deleteAll();
        ordersRepository.deleteAll();
        productColorRepository.deleteAll();
        productsRepository.deleteAll();
        userRepository.deleteAll();
        colorsRepository.deleteAll();
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
        colorsRepository.save(color);

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
        order.setOrderDate(new Date());
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setStatus("Completed");
        order.setUser(user);
        ordersRepository.save(order);

        // Tạo chi tiết đơn hàng
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProductInfo(productInfo);
        orderDetail.setQuantity(2);
        orderDetail.setPrice(new BigDecimal("50.00"));
        orderDetailRepository.save(orderDetail);
    }

    @Test
    public void testFindAllOrderDetails() {
        List<Object[]> result = orderDetailRepository.findAllOrderDetails(1);
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.size());
        assertEquals("Product A", result.get(0)[2]);
        assertEquals("image.png", result.get(0)[3]);
    }

    @Test
    public void testFindTop5Sellers() {
        List<Object[]> result = orderDetailRepository.findTop5Sellers();
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.size());
        assertEquals("Product A", result.get(0)[1]);
        assertEquals("image.png", result.get(0)[2]);
        assertEquals(2L, ((Number) result.get(0)[3]).longValue());
    }

    @Test
    public void testFindTopSelling() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Products> result = orderDetailRepository.findTopSelling(pageable);
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.getTotalElements());
        assertEquals("Product A", result.getContent().get(0).getProductName());
    }
    @Test
    public void testFindTopSellingByCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Products> result = orderDetailRepository.findTopSellingByCategory(1, pageable);
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.getTotalElements());
        assertEquals("Product A", result.getContent().get(0).getProductName());
    }
}