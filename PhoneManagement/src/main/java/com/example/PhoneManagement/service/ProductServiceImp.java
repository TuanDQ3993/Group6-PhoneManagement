package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Products;
import com.example.PhoneManagement.repository.CategoryRepository;
import com.example.PhoneManagement.repository.ColorRepository;
import com.example.PhoneManagement.repository.ProductColorRepository;
import com.example.PhoneManagement.repository.ProductRepository;
import com.example.PhoneManagement.service.imp.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Override
    public void saveProduct(ProductDTO productDTO) {
        int total=0;
        Products product = new Products();
        boolean result=productRepository.findAll().stream().anyMatch(p ->p.getProductName().equals(productDTO.getProductName()));
        if(result) return;
        if(productDTO.getWarrantyPeriod()<0) return ;
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());

        product.setBrandName(productDTO.getBrandName());
        product.setWarrantyPeriod(productDTO.getWarrantyPeriod());
        product.setCreatedAt(new Date());

        Category category = new Category();
        category.setCategoryId(productDTO.getCategoryId());
        product.setCategory(category);

        if (product.getProductInfoList() == null) {
            product.setProductInfoList(new ArrayList<>());
        }

        for (ProductColorDTO colorDTO : productDTO.getColors()) {
            Path uploadDir = Paths.get("uploads/");

            if (!Files.exists(uploadDir)) {
                try {
                    Files.createDirectories(uploadDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String fileImage = null;
            if (colorDTO.getImage() != null && !colorDTO.getImage().isEmpty()) {
                try {
                    String fileName = StringUtils.cleanPath(colorDTO.getImage().getOriginalFilename());
                    Path filePath = uploadDir.resolve(fileName);

                    try (InputStream inputStream = colorDTO.getImage().getInputStream()) {
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    fileImage = "/uploads/" + fileName.toLowerCase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(colorDTO.getQuantity()<0) return;

            ProductInfo productInfo = new ProductInfo();
            Colors color = new Colors();
            color.setColorId(colorDTO.getColorId());
            productInfo.setColors(color);
            BigDecimal gia = null;
            try {
                gia = new BigDecimal(colorDTO.getPrice());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            productInfo.setPrice(gia);
            productInfo.setImage(fileImage);
            productInfo.setQuantity(colorDTO.getQuantity());
            total+=colorDTO.getQuantity();
            productInfo.setProducts(product);

            product.getProductInfoList().add(productInfo);
        }
        product.setQuantity(total);
        productRepository.save(product);
    }

    @Override
    public ProductViewRequest getProduct(int productId) {
        try {
            ProductViewRequest productViewRequest = new ProductViewRequest();
            Products products = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("product not exist"));
            Category category = categoryRepository.findById(products.getCategory().getCategoryId()).orElseThrow(() -> new RuntimeException("category not exist"));
            List<ProductInfo> productInfos = productColorRepository.findAll().stream()
                    .filter(pro -> pro.getProducts().getProductId() == products.getProductId())
                    .toList();

            List<String> imagePaths = new ArrayList<>();
            List<Integer> colorId = new ArrayList<>();
            List<String> colorName = new ArrayList<>();
            List<Integer> quantity = new ArrayList<>();
            List<Integer> proColorId = new ArrayList<>();
            List<BigDecimal> price=new ArrayList<>();
            List<Boolean> isDeleted=new ArrayList<>();

            productViewRequest.setProductId(products.getProductId());
            productViewRequest.setProductName(products.getProductName());
            productViewRequest.setCategory(category.getCategoryId());
            productViewRequest.setBrandName(products.getBrandName());
            for (ProductInfo productInfo : productInfos) {
                proColorId.add(productInfo.getProductcolorId());
                imagePaths.add(productInfo.getImage());
                colorId.add(productInfo.getColors().getColorId());
                colorName.add(productInfo.getColors().getColorName());
                quantity.add(productInfo.getQuantity());
                price.add(productInfo.getPrice());
                isDeleted.add(productInfo.isDeleted());
            }

            productViewRequest.setProColorId(proColorId);
            productViewRequest.setColorId(colorId);
            productViewRequest.setColorName(colorName);
            productViewRequest.setImage(imagePaths);
            productViewRequest.setDescription(products.getDescription());
            productViewRequest.setQuantity(quantity);
            productViewRequest.setPrice(price);
            productViewRequest.setIsDeleted(isDeleted);
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
            products.setDescription(request.getDescription());
            products.setWarrantyPeriod(request.getWarrantyPeriod());
            products.setBrandName(request.getBrandName());
            productRepository.save(products);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateProductColor(int proColorId, ProductColorUpdate request) {
        ProductInfo productInfo = productColorRepository.findById(proColorId)
                .orElseThrow(() -> new RuntimeException("ProductColor not exist"));

//        Products product = productRepository.findById(request.getProductId())
//                .orElseThrow(() -> new RuntimeException("Product not exist"));

        Colors color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new RuntimeException("Color not exist"));

        ProductViewRequest productViewRequest = getProduct(request.getProductId());
        boolean colorExists = productViewRequest.getColorId().stream().anyMatch(id -> id == color.getColorId() && id!=productInfo.getColors().getColorId());
        if (request.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (colorExists) {
            throw new IllegalArgumentException("Color already exists for the product");
        }
//        productColor.setProducts(product);
        productInfo.setColors(color);
        productInfo.setImage(request.getImage());
        productInfo.setPrice(request.getPrice());
        productInfo.setQuantity(request.getQuantity());
        productInfo.setLastUpdated(new Date());

        productColorRepository.save(productInfo);
    }

    @Override
    public void addProductColor(ProductColorCreateRequest request, int productId){
        try{
            ProductInfo productInfo = new ProductInfo();
            Products products = productRepository.findById(request.getProId()).orElseThrow(() -> new RuntimeException("product not exist"));
            Colors colors = colorRepository.findById(request.getColorId()).orElseThrow(() -> new RuntimeException("Color not exist"));
            ProductViewRequest productViewRequest=getProduct(productId);
            boolean colorExists = productViewRequest.getColorId().stream()
                    .anyMatch(color -> color== colors.getColorId());
            if(request.getQuantity()<0) return;
            if(!colorExists) return;
            productInfo.setProducts(products);
            productInfo.setColors(colors);
            productInfo.setImage(request.getImage());
            productInfo.setPrice(request.getPrice());
            productInfo.setQuantity(request.getQuantity());
            productInfo.setLastUpdated(new Date());

            productColorRepository.save(productInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProductColor(int proId){
        ProductInfo productInfo =productColorRepository.findById(proId).orElseThrow(()-> new RuntimeException(("Product not exist")));
        productColorRepository.delete(productInfo);
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
    public Page<ProductDTO> findPaginated(Pageable pageable, Integer categoryId, String name) {
        try {
            Page<Products> productPage;

            // Modify the query based on the name and categoryId parameters
            if (categoryId != null && name != null) {
                productPage = productRepository.findByCategoryCategoryIdAndProductNameContainingIgnoreCase(categoryId, name, pageable);
            } else if (categoryId != null) {
                productPage = productRepository.findByCategoryCategoryId(categoryId, pageable);
            } else if (name != null) {
                productPage = productRepository.findByProductNameContainingIgnoreCase(name, pageable);
            } else {
                productPage = productRepository.findAll(pageable);
            }

            List<Category> categories = categoryRepository.findAll();
            List<ProductInfo> productInfoList = productColorRepository.findAll();
            List<ProductDTO> productDTOS = new ArrayList<>();

            for (Products product : productPage.getContent()) {
                ProductDTO dto = new ProductDTO();
                dto.setProductId(product.getProductId());
                dto.setProductName(product.getProductName());
                dto.setCategoryId(product.getCategory().getCategoryId());
                dto.setBrandName(product.getBrandName());
                dto.setQuantity(product.getQuantity());
                dto.setWarrantyPeriod(product.getWarrantyPeriod());
                dto.setCreateAt(product.getCreatedAt());
                dto.setDescription(product.getDescription());
                Category category = categories.stream()
                        .filter(cat -> cat.getCategoryId() == product.getCategory().getCategoryId())
                        .findFirst()
                        .orElse(null);
                ProductInfo productInfo = productInfoList.stream()
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
    public ProductInfo getProductColorById(int proId){
        return productColorRepository.findById(proId).orElseThrow(()-> new RuntimeException("Product not exist"));
    }

    @Override
    public List<ProductDTO> findAllProduct(){
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductDTO(product.getProductId(), product.getProductName()))
                .collect(Collectors.toList());
    }

    @Override
    public void isDeletedProduct(int proId) {
        ProductInfo productInfo=productColorRepository.findById(proId).orElseThrow(()-> new RuntimeException(""));
        if(productInfo.isDeleted()){
            productInfo.setDeleted(false);
        }
        else productInfo.setDeleted(true);
        productColorRepository.save(productInfo);
    }
}