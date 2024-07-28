package com.example.PhoneManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductTopSeller {
    int productId;
    String productName;
    String image;
    int quantitySold;
}
