
package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.service.CategoryServiceImp;
import com.example.PhoneManagement.service.ColorServiceImp;
import com.example.PhoneManagement.service.FileStorageServiceImpl;
import com.example.PhoneManagement.service.ProductServiceImp;
import com.example.PhoneManagement.service.imp.CategoryService;
import com.example.PhoneManagement.service.imp.ColorService;
import com.example.PhoneManagement.service.imp.FileStorageService;
import com.example.PhoneManagement.service.imp.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductController {
    ProductService productService;
    CategoryService categoryService;
    ColorService colorService;
    FileStorageService fileStorageService;
    @GetMapping
    public String getAllProduct(Model model,
                                @RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "5") int size,
                                @RequestParam(name = "sortField", defaultValue = "productId") String sortField,
                                @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
                                @RequestParam(name = "categoryId", required = false) Integer categoryId,
                                @RequestParam(name = "search", required = false) String name) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> productColorPage = productService.findPaginated(pageable, categoryId,name);

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("productColorPage", productColorPage);
        model.addAttribute("category", categoryService.findAllCategory());
        model.addAttribute("colors", colorService.getAllColor());
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("search",name);
        return "listproduct";
    }

//    @PostMapping("/search")
//    public String searchProduct(@RequestParam("search") String search, Model model){
//        model.addAttribute("proList", productService.getAllProduct().stream().filter(pro -> pro.getProductName().contains(search)).toList());
//        model.addAttribute("category", categoryService.findAllCategory());
//        return "listproduct";
//    }

    @PostMapping("/update")
    public String updateProduct(@RequestParam("productId") int proId,
                                @RequestParam("productName") String productName,
                                @RequestParam("cateId") int cateId,
                                @RequestParam("quantity") int quantity,
                                @RequestParam("description") String description,
                                @RequestParam("warrantyPeriod") int warrantyPeriod
    ) {
        ProductUpdateRequest request=new ProductUpdateRequest();
        request.setProductName(productName);
        request.setCategory(cateId);
        request.setQuantity(quantity);
        request.setDescription(description);
        request.setWarrantyPeriod(warrantyPeriod);
        productService.updateProduct(proId, request);
        return "redirect:/admin/products";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute("productDTO") ProductDTO productDTO) {
        productService.saveProduct(productDTO);
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
                                     @RequestParam("proColorId") int proColorId,
                                     @RequestParam("colors") int colorId,
                                     @RequestParam("image") MultipartFile image,
                                     @RequestParam("price") String price,
                                     @RequestParam("quantity") int quantity,
                                     Model model, RedirectAttributes redirectAttributes) {
        Path uploadDir = Paths.get("uploads/");

        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ProductInfo existingProductColor = productService.getProductColorById(proColorId);
        if (existingProductColor == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product color not found.");
            return "redirect:/admin/products/" + productId;
        }

        String fileImage = existingProductColor.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                Path filePath = uploadDir.resolve(fileName);

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                fileImage = fileName.toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ProductColorUpdate request = new ProductColorUpdate();
        request.setProductId(productId);
        request.setColorId(colorId);
        request.setImage(fileImage);
        BigDecimal gia = null;
        try {
            gia = new BigDecimal(price);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid price format.");
            return "redirect:/admin/products/" + productId;
        }

        request.setPrice(gia);
        request.setQuantity(quantity);

        try {
            productService.updateProductColor(proColorId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Product color updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }
        return "redirect:/admin/products/" + productId;
    }


    @PostMapping("/{productId}/add")
    public String addProductColor(@PathVariable("productId") int proId,
                                  @RequestParam("color") int color,
                                  @RequestParam("image") MultipartFile image,
                                  @RequestParam("price") String price,
                                  @RequestParam("quantity") int quantity){
        Path uploadDir = Paths.get("uploads/");

        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String fileImage = null;
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                Path filePath = uploadDir.resolve(fileName);

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                fileImage =   fileName.toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ProductColorCreateRequest request=new ProductColorCreateRequest();
        request.setProId(proId);
        request.setImage(fileImage);
        request.setQuantity(quantity);
        BigDecimal gia = null;
        try {
            gia = new BigDecimal(price);
        } catch (NumberFormatException e) {
            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("errorMessage", "Invalid price format.");
            return "redirect:/admin/products/{productId}";
        }
        request.setPrice(gia);
        request.setColorId(color);

        productService.addProductColor(request,proId);
        return "redirect:/admin/products/{productId}";
    }

    @PostMapping("/{productId}/delete")
    public String deleteProductColor(@RequestParam("proColorId")int proId, @PathVariable("productId") int productId){
        productService.isDeletedProduct(proId);
        return "redirect:/admin/products/"+productId;
    }

}
