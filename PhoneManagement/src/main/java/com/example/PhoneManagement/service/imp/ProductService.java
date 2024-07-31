package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ProductService {
    public void saveProduct(ProductDTO productDTO);

    ProductViewRequest getProduct(int productId);

    void updateProduct(int productId, ProductUpdateRequest request);

    void updateProductColor(int proId, ProductColorUpdate request);

    void addProductColor(ProductColorCreateRequest request, int productId);

    void deleteProductColor(int proId);

    String uploadFile(MultipartFile file);

    List<ProductInfo> findAllProductColor();

    Page<ProductDTO> findPaginated(Pageable pageable, Integer categoryId, String name);

    List<ProductDTO> findAllProduct();
}