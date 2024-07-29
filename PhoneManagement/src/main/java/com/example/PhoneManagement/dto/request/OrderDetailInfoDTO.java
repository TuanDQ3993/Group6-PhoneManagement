package com.example.PhoneManagement.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailInfoDTO {
    double totalAmount;
    Date orderDate;
    String customerName;
    String address;
    String phoneNumber;
}
