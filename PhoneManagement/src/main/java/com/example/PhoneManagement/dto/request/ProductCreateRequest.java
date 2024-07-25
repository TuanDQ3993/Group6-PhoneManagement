package com.example.PhoneManagement.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreateRequest {
    String productName;
    int category;
    BigDecimal price;
    int quantity;
    String description;
    int warrantyPeriod;
}
