package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.entity.ProductColor;
import com.example.PhoneManagement.entity.Products;
import com.example.PhoneManagement.repository.*;
import com.example.PhoneManagement.service.imp.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImp implements ProductService {
    ProductRepository productRepository;
    ColorRepository colorRepository;
    CategoryRepository categoryRepository;
    ProductColorRepository productColorRepository;
    FileStorageServiceImpl fileStorageService;
    OrderDetailRepository orderDetailRepository;

    @Override
    public void saveProduct(ProductDTO productDTO) {
        Products product = new Products();
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setPrice(productDTO.getPrice());
        product.setWarrantyPeriod(productDTO.getWarrantyPeriod());
        product.setCreatedAt(new Date());

        Category category = new Category();
        category.setCategoryId(productDTO.getCategoryId());
        product.setCategory(category);

        if (product.getProductColorList() == null) {
            product.setProductColorList(new ArrayList<>());
        }

        for (ProductColorDTO colorDTO : productDTO.getColors()) {
            ProductColor productColor = new ProductColor();
            Colors color = new Colors();
            color.setColorId(colorDTO.getColorId());
            productColor.setColors(color);
            productColor.setQuantity(colorDTO.getQuantity());
            String image=uploadFile(colorDTO.getImage());
            productColor.setImage(image);
            productColor.setProducts(product);

            product.getProductColorList().add(productColor);
        }

        productRepository.save(product);
    }

    @Override
    public ProductViewRequest getProduct(int productId) {
        try {
            ProductViewRequest productViewRequest = new ProductViewRequest();
            Products products = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("product not exist"));
            Category category = categoryRepository.findById(products.getCategory().getCategoryId()).orElseThrow(() -> new RuntimeException("category not exist"));
            List<ProductColor> productColors = productColorRepository.findAll().stream()
                    .filter(pro -> pro.getProducts().getProductId() == products.getProductId())
                    .toList();

            List<String> imagePaths = new ArrayList<>();
            List<Integer> colorId = new ArrayList<>();
            List<String> colorName = new ArrayList<>();
            List<Integer> quantity = new ArrayList<>();
            List<Integer> proColorId = new ArrayList<>();

            productViewRequest.setProductId(products.getProductId());
            productViewRequest.setProductName(products.getProductName());
            productViewRequest.setCategory(category.getCategoryId());

            for (ProductColor productColor : productColors) {
                proColorId.add(productColor.getProductcolorId());
                imagePaths.add(productColor.getImage());
                colorId.add(productColor.getColors().getColorId());
                colorName.add(productColor.getColors().getColorName());
                quantity.add(productColor.getQuantity());
            }

            productViewRequest.setProColorId(proColorId);
            productViewRequest.setColorId(colorId);
            productViewRequest.setColorName(colorName);
            productViewRequest.setImage(imagePaths);
            productViewRequest.setPrice(products.getPrice());
            productViewRequest.setDescription(products.getDescription());
            productViewRequest.setQuantity(quantity);
            productViewRequest.setWarrantyPeriod(products.getWarrantyPeriod());
            productViewRequest.setCreatedAt(products.getCreatedAt());

            return productViewRequest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void updateProduct(int productId, ProductUpdateRequest request) {
        try {
            Products products = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("product not exist"));
            Category category = categoryRepository.findById(request.getCategory()).orElseThrow(() -> new RuntimeException("Category not exist"));
            products.setProductName(request.getProductName());
            products.setCategory(category);
//        products.setDescription(request.getDescription());
            products.setQuantity(request.getQuantity());
            products.setPrice(request.getPrice());
            products.setWarrantyPeriod(request.getWarrantyPeriod());
            productRepository.save(products);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateProductColor(int proId, ProductColorUpdate request, int productId) {
        ProductColor productColor = productColorRepository.findById(proId).orElseThrow(() -> new RuntimeException("Product not exist"));
        Products products = productRepository.findById(request.getProductId()).orElseThrow(() -> new RuntimeException("product not exist"));
        Colors colors = colorRepository.findById(request.getColorId()).orElseThrow(() -> new RuntimeException("Color not exist"));
        ProductViewRequest productViewRequest=getProduct(productId);
        int c=productViewRequest.getColorId().stream().filter(color -> color==colors.getColorId()).findFirst().orElse(-1);
        if(request.getQuantity()<0) return;
        if(c!=-1) return;
        productColor.setProducts(products);
        productColor.setColors(colors);
        productColor.setImage(request.getImage());
        productColor.setQuantity(request.getQuantity());
        productColor.setLastUpdated(new Date());

        productColorRepository.save(productColor);
    }

    @Override
    public void addProduct(ProductCreateRequest request){
        try{
            Products products=new Products();
            Category category = categoryRepository.findById(request.getCategory()).orElseThrow(() -> new RuntimeException("Category not exist"));
            List<Products> pro=productRepository.findAll();
            for(Products product:pro){
                if(product.getProductName().equals(request.getProductName()))
                    return;
            }
            products.setProductName(request.getProductName());
            products.setCategory(category);
            products.setDescription(request.getDescription());
            products.setPrice(request.getPrice());
            products.setWarrantyPeriod(request.getWarrantyPeriod());
            products.setCreatedAt(new Date());
            products.setQuantity(request.getQuantity());

            productRepository.save(products);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void addProductColor(ProductColorCreateRequest request, int productId){
        try{
            ProductColor productColor = new ProductColor();
            Products products = productRepository.findById(request.getProId()).orElseThrow(() -> new RuntimeException("product not exist"));
            Colors colors = colorRepository.findById(request.getColorId()).orElseThrow(() -> new RuntimeException("Color not exist"));
            ProductViewRequest productViewRequest=getProduct(productId);
            int c=productViewRequest.getColorId().stream().filter(color -> color==colors.getColorId()).findFirst().orElse(-1);
            if(request.getQuantity()<0) return;
            if(c!=-1) return;
            productColor.setProducts(products);
            productColor.setColors(colors);
            productColor.setImage(request.getImage());
            productColor.setQuantity(request.getQuantity());
            productColor.setLastUpdated(new Date());

            productColorRepository.save(productColor);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProductColor(int proId){
        ProductColor productColor=productColorRepository.findById(proId).orElseThrow(()-> new RuntimeException(("Product not exist")));
        productColorRepository.delete(productColor);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = fileStorageService.storeFile(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();
            return fileDownloadUri;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<ProductDTO> findPaginated(Pageable pageable, Integer categoryId) {
        try {
            Page<Products> productPage;
            if (categoryId != null) {
                productPage = productRepository.findByCategoryCategoryId(categoryId, pageable);
            } else {
                productPage = productRepository.findAll(pageable);
            }

            List<Category> categories = categoryRepository.findAll();
            List<ProductColor> productColorList = productColorRepository.findAll();
            List<ProductDTO> productDTOS = new ArrayList<>();

            for (Products product : productPage.getContent()) {
                ProductDTO dto = new ProductDTO();
                dto.setProductId(product.getProductId());
                dto.setProductName(product.getProductName());
                dto.setCategoryId(product.getCategory().getCategoryId());
                dto.setPrice(product.getPrice());
                dto.setQuantity(product.getQuantity());
                dto.setWarrantyPeriod(product.getWarrantyPeriod());
                dto.setCreateAt(product.getCreatedAt());

                Category category = categories.stream()
                        .filter(cat -> cat.getCategoryId() == product.getCategory().getCategoryId())
                        .findFirst()
                        .orElse(null);
                ProductColor productColor = productColorList.stream()
                        .filter(i -> i.getProducts().getProductId() == product.getProductId())
                        .findFirst()
                        .orElse(null);

                productDTOS.add(dto);
            }
            return new PageImpl<>(productDTOS, pageable, productPage.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Page.empty();
    }

    @Override
    public List<ProductColor> findAllProductColor(){
        return productColorRepository.findAll();
    }

    @Override
    public List<ProductDTO> findAllProduct(){
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductDTO(product.getProductId(), product.getProductName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Products> getNewProducts() {
        Pageable pageable = PageRequest.of(0, 8); // Top 8 sản phẩm mới nhất
        return productRepository.findTop8ByOrderByCreatedAtDesc(pageable).getContent();
    }

    @Override
    public List<Products> getNewProductsByCategory(int categoryId) {
        Pageable pageable = PageRequest.of(0, 8); // Top 8 sản phẩm mới nhất theo danh mục
        return productRepository.findTop8ByCategoryIdOrderByCreatedAtDesc(categoryId, pageable).getContent();
    }

    @Override
    public List<Products> getTopSellingProduct() {
        Pageable pageable = PageRequest.of(0, 8);
        return orderDetailRepository.findTopSelling(pageable).getContent();
    }

    @Override
    public List<Products> getTopSellingProductsByCategory(int categoryId) {
        Pageable pageable = PageRequest.of(0, 8);
        return orderDetailRepository.findTopSellingByCategory(categoryId, pageable).getContent();
    }

}