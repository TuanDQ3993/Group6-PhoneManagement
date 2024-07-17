package com.example.PhoneManagement.entity;

import com.example.PhoneManagement.entity.Key.KeyProductColor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;


@Entity(name = "productcolor")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_color_id")
    int productcolorId;

    @ManyToOne
    @JoinColumn(name="product_id",insertable = false, updatable = false)
    Products products;

    @ManyToOne
    @JoinColumn(name="color_id",insertable = false, updatable = false)
    Colors colors;

    @Lob
    @Column(name="image",columnDefinition = "text")
    String image;

    @Column(name="quantity")
    int quantity;

    @Column(name="last_updated")
    Date lastUpdated;
}
