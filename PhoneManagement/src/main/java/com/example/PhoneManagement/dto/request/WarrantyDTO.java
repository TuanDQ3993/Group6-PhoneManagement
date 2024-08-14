package com.example.PhoneManagement.dto.request;

import com.example.PhoneManagement.entity.WarrantyRepair;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarrantyDTO {
    private WarrantyRepair existingWarrantyRepair;
    private String issueDescription;
    private int orderId;
    private String image;
}
