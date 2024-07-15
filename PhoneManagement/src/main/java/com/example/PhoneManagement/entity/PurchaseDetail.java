package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity(name = "purchasedetail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="purchase_detail_id")
    int purchaseDetailId;

    @Column(name="quantity", nullable=false)
    int quantity;

    @Column(name="price",nullable=false,precision = 10, scale = 2)
    BigDecimal price;

    @ManyToOne
    @JoinColumn(name="purchase_id")
    Purchase purchase;

    @ManyToOne
    @JoinColumn(name="product_id")
    Products products;
}
