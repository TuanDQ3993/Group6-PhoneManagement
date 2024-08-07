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
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() throws ParseException {
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
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
    void countByOrderDateBetween() throws ParseException {
        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2024-06-01");
        Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse("2024-07-31");
        long count = orderRepository.countByOrderDateBetween(startDate, endDate);
        assertEquals(1, count);
    }

    @Test
    void countByOrderDateBetweenAndStatus() throws ParseException {
        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2024-06-01");
        Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse("2024-07-31");
        long count = orderRepository.countByOrderDateBetweenAndStatus(startDate, endDate, "Completed");
        assertEquals(1, count);
    }

    @Test
    void sumTotalAmountByMonthAndStatus() {
        Double totalAmount = orderRepository.sumTotalAmountByMonthAndStatus(7);
        assertNotNull(totalAmount);
        assertEquals(200.00, totalAmount);
    }

    @Test
    void countDistinctUserId() {
        long count = orderRepository.countDistinctUserId(7);
        assertEquals(1, count);
    }

//    @Test
//    //H2 khong ho tro ham DATE() như MySQL nên khong lam được. Muon lam duoc phai thay query
//    void getSaleMinOrder() {
//        int salerId = orderRepository.getSaleMinOrder();
//        assertTrue(salerId > 0); // Assuming that there's at least one salerId, adjust as needed.
//    }

    @Test
    void findOrdersWithFiltersCategory() {
        Page<Object[]> result = orderRepository.findOrdersWithFiltersCategory(
                1, "Completed", null, PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.isEmpty());

        Object[] order = result.getContent().get(0);
        assertEquals(1, order[0]); // orderId
        assertEquals("Product A", order[1]); // productName
        assertEquals("Red", order[2]); // colorName
        assertEquals("image.png", order[3]); // image
        assertEquals(new BigDecimal("200.00"), order[5]); // totalAmount
        assertEquals(2, order[6]); // quantity
        assertNotNull(order[7]); // orderDate
        assertEquals("Smartphones", order[8]); // categoryName
        assertEquals("Completed", order[9]); // status
    }

    @Test
    void findOrderDetail() {
        List<Object[]> orderDetails = orderRepository.findOrderDetail(1);

        assertNotNull(orderDetails);
        assertFalse(orderDetails.isEmpty());

        Object[] details = orderDetails.get(0);
        assertEquals(1, details[0]); // orderId
        assertEquals("Product A", details[1]); // productName
        assertEquals("Red", details[2]); // colorName
        assertEquals("image.png", details[3]); // image
        assertEquals(new BigDecimal("200.00"), details[5]); // totalAmount
        assertEquals(2, details[6]); // quantity
        assertNotNull(details[7]); // orderDate
        assertEquals("Smartphones", details[8]); // categoryName
    }

    @Test
    void countOrdersByUserId() {
        int count = orderRepository.countOrdersByUserId(1);
        assertEquals(1, count);
    }
}