package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.dto.request.PurchaseCreateRequest;
import com.example.PhoneManagement.dto.request.PurchaseDTO;
import com.example.PhoneManagement.dto.request.PurchaseDetailsDTO;
import com.example.PhoneManagement.entity.*;
import com.example.PhoneManagement.repository.ProductColorRepository;
import com.example.PhoneManagement.repository.ProductRepository;
import com.example.PhoneManagement.repository.PurchaseDetailsRepository;
import com.example.PhoneManagement.repository.PurchaseRepository;
import com.example.PhoneManagement.service.imp.PurchaseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PurchaseServiceImp implements PurchaseService {
    PurchaseRepository purchaseRepository;
    PurchaseDetailsRepository purchaseDetailsRepository;
    ProductRepository productRepository;
    ProductColorRepository productColorRepository;

    @Override
    public Page<PurchaseDTO> findAll(int userId, PageableDTO pageable) {
        try {
            PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

            Page<Purchase> purchasePage = purchaseRepository.findByUserId(userId, pageRequest);
            List<PurchaseDTO> purchaseDTOS = purchasePage.getContent().stream().map(purchase -> {
                PurchaseDTO purchaseDTO = new PurchaseDTO();
                purchaseDTO.setPurchaseId(purchase.getPurchaseId());
                purchaseDTO.setUserId(userId);
                purchaseDTO.setOrigin(purchase.getOrigin());
                purchaseDTO.setPurchaseDate(purchase.getPurchaseDate());
                purchaseDTO.setTotalAmount(purchase.getTotalAmount());
                purchaseDTO.setStatus(purchase.getStatus());
                return purchaseDTO;
            }).collect(Collectors.toList());

            return new PageImpl<>(purchaseDTOS, pageRequest, purchasePage.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty();
        }
    }

    @Override
    public void addProduct(PurchaseCreateRequest request) {
        Purchase purchase = new Purchase();
        purchase.setPurchaseDate(new Date());
        Users users = new Users();
        users.setUserId(request.getUserId());
        purchase.setUser(users);
        purchase.setOrigin(request.getOrigin());
        purchase.setStatus(request.getStatus());
        purchaseRepository.save(purchase);
    }

    public Purchase createPurchase(PurchaseDTO purchaseDTO) {
        Purchase purchase = new Purchase();

        Users users = new Users();
//        users.setUserId(purchaseDTO.getUserId());
        users.setUserId(5);
        purchase.setUser(users);
        purchase.setPurchaseDate(new Date());
        purchase.setTotalAmount(purchaseDTO.getTotalAmount());
        purchase.setOrigin(purchaseDTO.getOrigin());
        purchase.setStatus(purchaseDTO.getStatus());

        if (purchase.getPurchaseDetailList() == null) {
            purchase.setPurchaseDetailList(new ArrayList<>());
        }

        for (PurchaseDetailsDTO purchaseDetailsDTO : purchaseDTO.getPurchaseDetails()) {
            PurchaseDetail purchaseDetail = new PurchaseDetail();
            purchaseDetail.setQuantity(purchaseDetailsDTO.getQuantity());
            purchaseDetail.setPrice(purchaseDetailsDTO.getPrice());
            purchaseDetail.setPurchase(purchase);

            ProductColor productColor = productColorRepository.findById(purchaseDetailsDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product color not found"));

            purchaseDetail.setProductColor(productColor);

            purchase.getPurchaseDetailList().add(purchaseDetail);
        }

        return purchaseRepository.save(purchase);
    }

    public PurchaseDTO getPurchaseDetail(int purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not existed"));

        List<PurchaseDetailsDTO> purchaseDetailsDTOS = new ArrayList<>();
        PurchaseDTO purchaseDTO = new PurchaseDTO();

        purchaseDTO.setPurchaseId(purchase.getPurchaseId());
        purchaseDTO.setStatus(purchase.getStatus());
        purchaseDTO.setOrigin(purchase.getOrigin());
        purchaseDTO.setTotalAmount(purchase.getTotalAmount());
        purchaseDTO.setPurchaseDate(purchase.getPurchaseDate());

        for (PurchaseDetail purchaseDetail : purchase.getPurchaseDetailList()) {
            PurchaseDetailsDTO purchaseDetailsDTO = new PurchaseDetailsDTO();
            purchaseDetailsDTO.setPurchasedetailId(purchaseDetail.getPurchaseDetailId());
            purchaseDetailsDTO.setProductId(purchaseDetail.getProductColor().getProductcolorId());
            purchaseDetailsDTO.setQuantity(purchaseDetail.getQuantity());
            purchaseDetailsDTO.setPrice(purchaseDetail.getPrice());
            purchaseDetailsDTOS.add(purchaseDetailsDTO);
        }

        purchaseDTO.setPurchaseDetails(purchaseDetailsDTOS);
        return purchaseDTO;
    }
}
