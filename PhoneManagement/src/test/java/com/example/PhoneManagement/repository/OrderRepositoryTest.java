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
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productsRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ColorRepository colorsRepository;

    @BeforeEach
    void setUp() {
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
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
        product.setPrice(new BigDecimal("100.00"));
        product.setWarrantyPeriod(12);
        product.setCreatedAt(new Date());
        productsRepository.save(product);

        // Tạo màu sắc của sản phẩm
        ProductColor productColor = new ProductColor();
        productColor.setProducts(product);
        productColor.setColors(color);
        productColor.setImage("image.png");
        productColor.setQuantity(5);
        productColor.setLastUpdated(new Date());
        productColorRepository.save(productColor);

        // Tạo đơn hàng
        Orders order = new Orders();
        order.setSalerId(1);
        order.setOrderDate(new Date());
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setStatus("Pending");
        order.setUser(user);
        orderRepository.save(order);

        // Tạo chi tiết đơn hàng
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProductColor(productColor);
        orderDetail.setQuantity(2);
        orderDetail.setPrice(new BigDecimal("50.00"));
        orderDetailRepository.save(orderDetail);
    }

    @Test
    void findOrderedOrders() {
        List<Object[]> orderedOrders = orderRepository.findOrderedOrders();
        assertNotNull(orderedOrders);
        assertFalse(orderedOrders.isEmpty());

        Object[] orderedOrder = orderedOrders.get(0);
        assertEquals(1, orderedOrder[0]);
        assertEquals("Product A", orderedOrder[1]);
        assertEquals("image.png", orderedOrder[2]);
        assertEquals(new BigDecimal("200.00"), orderedOrder[3]);
        assertNotNull(orderedOrder[4]); // Kiểm tra ngày không null
        assertEquals("Quang Test", orderedOrder[5]);
        assertEquals(0, ((Number) orderedOrder[6]).intValue()); // countP
    }

    @Test
    void findOrderInfo() {
        List<Object[]> orderInfo = orderRepository.findOrderInfo(1);
        assertNotNull(orderInfo);
        assertFalse(orderInfo.isEmpty());

        Object[] info = orderInfo.get(0);
        assertEquals(new BigDecimal("200.00"), info[0]);
        assertNotNull(info[1]); // Kiểm tra ngày không null
        assertEquals("Vinh Test", info[2]);
        assertEquals("Quang Test", info[3]);
        assertEquals("1234567890", info[4]);
    }
}