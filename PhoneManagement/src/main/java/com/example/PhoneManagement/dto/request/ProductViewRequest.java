package com.example.PhoneManagement.dto.request;

import com.example.PhoneManagement.entity.Colors;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductViewRequest {
    int productId;
    List<Integer> proColorId;
    String productName;
    int category;
    List<Integer> colorId;
    List<String> colorName;
    List<String> image;
    BigDecimal price;
    String description;
    List<Integer> quantity;
    int warrantyPeriod;
    Date lastUpdated;
    Date createdAt;
}
