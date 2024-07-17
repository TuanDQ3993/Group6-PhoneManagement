package com.example.PhoneManagement.dto.request;

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
    String productName;
    String category;
    List<String> color;
    List<String> image;
    BigDecimal price;
    String description;
    int quantity;
    int warrantyPeriod;
    Date lastUpdated;
    Date createdAt;
}
