package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity(name = "Orders")
@Data
@Builder
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

    @Column(name = "note", length = 250)
    String note;

    @Column(name = "receiver", length = 50)
    String receiver;

    @Column(name = "address", length = 250)
    String address;

    @Column(name = "phone_number", length = 50)
    String phoneNumber;

    @Column(name = "payment", length = 20)
    String payment;

    @ManyToOne
    @JoinColumn(name="user_id")
    Users user;

    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetails;
}
