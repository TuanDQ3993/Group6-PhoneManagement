package com.example.PhoneManagement.dto.request;

import com.example.PhoneManagement.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseDTO {
    int purchaseId;
    int userId;
    BigDecimal totalAmount;
    String origin;
    String status;
    Date purchaseDate;
    List<PurchaseDetailsDTO> purchaseDetails = new ArrayList<>();
}
