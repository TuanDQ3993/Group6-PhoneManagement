package com.example.PhoneManagement.dto.request;

import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.entity.Products;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductColorUpdate {
    int productId;
    int colorId;
    String image;
    int quantity;
    Date lastUpdated;
}
