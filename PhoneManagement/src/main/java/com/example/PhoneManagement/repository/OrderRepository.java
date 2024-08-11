package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {
    @Query(value = "WITH OrderedOrders AS (" +
            "SELECT o.order_id AS OrderID, p.product_name AS ProductName, pc.image AS Image, " +
            "o.total_amount AS TotalAmount, o.order_date AS OrderDate, u.fullname AS Username, " +
            "ROW_NUMBER() OVER (PARTITION BY o.order_id ORDER BY od.order_detail_id) AS rn, " +
            "(SELECT COUNT(*) FROM orderdetail WHERE order_id = o.order_id) - 1 AS countP, " +
            "o.status AS Status," +
            "o.saler_id AS Saler " +
            "FROM orders o " +
            "JOIN useraccount u ON o.user_id = u.user_id " +
            "JOIN orderdetail od ON o.order_id = od.order_id " +
            "JOIN productinfo pc ON od.product_color_id = pc.product_color_id " +
            "JOIN products p ON pc.product_id = p.product_id) " +
            "SELECT OrderID, ProductName, Image, TotalAmount, OrderDate, Username, countP,Status,Saler " +
            "FROM OrderedOrders " +
            "WHERE rn = 1 " +
            "ORDER BY OrderDate DESC",
            nativeQuery = true)
    List<Object[]> findOrderedOrders();

    long countByOrderDateBetween(Date startDate, Date endDate);

    long countByOrderDateBetweenAndStatus(Date startDate, Date endDate, String status);

    @Query(value = "SELECT SUM(total_amount) FROM orders WHERE MONTH(order_date) = :month AND status = 'Completed'", nativeQuery = true)
    Double sumTotalAmountByMonthAndStatus(@Param("month") int month);

    @Query(value = "SELECT COUNT(DISTINCT user_id) FROM orders WHERE MONTH(order_date) = :month", nativeQuery = true)
    long countDistinctUserId(@Param("month") int month);

    @Query(value = "SELECT " +
            "    saler_id " +
            "FROM (" +
            "    SELECT " +
            "    s.user_id AS saler_id, " +
            "    COUNT(o.order_id) AS order_count " +
            "    FROM useraccount s " +
            "    LEFT JOIN orders o ON s.user_id = o.saler_id " +
            "    AND DATE(o.order_date) = CURDATE() " +
            "    WHERE s.role_id = 2 and s.active= 1" +
            "    GROUP BY s.user_id " +
            ") AS order_counts " +
            "ORDER BY order_count ASC " +
            "LIMIT 1",
            nativeQuery = true)
    int getSaleMinOrder();

    @Query(value = "SELECT o1.orderId, p2.productName, c1.colorName, p1.image, p1.price, o1.totalAmount, o2.quantity, o1.orderDate, c2.categoryName, o1.status, p2.warrantyPeriod " +
            "FROM Orders o1 " +
            "JOIN orderdetail o2 ON o1.orderId = o2.order.orderId " +
            "JOIN productinfo p1 ON o2.productInfo.productcolorId = p1.productcolorId " +
            "JOIN Products p2 ON p1.products.productId = p2.productId " +
            "JOIN Color c1 ON p1.colors.colorId = c1.colorId " +
            "JOIN Category c2 ON c2.categoryId = p2.category.categoryId " +
            "WHERE o1.user.userId = :userId " +
            "AND (:status = 'all' OR o1.status = :status) " +
            "AND (:searchTerm IS NULL OR LOWER(p2.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY o1.orderDate DESC")
    List<Object[]> findOrdersWithFiltersCategory(@Param("userId") int userId,
                                                 @Param("status") String status,
                                                 @Param("searchTerm") String searchTerm);

    @Query(value = "SELECT o1.orderId, p2.productName, c1.colorName, p1.image, p1.price, o1.totalAmount, o2.quantity, o1.orderDate, c2.categoryName," +
            "o1.receiver, o1.note, o1.address, o1.phoneNumber, o1.payment " +
            "FROM Orders o1 " +
            "JOIN orderdetail o2 ON o1.orderId = o2.order.orderId " +
            "JOIN productinfo p1 ON o2.productInfo.productcolorId = p1.productcolorId " +
            "JOIN Products p2 ON p1.products.productId = p2.productId " +
            "JOIN Color c1 ON p1.colors.colorId = c1.colorId " +
            "JOIN Category c2 ON c2.categoryId = p2.category.categoryId " +
            "WHERE o1.orderId=:orderId ")
    List<Object[]> findOrderDetail(@Param("orderId") int orderId);


    @Query("SELECT COUNT(o) FROM Orders o WHERE o.user.userId = :userId")
    int countOrdersByUserId(@Param("userId") int userId);
}
