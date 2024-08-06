package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Products;
import com.example.PhoneManagement.repository.ProductColorRepository;
import com.example.PhoneManagement.service.imp.ProductService;
import com.example.PhoneManagement.service.imp.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequestMapping("/home")
@Controller
public class ProductDetailController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProductColorRepository productColorRepository;

    @GetMapping("/detail/{productId}/{productColorId}")
    public String getProductDetail(@PathVariable int productId, @PathVariable int productColorId, Model model, Principal principal) {
        Products products = productService.getProductById(productId);
        ProductInfo selectedProductInfo = productService.getProductInfoById(productColorId);

        List<Products> relatedProducts = productService.getRelatedProductByCategory(products.getCategory().getCategoryId());
//        if (principal != null) {
//            String userName = principal.getName();
//            Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
//            userDTO.ifPresent(user -> model.addAttribute("user", user));
//        }

        if(selectedProductInfo != null) {
            int stock = productColorRepository.getQuantityProduct(productColorId);
            selectedProductInfo.setQuantity(stock);
            model.addAttribute("stock", stock);
        }
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("product", products);
        model.addAttribute("selectedProductInfo", selectedProductInfo);
        model.addAttribute("productInfo", products.getProductInfoList());
        return "product";
    }

}
