package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    @Override
    long count();

    @Query(value = "select n from Products n")
    List<Products> getListProduct();

    Page<Products> findByCategoryCategoryId(int categoryId, Pageable pageable);

    @Query("SELECT p FROM Products p ORDER BY p.createdAt DESC")
    Page<Products> findTop8ByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Products p WHERE p.category.categoryId = :categoryId ORDER BY p.createdAt DESC")
    Page<Products> findTop8ByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") int categoryId, Pageable pageable);
    @Query(value = "select distinct brand_name from products", nativeQuery = true)
    List<String> getAllBrand();

    @Query(value = "SELECT p.product_id, p.product_name, pi.price, pi.image, p.category_id, p.description, pi.color_id, pi.quantity FROM products p\n" +
            "join productinfo pi on p.product_id = pi.product_id;", nativeQuery = true)
    List<Object[]> getAllProductShop();

    @Query(value = "SELECT p.product_id, p.product_name, pi.price, pi.image, p.category_id, p.description, pi.color_id, pi.quantity FROM products p\n" +
            "join productinfo pi on p.product_id = pi.product_id\n" +
            "where p.category_id = :categoryId", nativeQuery = true)
    List<Object[]> findProductsByCategoryId(@Param("categoryId") int categoryId);

    @Query(value = "SELECT p.product_id, p.product_name, pi.price, pi.image, p.category_id, p.description, pi.color_id, pi.quantity FROM products p\n" +
            "join productinfo pi on p.product_id = pi.product_id\n" +
            "where brand_name =:brandName", nativeQuery = true)
    List<Object[]> findProductsByBrandName(@Param("brandName") String brandName);

    @Query(value = "SELECT p.product_id, p.product_name, pi.price, pi.image, p.category_id, p.description, pi.color_id, pi.quantity FROM products p " +
            "JOIN productinfo pi ON p.product_id = pi.product_id " +
            "WHERE p.category_id = :categoryId " +
            "AND p.brand_name = :brandName", nativeQuery = true)
    List<Object[]> findProductsByCategoryIdAndBrand(@Param("categoryId") int categoryId, @Param("brandName") String brandName);

    @Query(value = "SELECT p.product_id, p.product_name, pi.price, pi.image, p.category_id, p.description, pi.color_id, pi.quantity FROM products p " +
            "JOIN productinfo pi ON p.product_id = pi.product_id " +
            "WHERE product_name LIKE %:search% ",nativeQuery = true)
    List<Object[]> findProductShopByProductName(@Param("search") String search);

}