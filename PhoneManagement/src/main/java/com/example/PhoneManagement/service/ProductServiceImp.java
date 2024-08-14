package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.entity.ProductInfo;
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
import java.util.*;
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
        int total = 0;
        Products product = new Products();
        boolean result = productRepository.findAll().stream().anyMatch(p -> p.getProductName().equals(productDTO.getProductName()));
        if (result) throw new IllegalArgumentException("Product Name already existed");

        if (productDTO.getWarrantyPeriod() < 0) throw new IllegalArgumentException("WarrantyPeriod cannot be negative");
        if(productDTO.getProductName().trim().isEmpty()) throw new IllegalArgumentException("Product Name is not empty");
        if(productDTO.getBrandName().trim().isEmpty()) throw new IllegalArgumentException("Brand Name is not empty");
        if(productDTO.getDescription().trim().isEmpty()) throw new IllegalArgumentException("Description is not empty");
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
            String contentType = colorDTO.getImage().getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Could not create upload directory");
            }
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

                    fileImage = fileName.toLowerCase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (colorDTO.getQuantity() <= 0) throw new IllegalArgumentException("Quantity cannot be negative");

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
            if (gia.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Price cannot be negative");
            productInfo.setPrice(gia);
            productInfo.setImage(fileImage);
            productInfo.setQuantity(colorDTO.getQuantity());
            total += colorDTO.getQuantity();
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

            List<BigDecimal> price = new ArrayList<>();
            List<Boolean> isDeleted = new ArrayList<>();


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
            if (request.getWarrantyPeriod() <= 0)
                throw new IllegalArgumentException("WarrantyPeriod cannot be negative");
            boolean result = productRepository.findAll().stream().anyMatch(pro -> pro.getProductName().equals(request.getProductName()));
            if (result && !products.getProductName().equals(request.getProductName())) {
                throw new IllegalArgumentException("Product Name already exist");
            }
            if(request.getProductName().trim().isEmpty()) throw new IllegalArgumentException("Product Name is not empty");
            if(request.getBrandName().trim().isEmpty()) throw new IllegalArgumentException("Brand Name is not empty");
            if(request.getDescription().trim().isEmpty()) throw new IllegalArgumentException("Description is not empty");
            products.setProductName(request.getProductName());
            products.setCategory(category);
            products.setDescription(request.getDescription());
            products.setWarrantyPeriod(request.getWarrantyPeriod());
            products.setBrandName(request.getBrandName());
            products.setCreatedAt(new Date());
            productRepository.save(products);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception e) {
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
        boolean colorExists = productViewRequest.getColorId().stream().anyMatch(id -> id == color.getColorId() && id != productInfo.getColors().getColorId());
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
    public void addProductColor(ProductColorCreateRequest request, int productId) {
        try {
            ProductInfo productInfo = new ProductInfo();
            Products products = productRepository.findById(request.getProId()).orElseThrow(() -> new RuntimeException("product not exist"));
            Colors color = colorRepository.findById(request.getColorId()).orElseThrow(() -> new RuntimeException("Color not exist"));
            List<Colors> colors = findColorByProductId(productId);
            for (Colors c : colors) {
                if (c.getColorId() == request.getColorId())
                    throw new IllegalArgumentException("Color already exists for the product");
            }
            if (request.getQuantity() < 0) throw new IllegalArgumentException("Quantity cannot be negative");
            productInfo.setProducts(products);
            productInfo.setColors(color);
            productInfo.setImage(request.getImage());
            productInfo.setPrice(request.getPrice());
            productInfo.setQuantity(request.getQuantity());
            productInfo.setLastUpdated(new Date());

            productColorRepository.save(productInfo);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProductColor(int proId) {
        ProductInfo productInfo = productColorRepository.findById(proId).orElseThrow(() -> new RuntimeException(("Product not exist")));
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

    public ProductInfo getProductColorById(int proId) {
        return productColorRepository.findById(proId).orElseThrow(() -> new RuntimeException("Product not exist"));

    }

    @Override
    public void updateQuantityProduct(int prodId, int quantity) {
        Products products = productRepository.findById(prodId).orElseThrow(() -> new RuntimeException("product not exist"));
        products.setQuantity(quantity);
        productRepository.save(products);
    }


    @Override
    public List<ProductDTO> findAllProduct() {
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

    @Override
    public Products getProductById(int productId) {
        return productRepository.findById(productId).orElse(null);
    }

    @Override
    public ProductInfo getProductInfoById(int productColorId) {
        return productColorRepository.findByProductcolorIdAndIsDeletedTrue(productColorId).orElse(null);
    }

    @Override
    public List<Products> getRelatedProductByCategory(int categoryId) {
        Pageable pageable = PageRequest.of(0, 4);
        return productColorRepository.findTop4ByCategoryIdOrderByCreatedAtDesc(categoryId, pageable).getContent();
    }

    public void isDeletedProduct(int proId) {
        ProductInfo productInfo = productColorRepository.findById(proId).orElseThrow(() -> new RuntimeException(""));
        if (productInfo.isDeleted()) {
            productInfo.setDeleted(false);
        } else productInfo.setDeleted(true);
        productColorRepository.save(productInfo);
        ProductViewRequest productViewRequest = getProduct(productInfo.getProducts().getProductId());
        List<Integer> proInfoId = productViewRequest.getQuantity();
        List<Boolean> deleted = productViewRequest.getIsDeleted();
        int total = 0;
        if (proInfoId == null || deleted == null) {
            total = 0;
        }
        for (int i = 0; i < proInfoId.size(); i++) {
            if (deleted.get(i)) {
                total += proInfoId.get(i);
            }
        }
        updateQuantityProduct(productInfo.getProducts().getProductId(), total);

    }

    @Override
    public List<Colors> findColorByProductId(int productId) {
        List<Colors> color = new ArrayList<>();
        List<ProductInfo> productInfos = productColorRepository.findAll().stream().filter(p -> p.getProducts().getProductId() == productId).toList();
        for (ProductInfo pro : productInfos) {
            color.add(pro.getColors());
        }
        return color;
    }


    @Override
    public List<String> getAllBrand() {
        return productRepository.getAllBrand();
    }

    @Override
    public List<ProductShop> getProductShops() {
        List<Object[]> results = productRepository.getAllProductShop();
        List<ProductShop> shop = new ArrayList<>();
        for (Object[] result : results) {
            ProductShop productShop = new ProductShop();
            productShop.setId(((Number) result[0]).intValue());
            productShop.setProductName((String) result[1]);
            productShop.setPrice(BigDecimal.valueOf(((Number) result[2]).doubleValue()));
            productShop.setImage((String) result[3]);
            productShop.setCategory_id(((Number) result[4]).intValue());
            productShop.setDescription((String) result[5]);
            productShop.setColor_id(((Number) result[6]).intValue());
            productShop.setQuantity(((Number) result[7]).intValue());
            shop.add(productShop);
        }
        return shop;
    }

    @Override
    public Page<ProductShop> findPaginated(PageableDTO pageable, Integer categoryId, String brandName, String productName, String minprice, String maxprice) {
        try {
            int pageSize = pageable.getPageSize();
            int currentPage = pageable.getPageNumber();
            String sort = pageable.getSort();

            List<ProductShop> productShops;
            System.out.println("categoryId:" + categoryId);
            System.out.println("brandName:" + brandName);
            System.out.println("min: " + minprice);
            System.out.println("max: " + maxprice);
            System.out.println("sort: " + sort);

            if (categoryId > 0 && !brandName.isEmpty()) {
                productShops = findProductsByCategoryIdAndBrandAndPrice(categoryId, brandName, minprice, maxprice);
            } else if (categoryId > 0) {
                productShops = findProductShopByCategoryId(categoryId, minprice, maxprice);
            } else if (!brandName.isEmpty()) {
                productShops = findProductShopByBrand(brandName, minprice, maxprice);
            } else if (productName != null && !productName.isEmpty()) {
                productShops = findProductShopByProductName(productName);
            } else if (!maxprice.isEmpty() && !minprice.isEmpty()) {
                productShops = findProductShopByPrice(minprice, maxprice);
            } else {
                productShops = getProductShops();
            }

            if (sort != null && !sort.isEmpty()) {
                switch (sort) {
                    case "price_asc":
                        productShops.sort(Comparator.comparing(ProductShop::getPrice));
                        break;
                    case "price_desc":
                        productShops.sort(Comparator.comparing(ProductShop::getPrice).reversed());
                        break;

                }
            }

            int totalItems = productShops.size();
            int startItem = currentPage * pageSize;

            List<ProductShop> paginatedList;
            if (startItem >= totalItems) {
                paginatedList = Collections.emptyList();
            } else {
                int toIndex = Math.min(startItem + pageSize, totalItems);
                paginatedList = productShops.subList(startItem, toIndex);
            }

            return new PageImpl<>(paginatedList, PageRequest.of(currentPage, pageSize), totalItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<ProductShop> findProductShopByCategoryId(int categoryId, String minprice, String maxprice) {
        List<Object[]> results = productRepository.findProductsByCategoryId(categoryId, minprice, maxprice);
        return mapResultsToProductShops(results);
    }


    @Override
    public List<ProductShop> findProductShopByBrand(String brandName, String minprice, String maxprice) {
        List<Object[]> results = productRepository.findProductsByBrandName(brandName, minprice, maxprice);
        return mapResultsToProductShops(results);
    }

    @Override
    public List<ProductShop> findProductShopByCategoryIdAndBrand(int categoryId, String brandName) {
        List<Object[]> results = productRepository.findProductsByCategoryIdAndBrand(categoryId, brandName);
        return mapResultsToProductShops(results);
    }

    @Override
    public List<ProductShop> findProductShopByProductName(String productName) {
        List<Object[]> results = productRepository.findProductShopByProductName(productName.trim());
        return mapResultsToProductShops(results);
    }

    @Override
    public List<ProductShop> findProductsByCategoryIdAndBrandAndPrice(int categoryId, String brandName, String minprice, String maxprice) {
        List<Object[]> results = productRepository.findProductsByCategoryIdAndBrandAndPrice(categoryId, brandName, minprice, maxprice);
        return mapResultsToProductShops(results);

    }

    @Override
    public List<ProductShop> findProductShopByPrice(String minprice, String maxprice) {
        List<Object[]> results = productRepository.findProductsByPrice(minprice, maxprice);
        return mapResultsToProductShops(results);
    }

    private List<ProductShop> mapResultsToProductShops(List<Object[]> results) {
        List<ProductShop> shops = new ArrayList<>();
        for (Object[] result : results) {
            ProductShop productShop = new ProductShop();
            productShop.setId(((Number) result[0]).intValue());
            productShop.setProductName((String) result[1]);
            productShop.setPrice(BigDecimal.valueOf(((Number) result[2]).doubleValue()));
            productShop.setImage((String) result[3]);
            productShop.setCategory_id(((Number) result[4]).intValue());
            productShop.setDescription((String) result[5]);
            productShop.setColor_id(((Number) result[6]).intValue());
            productShop.setQuantity(((Number) result[7]).intValue());
            shops.add(productShop);
        }
        return shops;
    }

    public long countAllProduct() {
        return productColorRepository.countAllBy();
    }
}