package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.entity.Category;
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

//        //get user
//        if (principal != null) {
//            String userName = principal.getName();
//            Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
//            userDTO.ifPresent(user -> model.addAttribute("user", user));
//        }
//
//        Cart cart = (Cart) session.getAttribute("cart");
//        if (cart != null) {
//            model.addAttribute("cart", cart);
//            model.addAttribute("size", cart.getItems().size());
//            model.addAttribute("total", cart.getTotalPrice());
//        } else {
//            model.addAttribute("size", 0);
//            model.addAttribute("total", 0.0);
//        }


        model.addAttribute("productTS", productTS);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedCategoryIdTS", categoryIdTS);

        return "homepage";
    }
}