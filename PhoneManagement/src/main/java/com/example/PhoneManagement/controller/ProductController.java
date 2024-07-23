package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.service.CategoryServiceImp;
import com.example.PhoneManagement.service.ColorServiceImp;
import com.example.PhoneManagement.service.ProductServiceImp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductController {
    ProductServiceImp productService;
    CategoryServiceImp categoryService;
    ColorServiceImp colorService;
    @GetMapping
    public String getAllProduct(Model model,@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductColorDTO> productColorPage = productService.findPaginated(pageable);
        model.addAttribute("proList", productService.getAllProduct());
        model.addAttribute("category", categoryService.findAllCategory());
        model.addAttribute("productColorPage", productColorPage);
        model.addAttribute("pageSize", size);
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
        return "redirect:/admin/products";
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
        return "redirect:/admin/products";
    }

    @GetMapping("/{productId}")
    public String getProduct(@PathVariable("productId") int productId, Model model){
        ProductViewRequest products=productService.getProduct(productId);
        model.addAttribute("product",products);
        model.addAttribute("category",categoryService.findAllCategory());
        model.addAttribute("colors",colorService.getAllColor());
        model.addAttribute("size",products.getImage().size()) ;
        return "e_commerce";
    }
    @PostMapping("/{productId}/update")
    public String updateProductColor(@PathVariable("productId") int productId,
                                     @RequestParam("proColorId") int proId,
                                     @RequestParam("colors") int colors,
                                     @RequestParam("image") MultipartFile image,
                                     @RequestParam("quantity") int quantity) {
        String fileImage=productService.uploadFile(image);
        ProductColorUpdate request = new ProductColorUpdate();
        request.setProductId(productId);
        request.setColorId(colors);
        request.setImage(fileImage);
        request.setQuantity(quantity);
        productService.updateProductColor(proId, request,productId);
        return "redirect:/admin/products/{productId}";
    }

    @PostMapping("/{productId}/add")
    public String addProductColor(@PathVariable("productId") int proId,
                                  @RequestParam("color") int color,
                                  @RequestParam("image") MultipartFile image,
                                  @RequestParam("quantity") int quantity){
        String fileImage=productService.uploadFile(image);
        ProductColorCreateRequest request=new ProductColorCreateRequest();
        request.setProId(proId);
        request.setImage(fileImage);
        request.setQuantity(quantity);
        request.setColorId(color);

        productService.addProductColor(request,proId);
        return "redirect:/admin/products/{productId}";
    }

    @PostMapping("/{productId}/delete")
    public String deleteProductColor(@RequestParam("proColorId")int proId, @PathVariable("productId") int productId){
            productService.deleteProductColor(proId);
            return "redirect:/admin/products/"+productId;
    }

}
