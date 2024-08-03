package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.entity.OrderDetail;
import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.*;
import com.example.PhoneManagement.service.imp.CartService;

import com.example.PhoneManagement.util.Cart;
import com.example.PhoneManagement.util.Item;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CartServiceImp implements CartService {

    @Autowired
    ProductColorRepository productColorRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public ProductInfo getProductInfoById(int id) {
        return productColorRepository.findById(id).get();
    }


    @Override
    public void addOrder(Users user, Cart cart, String name, String address, String tel, String note, String payment,String status) {

        var order = Orders.builder()
                .user(user)
                .salerId(2)
                .orderDate(new Date())
                .totalAmount(BigDecimal.valueOf(cart.getTotalPrice()))
                .status(status)
                .note(note)
                .receiver(name)
                .address(address)
                .phoneNumber(tel)
                .payment(payment)
                .build();
        orderRepository.save(order);

        for (Item item : cart.getItems()) {
        var orderDetail= OrderDetail.builder()
                .order(order)
                .productInfo(item.getProductColor())
                .quantity(item.getQuantity())
                .price(BigDecimal.valueOf(item.getPrice()))
                .build();
        orderDetailRepository.save(orderDetail);

            ProductInfo productInfo = productColorRepository.findById(item.getProductColor().getProductcolorId()).get();
            productInfo.setQuantity(productInfo.getQuantity() - item.getQuantity());
            productColorRepository.save(productInfo);
        }
    }
}
