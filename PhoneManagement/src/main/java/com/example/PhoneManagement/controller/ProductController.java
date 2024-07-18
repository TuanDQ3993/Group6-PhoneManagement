package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.ProductColorDTO;
import com.example.PhoneManagement.dto.request.ProductColorUpdate;
import com.example.PhoneManagement.dto.request.ProductUpdateRequest;
import com.example.PhoneManagement.dto.request.ProductViewRequest;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Products;
import com.example.PhoneManagement.service.CategoryService;
import com.example.PhoneManagement.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductController {
    ProductService productService;
    CategoryService categoryService;
    @GetMapping
    public String getAllProduct(Model model){
        model.addAttribute("proList",productService.getAllProduct());
        model.addAttribute("category",categoryService.findAllCategory());
         return "listproduct";
    }

    @GetMapping("/{productId}")
    public String getProduct(@PathVariable("productId") int productId, Model model){
        ProductViewRequest products=productService.getProduct(productId);
        model.addAttribute("product",products);
        model.addAttribute("category",categoryService.findAllCategory());
        model.addAttribute("size", products.getImage().size());
        return "e_commerce";
    }
    @PostMapping
    public String UpdateProduct(@RequestParam("productId") int proId,@ModelAttribute("product") ProductUpdateRequest request, Model model){
        productService.updateProduct(proId,request);
        return "redirect:/product";
    }

    @PostMapping("/{productId}/")
    public String updateProductColor(@PathVariable("productId") int productId,
                                     @RequestParam("proId") int proId,
                                     @RequestParam("colors") int colors,
                                     @RequestParam("images") String images,
                                     @RequestParam("quantities") int quantities,
                                     RedirectAttributes redirectAttributes) {
        ProductColorUpdate request = new ProductColorUpdate();
        request.setColorId(colors);
        request.setImage(images);
        request.setQuantity(quantities);

        productService.updateProductColor(proId, request);
        redirectAttributes.addAttribute("proId", proId);

        return "redirect:/product/" + productId; // Adjust as per your URL structure
    }
}
