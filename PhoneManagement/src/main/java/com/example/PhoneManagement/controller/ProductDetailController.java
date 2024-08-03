package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Products;
import com.example.PhoneManagement.service.imp.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/home")
@Controller
public class ProductDetailController {

    @Autowired
    private ProductService productService;

    @GetMapping("/detail/{productId}/{productColorId}")
    public String getProductDetail(@PathVariable int productId, @PathVariable int productColorId, Model model) {
        Products products = productService.getProductById(productId);
        ProductInfo selectedProductInfo = productService.getProductInfoById(productColorId);

        List<Products> relatedProducts = productService.getRelatedProductByCategory(products.getCategory().getCategoryId());

        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("product", products);
        model.addAttribute("selectedProductInfo", selectedProductInfo);
        model.addAttribute("productInfo", products.getProductInfoList());
        return "product";
    }

}
