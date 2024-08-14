package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.Colors;
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

    ProductInfo getProductColorById(int proId);

    Page<ProductDTO> findPaginated(Pageable pageable, Integer categoryId, String name);

    List<ProductDTO> findAllProduct();

    public List<Products> getNewProducts();

    public List<Products> getNewProductsByCategory(int categoryId);

    public List<Products> getTopSellingProduct();

    public List<Products> getTopSellingProductsByCategory(int categoryId);

    public Products getProductById(int productId);

    public ProductInfo getProductInfoById(int productColorId);

    public List<Products> getRelatedProductByCategory(int categoryId);

    void isDeletedProduct(int proId);

    List<Colors> findColorByProductId(int productId);
    List<String> getAllBrand();
    List<ProductShop> getProductShops();
    Page<ProductShop> findPaginated(PageableDTO pageable, Integer categoryId, String brandName, String productName, String maxprice, String minprice);
    List<ProductShop> findProductShopByCategoryId(int categoryId,String maxprice, String minprice);
    List<ProductShop> findProductShopByBrand(String brandName , String maxprice, String minprice);
    List<ProductShop> findProductShopByCategoryIdAndBrand(int categoryId, String brandName);
    List<ProductShop> findProductShopByProductName(String productName);
    List<ProductShop> findProductsByCategoryIdAndBrandAndPrice(int categoryId, String brandName, String minPrice, String maxPrice);
    List<ProductShop> findProductShopByPrice(String minprice, String maxprice);

    void updateQuantityProduct(int prodId, int quantity);
}