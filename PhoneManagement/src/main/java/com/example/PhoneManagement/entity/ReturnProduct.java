package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity(name="returnproduct")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="return_id")
    int returnId;

    @Column(name= "return_date")
    Date returnDate;

    @Column(name="reason")
    String reason;

    @ManyToOne
    @JoinColumn(name="user_id")
    Users user;

    @ManyToOne
    @JoinColumn(name="product_id")
    Products products;
}
