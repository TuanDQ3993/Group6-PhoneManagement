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
public class OrderInfoDTO {
    int orderID;
    String productName;
    String image;
    double totalAmount;
    Date orderDate;
    String username;
    int countP;
    String status;
    int salerId;
}
