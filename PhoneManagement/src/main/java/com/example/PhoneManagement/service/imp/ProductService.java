package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.*;
import org.springframework.data.domain.Page;
import com.example.PhoneManagement.entity.Products;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ProductService {
     List<ProductColorDTO> getAllProduct();
     ProductViewRequest getProduct(int productId);
     void updateProduct(int productId, ProductUpdateRequest request);
     void updateProductColor(int proId, ProductColorUpdate request, int productId);
     void addProduct(ProductCreateRequest request);
     void addProductColor(ProductColorCreateRequest request, int productId);
     void deleteProductColor(int proId);
     String uploadFile(MultipartFile file);
     Page<ProductColorDTO> findPaginated(Pageable pageable);
}
