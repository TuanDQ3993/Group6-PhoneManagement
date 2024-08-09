package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    @Override
    long count();

    @Query(value = "select n from Products n")
    List<Products> getListProduct();

    Page<Products> findByCategoryCategoryId(Integer categoryId, Pageable pageable);

    Page<Products> findByProductNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Products> findByCategoryCategoryIdAndProductNameContainingIgnoreCase(Integer categoryId, String name, Pageable pageable);

    @Query("SELECT p FROM Products p JOIN p.productInfoList pi WHERE pi.isDeleted = true AND p.category.deleted = true ORDER BY p.createdAt DESC")
    Page<Products> findTop8ByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Products p JOIN p.productInfoList pi WHERE pi.isDeleted = true AND p.category.categoryId = :categoryId ORDER BY p.createdAt DESC")
    Page<Products> findTop8ByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") int categoryId, Pageable pageable);


    @Query(value = "select distinct brand_name from products", nativeQuery = true)
    List<String> getAllBrand();

    @Query(value = "SELECT p.product_id, p.product_name,\n" +
            " MAX(pi.price) AS price,\n" +
            " MAX(pi.image) AS image,\n" +
            " p.category_id,\n" +
            "p.description,\n" +
            "MAX(pi.product_color_id) AS color_id,\n" +
            "MAX(p.quantity) AS quantity\n" +
            "FROM products p\n" +
            "JOIN productinfo pi ON p.product_id = pi.product_id \n" +
            "WHERE  pi.isdeleted = 1 GROUP BY p.product_id, p.product_name, p.category_id, p.description\n" +
            "LIMIT 0, 1000;", nativeQuery = true)
    List<Object[]> getAllProductShop();

    @Query(value = "SELECT p.product_id, p.product_name, \n" +
            "       MAX(pi.price) AS price, \n" +
            "       MAX(pi.image) AS image, \n" +
            "       p.category_id, \n" +
            "       p.description, \n" +
            "       MAX(pi.product_color_id) AS color_id, \n" +
            "       MAX(p.quantity) AS quantity\n" +
            "FROM products p\n" +
            "JOIN productinfo pi ON p.product_id = pi.product_id \n" +
            "where p.category_id = :categoryId and price between :minprice and :maxprice and pi.isdeleted = 1 GROUP BY p.product_id, p.product_name, p.category_id, p.description\n" +
            "LIMIT 0, 1000;\n", nativeQuery = true)
    List<Object[]> findProductsByCategoryId(@Param("categoryId") int categoryId,@Param("minprice")String minprice, @Param("maxprice")String maxprice);

    @Query(value = "SELECT p.product_id, p.product_name, \n" +
            "       MAX(pi.price) AS price, \n" +
            "       MAX(pi.image) AS image, \n" +
            "       p.category_id, \n" +
            "       p.description, \n" +
            "       MAX(pi.product_color_id) AS color_id, \n" +
            "       MAX(p.quantity) AS quantity\n" +
            "FROM products p\n" +
            "JOIN productinfo pi ON p.product_id = pi.product_id \n" +
            "where brand_name =:brandName and price between :minprice and :maxprice and pi.isdeleted = 1 GROUP BY p.product_id, p.product_name, p.category_id, p.description\n" +
            "LIMIT 0, 1000;\n", nativeQuery = true)
    List<Object[]> findProductsByBrandName(@Param("brandName") String brandName,@Param("minprice")String minprice, @Param("maxprice")String maxprice);

    @Query(value = "SELECT p.product_id, p.product_name, \n" +
            "       MAX(pi.price) AS price, \n" +
            "       MAX(pi.image) AS image, \n" +
            "       p.category_id, \n" +
            "       p.description, \n" +
            "       MAX(pi.product_color_id) AS color_id, \n" +
            "       MAX(p.quantity) AS quantity\n" +
            "FROM products p\n" +
            "JOIN productinfo pi ON p.product_id = pi.product_id " +
            "WHERE p.category_id = :categoryId " +
            "AND p.brand_name = :brandName and  pi.isdeleted = 1 GROUP BY p.product_id, p.product_name, p.category_id, p.description\n" +
            "LIMIT 0, 1000;\n", nativeQuery = true)
    List<Object[]> findProductsByCategoryIdAndBrand(@Param("categoryId") int categoryId, @Param("brandName") String brandName);

    @Query(value = "SELECT p.product_id, p.product_name, \n" +
            "       MAX(pi.price) AS price, \n" +
            "       MAX(pi.image) AS image, \n" +
            "       p.category_id, \n" +
            "       p.description, \n" +
            "       MAX(pi.product_color_id) AS color_id, \n" +
            "       MAX(p.quantity) AS quantity\n" +
            "FROM products p\n" +
            "JOIN productinfo pi ON p.product_id = pi.product_id  " +
            "WHERE product_name LIKE %:search%  and pi.isdeleted = 1 GROUP BY p.product_id, p.product_name, p.category_id, p.description\n" +
            "LIMIT 0, 1000;\n",nativeQuery = true)
    List<Object[]> findProductShopByProductName(@Param("search") String search);

    @Query(value = "SELECT p.product_id, p.product_name,\n" +
            " MAX(pi.price) AS price,\n" +
            " MAX(pi.image) AS image,\n" +
            " p.category_id,\n" +
            "p.description,\n" +
            " MAX(pi.product_color_id) AS color_id,\n" +
            "MAX(pi.quantity) AS quantity\n" +
            "FROM products p\n" +
            "JOIN productinfo pi ON p.product_id = pi.product_id \n" +
            "WHERE p.category_id = :categoryId\n" +
            "AND p.brand_name = :brandName and price between :minprice and :maxprice and pi.isdeleted = 1 GROUP BY p.product_id, p.product_name, p.category_id, p.description\n" +
            "LIMIT 0, 1000;", nativeQuery = true)
    List<Object[]> findProductsByCategoryIdAndBrandAndPrice(@Param("categoryId") int categoryId, @Param("brandName") String brandName, @Param("minprice")String minprice, @Param("maxprice")String maxprice);

    @Query(value = "SELECT p.product_id, p.product_name,\n" +
            " MAX(pi.price) AS price,\n" +
            " MAX(pi.image) AS image,\n" +
            " p.category_id,\n" +
            "p.description,\n" +
            " MAX(pi.product_color_id) AS color_id,\n" +
            "MAX(p.quantity) AS quantity\n" +
            "FROM products p\n" +
            "JOIN productinfo pi ON p.product_id = pi.product_id \n" +
            "WHERE price between :minprice and :maxprice and pi.isdeleted = 1 GROUP BY p.product_id, p.product_name, p.category_id, p.description\n" +
            "LIMIT 0, 1000;", nativeQuery = true)
    List<Object[]> findProductsByPrice(@Param("minprice")String minprice, @Param("maxprice")String maxprice);

}