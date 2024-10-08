package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Products;
import com.example.PhoneManagement.repository.CategoryRepository;
import com.example.PhoneManagement.service.UserServiceImp;
import com.example.PhoneManagement.service.imp.CategoryService;
import com.example.PhoneManagement.service.imp.ProductService;
import com.example.PhoneManagement.service.imp.UserService;
import com.example.PhoneManagement.util.Cart;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/home")
@Controller
public class HomepageController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @GetMapping("/homepage")
    public String homepage(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                           @RequestParam(value = "categoryIdTS", required = false) Integer categoryIdTS,
                           Model model, Principal principal, HttpSession session) {
        List<Category> categories = categoryService.getAllCategoryActive();
        List<Products> products;
        List<Products> productTS;

        if (categoryId != null) {
            products = productService.getNewProductsByCategory(categoryId);
        } else {
            products = productService.getNewProducts();
        }

        if (categoryIdTS != null) {
            productTS = productService.getTopSellingProductsByCategory(categoryIdTS);
        } else {
            productTS = productService.getTopSellingProduct();
        }

        for (Products product : products) {
            product.setProductInfoList(
                    product.getProductInfoList().stream()
                            .filter(ProductInfo::isDeleted)
                            .collect(Collectors.toList())
            );
        }

        model.addAttribute("productTS", productTS);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedCategoryIdTS", categoryIdTS);

        return "homepage";
    }
}