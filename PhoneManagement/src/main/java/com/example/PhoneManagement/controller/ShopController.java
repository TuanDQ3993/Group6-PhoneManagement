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

import java.math.BigDecimal;
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
                       @RequestParam(value = "minprice", required = false) String minprice,
                       @RequestParam(value = "maxprice", required = false) String maxprice,
                       @RequestParam(value = "sort", required = false) String sort,
                       Model model) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        PageableDTO pageableDTO = new PageableDTO(currentPage - 1, pageSize, sort);

        int effectiveCategoryId = (categoryId != null) ? categoryId : 0;
        String effectiveBrand = (brand != null) ? brand : "";
        String effectiveSearch = (search != null) ? search : "";
        String effectiveMinprice = (minprice != null) ? minprice : "";
        String effectiveMaxprice = maxprice != null ? maxprice : "";

        Page<ProductShop> productShops = productService.findPaginated(pageableDTO, effectiveCategoryId, effectiveBrand, effectiveSearch, effectiveMinprice, effectiveMaxprice);

        model.addAttribute("size1", pageSize);
        model.addAttribute("minprice", minprice);
        model.addAttribute("maxprice", maxprice);
        model.addAttribute("category", categoryService.getAllCategoryActive());
        model.addAttribute("brand", productService.getAllBrand());
        model.addAttribute("productShops", productShops);
        model.addAttribute("productName", search);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedSort", sort);

        int totalPages = productShops.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "store.html";
    }
}


