package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.dto.request.ProductShop;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.service.imp.CategoryService;
import com.example.PhoneManagement.service.imp.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/home")
public class ShopController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    private ProductService productService;


    @GetMapping("/shop")
    public String shop(@RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size,
                       @RequestParam(value = "categoryId", required = false) Integer categoryId,
                       @RequestParam(value = "brand", required = false) String brand,
                       @RequestParam(value = "productName", required = false) String search,
                       Model model) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        PageableDTO pageableDTO = new PageableDTO(currentPage - 1, pageSize);


        int effectiveCategoryId = (categoryId != null) ? categoryId : 0;
        String effectiveBrand = (brand != null) ? brand : "";
        String effectiveSearch = (search != null) ? search : "";

        Page<ProductShop> productShops = productService.findPaginated(pageableDTO, effectiveCategoryId, effectiveBrand, effectiveSearch);

        model.addAttribute("size1", pageSize);
        model.addAttribute("category", categoryService.findAllCategory());
        model.addAttribute("brand", productService.getAllBrand());
        model.addAttribute("productShops", productShops);
        model.addAttribute("productName", search);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedBrand", brand);

        int totalPages = productShops.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "store";
    }
    @GetMapping("/search")
    public String searchProducts(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                 @RequestParam(value = "brand", required = false) String brand,
                                 Model model) {
        List<ProductShop> products;

        if (categoryId != null && categoryId != 0 && brand != null && !brand.isEmpty()) {
            products = productService.findProductShopByCategoryIdAndBrand(categoryId, brand);
        } else if (categoryId != null && categoryId != 0) {
            products = productService.findProductShopByCategoryId(categoryId);
        } else if (brand != null && !brand.isEmpty()) {
            products = productService.findProductShopByBrand(brand);
        } else {
            products = productService.getProductShops();
        }

        List<Category> categories = categoryService.findAllCategory();

        model.addAttribute("product", products);
        model.addAttribute("brand", productService.getAllBrand());
        model.addAttribute("category", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedBrand", brand);

        return "store";
    }




}
