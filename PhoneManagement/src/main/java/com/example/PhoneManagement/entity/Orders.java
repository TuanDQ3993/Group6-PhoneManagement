package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity(name = "Orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    int orderId;

    @Column(name = "saler_id")
    int salerId;

    @Column(name = "order_date")
    Date orderDate;

    @Column(name="total_amount", precision = 10, scale = 2)
    BigDecimal totalAmount;

    @Column(name="status",length = 50)
    String status;

    @ManyToOne
    @JoinColumn(name="user_id")
    Users user;

    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetails;
}
