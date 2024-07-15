package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@Entity(name = "Product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    int productId;

    @Column(name="product_name")
    String productName;

    @Column(name="description")
    String description;

    @Column(name = "quantity")
    int quantity;

    @Column(name="price")
    float price ;

    @Column(name="warranty_period")
    int warrantyPeriod;

    @Column(name="created_at")
    Date createdAt;


    @ManyToOne
    @JoinColumn(name="category_id")
    Category category;

    @OneToMany(mappedBy = "products")
    List<ProductColor> productColorList;

    @OneToMany(mappedBy = "products")
    List<Sales> salesList;

    @OneToMany(mappedBy = "products")
    List<WarrantyRepair> warrantyRepairList;

    @OneToMany(mappedBy = "products")
    List<ReturnProduct> returnProductList;


}
