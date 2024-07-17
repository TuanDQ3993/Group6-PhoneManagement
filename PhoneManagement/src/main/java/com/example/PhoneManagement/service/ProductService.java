package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.ProductColorDTO;
import com.example.PhoneManagement.dto.request.ProductUpdateRequest;
import com.example.PhoneManagement.dto.request.ProductViewRequest;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.entity.ProductColor;
import com.example.PhoneManagement.entity.Products;
import com.example.PhoneManagement.repository.CategoryRepository;
import com.example.PhoneManagement.repository.ColorRepository;
import com.example.PhoneManagement.repository.ProductColorRepository;
import com.example.PhoneManagement.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ColorRepository colorRepository;
    CategoryRepository categoryRepository;
    ProductColorRepository productColorRepository;

    public List<ProductColorDTO> getAllProduct(){
        List<Products> products=productRepository.findAll();
        List<Category> categories=categoryRepository.findAll();
        List<ProductColor> productColorList=productColorRepository.findAll();
        List<ProductColorDTO> productColorDTOS=new ArrayList<>();

        for(Products product: products){
            ProductColorDTO dto=new ProductColorDTO();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setCategoryId(product.getCategory().getCategoryId());
            Category category = categories.stream()
                    .filter(cat -> cat.getCategoryId() == product.getCategory().getCategoryId())
                    .findFirst()
                    .orElse(null);
            ProductColor productColor=productColorList.stream()
                            .filter(i -> i.getProducts().getProductId()==product.getProductId())
                            .findFirst().orElse(null);
            dto.setImage(productColor.getImage());
            dto.setPrice(product.getPrice());
            dto.setQuantity(product.getQuantity());
            dto.setWarrantyPeriod(product.getWarrantyPeriod());
            dto.setCreatedAt(product.getCreatedAt());

            productColorDTOS.add(dto);
        }
        return productColorDTOS;
    }

    public ProductViewRequest getProduct(int productId){
        ProductViewRequest productViewRequest=new ProductViewRequest();
        Products products=productRepository.findById(productId).orElseThrow(() -> new RuntimeException("product not exist"));
        Category category=categoryRepository.findById(products.getCategory().getCategoryId()).orElseThrow(()-> new RuntimeException(""));
        List<ProductColor> productColors=productColorRepository.findAll().stream().filter(pro -> pro.getProducts().getProductId()==products.getProductId()).toList();
        List<String> image=new ArrayList<>();
        List<String> color=new ArrayList<>();

        productViewRequest.setProductId(products.getProductId());
        productViewRequest.setProductName(products.getProductName());
        productViewRequest.setCategory(category.getCategoryName());
        for(ProductColor productColor: productColors){
            image.add(productColor.getImage());
            color.add(productColor.getColors().getColorName());
        }
        productViewRequest.setColor(color);
        productViewRequest.setImage(image);
        productViewRequest.setPrice(products.getPrice());
        productViewRequest.setDescription(products.getDescription());
        productViewRequest.setQuantity(products.getQuantity());
        productViewRequest.setWarrantyPeriod(products.getWarrantyPeriod());
        productViewRequest.setCreatedAt(products.getCreatedAt());

        return productViewRequest;
    }

    public void updateProduct(int productId, ProductUpdateRequest request){
        Products products=productRepository.findById(productId).orElseThrow(()-> new RuntimeException("product not exist"));
        Category category=categoryRepository.findById(1).orElseThrow(() ->new RuntimeException("Category not exist"));
//        products.setProductName(request.getProductName());
        products.setCategory(category);
        products.setDescription(request.getDescription());
//        products.setPrice(request.getPrice());
        products.setWarrantyPeriod(request.getWarrantyPeriod());

        productRepository.save(products);
    }

}
