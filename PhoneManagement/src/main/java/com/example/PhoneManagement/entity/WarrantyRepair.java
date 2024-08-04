package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Entity(name = "warrantyrepair")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarrantyRepair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warranty_id")
    int warrantyId;

    @Column(name = "product_name", length = 50)
    String productName;

    @Column(name = "image", columnDefinition = "TEXT")
    String image;

    @Column(name = "status", length = 50)
    String status;

    @Column(name = "issue_description", length = 100)
    String issueDescription;

    @Column(name = "is_deleted")
    boolean isDeleted;

    @Column(name = "repair_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date repairDate;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    Users user;

    @ManyToOne
    @JoinColumn(name = "technical_id", referencedColumnName = "user_id")
    Users technical;

    @ManyToOne
    @JoinColumn(name="order_id")
    Orders order;
}

