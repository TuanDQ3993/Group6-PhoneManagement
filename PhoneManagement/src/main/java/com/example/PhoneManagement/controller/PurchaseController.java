package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.ProductColor;
import com.example.PhoneManagement.service.ProductServiceImp;
import com.example.PhoneManagement.service.PurchaseServiceImp;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/warehouse/purchase")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PurchaseController {
    PurchaseServiceImp purchaseServiceImp;
    ProductServiceImp productServiceImp;

    @GetMapping
    public String getAll(Model model,  @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "5") int size){
        PageableDTO pageableDTO = new PageableDTO(page, size);
        Page<PurchaseDTO> purchasePage = purchaseServiceImp.findAll(3, pageableDTO);
        model.addAttribute("purchasePage", purchasePage);
        model.addAttribute("product",productServiceImp.findAllProduct());
        model.addAttribute("purchaseDTO", new PurchaseDTO());
        model.addAttribute("pageSize", size);
        return "purchaselist";
    }

    @PostMapping("/add")
    public String createPurchase(@ModelAttribute("purchaseDTO") PurchaseDTO purchaseDTO) {
        purchaseServiceImp.createPurchase(purchaseDTO);
        return "redirect:/warehouse/purchase";
    }

    @PostMapping("/update/{purchaseId}")
    public String updatePurchase(@RequestParam("origin") String origin, @RequestParam("status") String status,
                                 @PathVariable("purchaseId")int purchaseId){
        PurchaseDTO purchaseDTO=new PurchaseDTO();
        purchaseDTO.setOrigin(origin);
        purchaseDTO.setStatus(status);
        purchaseServiceImp.updatePurchase(purchaseDTO,purchaseId);
        return "redirect:/warehouse/purchase";
    }


    @GetMapping("/{productId}")
    public String getPurchaseDetail(@PathVariable("productId")int productId, Model model){
        List<ProductColor> productColorList=productServiceImp.findAllProductColor();
        PurchaseDTO purchaseDetailsDTOList=purchaseServiceImp.getPurchaseDetail(productId);
        model.addAttribute("purchases",purchaseDetailsDTOList);
        model.addAttribute("color",productColorList);
        model.addAttribute("size",purchaseDetailsDTOList.getPurchaseDetails().size());
        return "purchasedetails";
    }


}
