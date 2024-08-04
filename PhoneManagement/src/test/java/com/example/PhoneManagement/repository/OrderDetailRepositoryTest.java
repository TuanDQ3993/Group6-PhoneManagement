package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

    @BeforeEach
    void setUp() {
        orderDetailRepository.deleteAll();
        ordersRepository.deleteAll();
        productColorRepository.deleteAll();
        productsRepository.deleteAll();
        userRepository.deleteAll();
        colorsRepository.deleteAll();

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

        // Tạo sản phẩm
        Products product = new Products();
        product.setProductName("Product A");
        product.setDescription("Description of Product A");
        product.setQuantity(10);
//        product.setPrice(new BigDecimal("100.00"));
        product.setWarrantyPeriod(12);
        product.setCreatedAt(new Date());
        productsRepository.save(product);

        // Tạo màu sắc của sản phẩm
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProducts(product);
        productInfo.setColors(color);
        productInfo.setImage("image.png");
        productInfo.setQuantity(5);
        productInfo.setLastUpdated(new Date());
        productColorRepository.save(productInfo);

        // Tạo đơn hàng
        Orders order = new Orders();
        order.setSalerId(1);
        order.setOrderDate(new Date());
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setStatus("Pending");
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
    void findAllOrderDetails() {
        List<Object[]> orderDetails = orderDetailRepository.findAllOrderDetails(1);
        assertNotNull(orderDetails);
        assertEquals(1, orderDetails.size());

        Object[] orderDetail = orderDetails.get(0);
        assertEquals(1, orderDetail[0]);
        assertEquals(1, orderDetail[1]);
        assertEquals("Product A", orderDetail[2]);
        assertEquals("image.png", orderDetail[3]);
        assertEquals(2, orderDetail[4]);
        assertEquals(new BigDecimal("50.00"), orderDetail[5]);
    }

    @Test
    void findTop5Sellers() {
        List<Object[]> topSellers = orderDetailRepository.findTop5Sellers();
        assertNotNull(topSellers);
        assertFalse(topSellers.isEmpty());

        Object[] topSeller = topSellers.get(0);
        assertEquals(1, ((Number) topSeller[0]).intValue()); // ProductID
        assertEquals("Product A", topSeller[1]); // ProductName
        assertEquals("image.png", topSeller[2]); // Image
        assertEquals(2, ((Number) topSeller[3]).intValue()); // TotalQuantitySold
    }
}