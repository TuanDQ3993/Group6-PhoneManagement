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

@Entity(name = "Purchase")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="purchase_id")
    int purchaseId;

    @Column(name="purchase_date")
    Date purchaseDate;

    @Column(name="total_amount", precision = 10, scale = 2)
    BigDecimal totalAmount;

    @Column(name="origin",length = 50)
    String origin;

    @Column(name="status",length = 50)
    String status;

    @ManyToOne
    @JoinColumn(name="user_id")
    Users user;

    @OneToMany(mappedBy = "purchase")
    List<PurchaseDetail> purchaseDetailList;
}
