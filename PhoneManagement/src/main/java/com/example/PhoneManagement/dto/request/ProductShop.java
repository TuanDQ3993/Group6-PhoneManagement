package com.example.PhoneManagement.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductShop {
    int id;
    int category_id;
    int color_id;
    String description;
    String productName;
    BigDecimal price;
    String image;
    int quantity;
}
