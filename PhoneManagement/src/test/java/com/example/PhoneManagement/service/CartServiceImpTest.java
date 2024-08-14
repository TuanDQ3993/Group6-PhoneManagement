package com.example.PhoneManagement.service;

import com.example.PhoneManagement.entity.*;
import com.example.PhoneManagement.repository.*;
import com.example.PhoneManagement.util.Cart;
import com.example.PhoneManagement.util.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImpTest {

    @InjectMocks
    private CartServiceImp cartService;

    @Mock
    private ProductColorRepository productColorRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private Users user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductInfoById() {

        int productId = 1;
        ProductInfo productInfo = new ProductInfo();
        when(productColorRepository.findById(productId)).thenReturn(Optional.of(productInfo));


        ProductInfo result = cartService.getProductInfoById(productId);


        assertNotNull(result);
        assertEquals(productInfo, result);
        verify(productColorRepository, times(1)).findById(productId);
    }


    @Test
    void testGetSaleMinOrder() {
        when(orderRepository.getSaleMinOrder()).thenReturn(1);

        int result = cartService.getSaleMinOrder();

        assertEquals(1, result);
        verify(orderRepository, times(1)).getSaleMinOrder();
    }

    @Test
    void testGetQuantityProduct() {

        int productId = 1;
        when(productColorRepository.getQuantityProduct(productId)).thenReturn(5);


        int result = cartService.getQuantityProduct(productId);


        assertEquals(5, result);
        verify(productColorRepository, times(1)).getQuantityProduct(productId);
    }
}
