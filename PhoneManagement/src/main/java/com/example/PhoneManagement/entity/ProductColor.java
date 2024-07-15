package com.example.PhoneManagement.entity;

import com.example.PhoneManagement.entity.Key.KeyProductColor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Entity(name = "productcolor")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductColor {

    @EmbeddedId
    KeyProductColor keyProductColor;

    @ManyToOne
    @JoinColumn(name="product_id",insertable = false, updatable = false)
    Products products;

    @ManyToOne
    @JoinColumn(name="color_id",insertable = false, updatable = false)
    Colors colors;

    @Column(name="image")
    String image;

    @Column(name="quantity")
    int quantity;

    @Column(name="last_updated")
    Date lastUpdated;




}
