package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Products;
import com.example.PhoneManagement.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceImpTest {

    @InjectMocks
    private ProductServiceImp productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ColorRepository colorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductColorRepository productColorRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    public void testUpdateProduct_Success() {
        int productId = 1;
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setProductName("Updated Product");
        request.setBrandName("Updated Brand");
        request.setDescription("Updated Description");
        request.setWarrantyPeriod(24);
        request.setCategory(1);

        Products product = new Products();
        product.setProductId(productId);
        product.setProductName("Old Product");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(new Category()));

        productService.updateProduct(productId, request);

        assertEquals("Updated Product", product.getProductName());
        verify(productRepository, times(1)).save(product);
    }



    @Test
    public void testDeleteProductColor_Success() {
        int proId = 1;
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductcolorId(proId);

        when(productColorRepository.findById(proId)).thenReturn(Optional.of(productInfo));

        productService.deleteProductColor(proId);

        verify(productColorRepository, times(1)).delete(productInfo);
    }


    @Test
    public void testUpdateQuantityProduct_Success() {
        int prodId = 1;
        int quantity = 50;

        Products product = new Products();
        product.setProductId(prodId);
        product.setQuantity(10);

        when(productRepository.findById(prodId)).thenReturn(Optional.of(product));

        productService.updateQuantityProduct(prodId, quantity);

        assertEquals(quantity, product.getQuantity());
        verify(productRepository, times(1)).save(product);
    }


}
