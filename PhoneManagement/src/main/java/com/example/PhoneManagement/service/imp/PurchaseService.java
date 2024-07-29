package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.dto.request.PurchaseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseService {
    Page<PurchaseDTO> findAll(int userId, PageableDTO pageable);
//    void addProduct(PurchaseCreateRequest request);
}
