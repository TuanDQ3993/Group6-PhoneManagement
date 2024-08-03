package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.util.Cart;

public interface CartService {
    ProductInfo getProductInfoById(int id);

    void addOrder(Users user, Cart cart, String name, String address, String tel, String note, String payment,String status);
}
