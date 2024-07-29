package com.example.PhoneManagement.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageDTO<T> {
    Integer PageSize;
    Integer PageNumber;
    public PageDTO(int currentPage, int pageSize) {
        this.PageNumber = currentPage;
        this.PageSize = pageSize;
    }
}
