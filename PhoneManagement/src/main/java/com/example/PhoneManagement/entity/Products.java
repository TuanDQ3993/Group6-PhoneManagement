package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity(name = "Products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    int productId;

    @Column(name="product_name", nullable = false, length = 100)
    String productName;

    @Lob
    @Column(name="description",columnDefinition = "text")
    String description;

    @Column(name = "quantity")
    int quantity;

    @Column(name="price", precision = 10, scale = 2)
    BigDecimal price ;

    @Column(name="warranty_period")
    int warrantyPeriod;

    @Column(name="created_at")
    Date createdAt;

    @ManyToOne
    @JoinColumn(name="category_id")
    Category category;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductColor> productColorList = new ArrayList<>();

    @OneToMany(mappedBy = "products")
    List<WarrantyRepair> warrantyRepairList;

    @OneToMany(mappedBy = "products")
    List<OrderDetail> orderDetailList;


}
