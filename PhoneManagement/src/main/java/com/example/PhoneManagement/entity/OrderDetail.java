package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity(name = "orderdetail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_detail_id")
    int orderDetailId;

    @Column(name="quantity",nullable = false)
    int quantity;

    @Column(name="price",nullable = false, precision = 10, scale = 2)
    BigDecimal price;

    @ManyToOne
    @JoinColumn(name="order_id")
    Orders order;

    @ManyToOne
    @JoinColumn(name="product_color_id")
    ProductColor productColor;
}
