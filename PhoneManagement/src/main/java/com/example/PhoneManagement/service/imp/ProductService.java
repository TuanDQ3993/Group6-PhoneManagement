package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Products;
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

    Page<ProductDTO> findPaginated(Pageable pageable, Integer categoryId);

    List<ProductDTO> findAllProduct();

    public List<Products> getNewProducts();

    public List<Products> getNewProductsByCategory(int categoryId);

    public List<Products> getTopSellingProduct();

    public List<Products> getTopSellingProductsByCategory(int categoryId);
}