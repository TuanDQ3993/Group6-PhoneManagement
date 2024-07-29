package com.example.PhoneManagement.entity;

import com.example.PhoneManagement.entity.Key.KeyProductColor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;


@Entity(name = "productcolor")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductColor {
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

    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @OneToMany(mappedBy = "productColor")
    private List<OrderDetail> orderDetailList;
}