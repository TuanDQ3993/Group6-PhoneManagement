package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Entity(name = "Sales")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sales_id")
    int salesId;

    @Column(name="quantity")
    int quantity;

    @Column(name="SalesDate")
    Date salesDate;

    @ManyToOne
    @JoinColumn(name="user_id")
    Users user;

    @ManyToOne
    @JoinColumn(name="product_id")
    Products products;

}
