package com.example.PhoneManagement.dto.request;

import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.entity.Key.KeyProductColor;
import com.example.PhoneManagement.entity.Products;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductColorDTO {
    Integer colorId;
    Integer quantity;
    MultipartFile image;
}
