package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Entity(name = "Color")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Colors {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="color_id")
    int colorId;

    @Column(name = "color_name")
    String colorName;

    @OneToMany(mappedBy = "colors")
    List<ProductColor> productColorList;

}
