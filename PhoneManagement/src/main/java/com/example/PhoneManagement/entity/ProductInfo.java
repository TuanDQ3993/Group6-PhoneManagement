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


@Entity(name = "productinfo")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_color_id")
    private int productcolorId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products products;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private Colors colors;

    @Lob
    @Column(name = "image", columnDefinition = "TEXT")  
    private String image;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name="price", precision = 10, scale = 2)
    BigDecimal price ;

    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;


    @Column(name="isdeleted")
    boolean isDeleted;

    @OneToMany(mappedBy = "productInfo")
    private List<OrderDetail> orderDetailList;
}