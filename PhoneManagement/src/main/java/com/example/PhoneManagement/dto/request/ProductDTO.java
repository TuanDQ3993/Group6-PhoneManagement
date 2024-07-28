package com.example.PhoneManagement.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO {
     Integer productId;
     String productName;
     String description;
     Integer quantity;
     BigDecimal price;
     Integer warrantyPeriod;
     Integer categoryId;
     Date createAt;
     List<ProductColorDTO> colors=new ArrayList<>();

     public ProductDTO(Integer productId, String productName) {
          this.productId = productId;
          this.productName = productName;
     }
}
