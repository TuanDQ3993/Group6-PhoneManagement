package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.dto.request.UserDTO;

import com.example.PhoneManagement.entity.OrderDetail;
import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.OrderRepository;
import com.example.PhoneManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImpTest {

    @InjectMocks
    private OrderServiceImp orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetListOrder() {
        // Arrange
        Orders order = new Orders();
        order.setOrderId(1);
        // Add more fields to the order as needed

        when(orderRepository.findOrderedOrders()).thenReturn(Collections.singletonList(new Object[]{1, "Product Name", "Image URL", 100.0, new java.util.Date(), "User", 1, "Pending", 1}));

        // Act
        List<OrderInfoDTO> result = orderService.getListOrder();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getOrderID());
    }

    @Test
    void testGetOrderInfo() {
        // Arrange
        Orders order = new Orders();
        order.setOrderId(1);
        // Add more fields to the order as needed

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // Act
        Orders result = orderService.getOrderInfo(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
    }

    @Test
    void testChangeStatusOrder() {
        // Arrange
        Orders order = new Orders();
        order.setOrderId(1);
        order.setStatus("Pending");

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // Act
        orderService.changeStatusOrder(1, "Completed");

        // Assert
        assertEquals("Completed", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testFindPaginated() {
        // Arrange
        UserDTO user = new UserDTO();
        user.setUserName("testUser");
        user.setRoleName("USER");

        // Set up mock data for orders
        Orders order1 = new Orders();
        order1.setOrderId(1);
        order1.setOrderDate(new java.util.Date());

        Orders order2 = new Orders();
        order2.setOrderId(2);
        order2.setOrderDate(new java.util.Date());

        when(userRepository.findIdByUserName("testUser")).thenReturn(1);
        when(orderRepository.findOrderedOrders()).thenReturn(Collections.singletonList(new Object[]{1, "Product Name", "Image URL", 100.0, new java.util.Date(), "User", 1, "Pending", 1}));

        // Act
        Page<OrderInfoDTO> page = orderService.findPaginated(new PageableDTO(0, 10), LocalDate.now().minusDays(30), LocalDate.now(), null, null, user);

        // Assert
        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testCountOrdersInCurrentMonth() {
        // Arrange
        when(orderRepository.countByOrderDateBetween(any(), any())).thenReturn(5L);

        // Act
        long count = orderService.countOrdersInCurrentMonth();

        // Assert
        assertEquals(5, count);
    }


    @Test
    void testGetAllSale() {

        Users user = new Users();
        user.setUserId(1);
        List<Users> sales = Collections.singletonList(user);
        when(userRepository.getAllSale()).thenReturn(sales);


   List<Users> result = orderService.getAllSale();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
    }

    @Test
    void testCountTotalOrders() {

        UserDTO user = new UserDTO();
        user.setUserName("testUser");
        when(userRepository.findIdByUserName("testUser")).thenReturn(1);
        when(orderRepository.countOrdersByUserId(1)).thenReturn(3);


        int count = orderService.countTotalOrders(user);


        assertEquals(3, count);
    }

    @Test
    void testFindOrderDetail() {
        when(orderRepository.findOrderDetail(1)).thenReturn(Collections.singletonList(new Object[]{1, "Product Name", 2, 100.0}));

        List<Object[]> details = orderService.findOrderDetail(1);

        assertNotNull(details);
        assertEquals(1, details.size());
        assertEquals("Product Name", details.get(0)[1]);
    }

}
