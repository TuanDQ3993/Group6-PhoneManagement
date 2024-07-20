package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.service.CategoryService;
import com.example.PhoneManagement.service.ColorService;
import com.example.PhoneManagement.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductController {
    ProductService productService;
    CategoryService categoryService;
    ColorService colorService;
    @GetMapping
    public String getAllProduct(Model model) {
        model.addAttribute("proList", productService.getAllProduct());
        model.addAttribute("category", categoryService.findAllCategory());
        return "listproduct";
    }

    @PostMapping("/search")
    public String searchProduct(@RequestParam("search") String search, Model model){
        model.addAttribute("proList", productService.getAllProduct().stream().filter(pro -> pro.getProductName().contains(search)).toList());
        model.addAttribute("category", categoryService.findAllCategory());
        return "listproduct";
    }

    @PostMapping("/update")
    public String updateProduct(@RequestParam("productId") int proId,
                @RequestParam("productName") String productName,
                @RequestParam("cateId") int cateId,
                @RequestParam("quantity") int quantity,
                @RequestParam("price") BigDecimal price,
                @RequestParam("warrantyPeriod") int warrantyPeriod
    ) {
        ProductUpdateRequest request=new ProductUpdateRequest();
        request.setProductName(productName);
        request.setCategory(cateId);
        request.setQuantity(quantity);
        request.setPrice(price);
        request.setWarrantyPeriod(warrantyPeriod);
        productService.updateProduct(proId, request);
        return "redirect:/products";
    }

    @PostMapping("/add")
    public String addProduct(@RequestParam("productName") String proName,
                             @RequestParam("description") String des,
                             @RequestParam("price") BigDecimal price,
                             @RequestParam("category") int category,
                             @RequestParam("warrantyPeriod") int warrantyPeriod,
                             @RequestParam("quantity") int quantity){
        ProductCreateRequest request=new ProductCreateRequest();
        request.setProductName(proName);
        request.setDescription(des);
        request.setCategory(category);
        request.setPrice(price);
        request.setWarrantyPeriod(warrantyPeriod);
        request.setQuantity(quantity);
        productService.addProduct(request);
        return "redirect:/products";
    }

    @GetMapping("/{productId}")
    public String getProduct(@PathVariable("productId") int productId, Model model){
        ProductViewRequest products=productService.getProduct(productId);
        model.addAttribute("product",products);
        model.addAttribute("category",categoryService.findAllCategory());
        model.addAttribute("colors",colorService.getAllColor());
        model.addAttribute("size", products.getImage().size());
        return "e_commerce";
    }

    @PostMapping("/{productId}/update")
    public String updateProductColor(@PathVariable("productId") int productId,
                                     @RequestParam("proColorId") int proId,
                                     @RequestParam("colors") int colors,
                                     @RequestParam("image") String image,
                                     @RequestParam("quantity") int quantity) {
        ProductColorUpdate request = new ProductColorUpdate();
        request.setProductId(productId);
        request.setColorId(colors);
        request.setImage(image);
        request.setQuantity(quantity);
        productService.updateProductColor(proId, request);
        return "redirect:/products/{productId}";
    }

    @PostMapping("/{productId}/add")
    public String addProductColor(@PathVariable("productId") int proId,
                                  @RequestParam("color") int color,
                                  @RequestParam("image") String image,
                                  @RequestParam("quantity") int quantity){
        ProductColorCreateRequest request=new ProductColorCreateRequest();
        request.setProId(proId);
        request.setImage(image);
        request.setQuantity(quantity);
        request.setColorId(color);

        productService.addProductColor(request);
        return "redirect:/products/{productId}";
    }

    @GetMapping("/{productId}/delete")
    public String deleteProductColor(@RequestParam("proColorId")int proId, @PathVariable("productId") int productId){
        productService.deleteProductColor(proId);
        return "redirect:/products/"+productId;
    }
}
