package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.ProductColorDTO;
import com.example.PhoneManagement.dto.request.ProductColorUpdate;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
            dto.setCateName(product.getCategory().getCategoryName());
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
        List<Integer> colorId=new ArrayList<>();
        List<String> colorName=new ArrayList<>();
        List<Integer> quantity=new ArrayList<>();
        productViewRequest.setProductId(products.getProductId());
        productViewRequest.setProductName(products.getProductName());
        productViewRequest.setCategory(category.getCategoryId());
        for(ProductColor productColor: productColors){
            image.add(productColor.getImage());
            colorId.add(productColor.getColors().getColorId());
            colorName.add(productColor.getColors().getColorName());
            quantity.add(productColor.getQuantity());
        }
        productViewRequest.setColorId(colorId);
        productViewRequest.setColorName(colorName);
        productViewRequest.setImage(image);
        productViewRequest.setPrice(products.getPrice());
        productViewRequest.setDescription(products.getDescription());
        productViewRequest.setQuantity(quantity);
        productViewRequest.setWarrantyPeriod(products.getWarrantyPeriod());
        productViewRequest.setCreatedAt(products.getCreatedAt());

        return productViewRequest;
    }

    public Products updateProduct(int productId, ProductUpdateRequest request){
        Products products=productRepository.findById(productId).orElseThrow(()-> new RuntimeException("product not exist"));
        Category category=categoryRepository.findById(request.getCategory()).orElseThrow(() ->new RuntimeException("Category not exist"));
        products.setProductName(request.getProductName());
        products.setCategory(category);
        products.setDescription(request.getDescription());
        products.setPrice(request.getPrice());
        products.setWarrantyPeriod(request.getWarrantyPeriod());

        return productRepository.save(products);
    }

    public ProductColor updateProductColor(int proId, ProductColorUpdate request){
        ProductColor productColor= productColorRepository.findById(proId).orElseThrow(() -> new RuntimeException("Product not exist"));
        Products products=productRepository.findById(request.getProductId()).orElseThrow(()-> new RuntimeException("product not exist"));
        Colors colors=colorRepository.findById(request.getColorId()).orElseThrow(() -> new RuntimeException("Color not exist"));
        productColor.setProducts(products);
        productColor.setColors(colors);
        productColor.setImage(request.getImage());
        productColor.setQuantity(request.getQuantity());
        productColor.setLastUpdated(new Date());

        return productColorRepository.save(productColor);
    }


}
