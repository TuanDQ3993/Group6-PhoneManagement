package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity(name="warrantyrepair")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarrantyRepair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="warranty_id")
    int warrantyId;

    @Column(name="issue_description")
    String issueDescription;

    @Column(name="repair_date")
    Date repairDate;

    @Column(name="technical_id")
    int technicalId;

    @ManyToOne
    @JoinColumn(name="user_id")
    Users user;

    @ManyToOne
    @JoinColumn(name="product_id")
    Products products;


}
