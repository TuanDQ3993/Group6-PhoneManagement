package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.ProductColorDTO;
import com.example.PhoneManagement.dto.request.ProductUpdateRequest;
import com.example.PhoneManagement.dto.request.ProductViewRequest;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Products;
import com.example.PhoneManagement.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductController {
    ProductService productService;

    @GetMapping
    public String getAllProduct(Model model){
         model.addAttribute("proList",productService.getAllProduct());
         return "listproduct";
    }

    @GetMapping("/{productId}")
    public String getProduct(@PathVariable("productId") int productId, Model model){
        model.addAttribute("product",productService.getProduct(productId));
        return "e_commerce";
    }

//    @PutMapping("/{productId}")
//    public void UpdateProduct(@PathVariable("productId") int proId, ProductUpdateRequest request){
//        productService.updateProduct(proId,request);
//    }
}
