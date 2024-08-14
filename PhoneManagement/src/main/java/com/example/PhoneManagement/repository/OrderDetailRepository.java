package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.OrderDetail;
import com.example.PhoneManagement.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query(value = "SELECT od.order_id, p.product_id, p.product_name, pc.image, od.quantity, od.price, c.color_name " +
            "FROM orderdetail od " +
            "JOIN productinfo pc ON pc.product_color_id = od.product_color_id " +
            "JOIN products p ON p.product_id = pc.product_id " +
            "JOIN color c on c.color_id = pc.color_id "+
            "WHERE od.order_id = :orderId", nativeQuery = true)
    List<Object[]> findAllOrderDetails(@Param("orderId") Integer orderId);

    @Query(value = "SELECT p.product_id,p.product_name,pc.image,SUM(od.quantity) AS total_quantity_sold " +
            "FROM orderdetail od " +
            "JOIN orders o ON od.order_id = o.order_id " +
            "JOIN productinfo pc ON od.product_color_id = pc.product_color_id " +
            "JOIN products p on p.product_id =pc.product_id " +
            "WHERE MONTH(o.order_date) = MONTH(CURRENT_DATE()) and o.status='Completed' " +
            "AND YEAR(o.order_date) = YEAR(CURRENT_DATE()) " +
            "GROUP BY p.product_id, p.product_name,pc.image " +
            "ORDER BY total_quantity_sold DESC " +
            "LIMIT 5;", nativeQuery = true)
    List<Object[]> findTop5Sellers();

    @Query("SELECT p FROM Products p JOIN p.productInfoList pi JOIN orderdetail od ON p.productId = od.productInfo.products.productId WHERE pi.isDeleted = true AND p.category.deleted = true GROUP BY p.productId, p.productName ORDER BY SUM(od.quantity) DESC")
    Page<Products> findTopSelling(Pageable pageable);

    @Query("SELECT p FROM Products p JOIN p.productInfoList pi JOIN orderdetail od ON p.productId = od.productInfo.products.productId WHERE pi.isDeleted = true AND p.category.categoryId = :categoryId GROUP BY p.productId, p.productName ORDER BY SUM(od.quantity) DESC")
    Page<Products> findTopSellingByCategory(@Param("categoryId") int categoryId, Pageable pageable);
}