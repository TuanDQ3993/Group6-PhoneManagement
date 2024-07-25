package com.example.PhoneManagement.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageableDTO {
    Integer PageSize;

    Integer PageNumber;

    public PageableDTO(int currentPage, int pageSize) {
        this.PageNumber = currentPage;
        this.PageSize = pageSize;
    }
}
