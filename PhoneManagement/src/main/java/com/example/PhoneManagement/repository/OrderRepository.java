package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Integer> {
    @Query(value = "WITH OrderedOrders AS (" +
            "SELECT o.order_id AS OrderID, p.product_name AS ProductName, pc.image AS Image, " +
            "o.total_amount AS TotalAmount, o.order_date AS OrderDate, u.fullname AS Username, " +
            "ROW_NUMBER() OVER (PARTITION BY o.order_id ORDER BY od.order_detail_id) AS rn, " +
            "(SELECT COUNT(*) FROM orderdetail WHERE order_id = o.order_id) - 1 AS countP "+
            "FROM orders o " +
            "JOIN useraccount u ON o.user_id = u.user_id " +
            "JOIN orderdetail od ON o.order_id = od.order_id " +
            "JOIN productcolor pc ON od.product_color_id = pc.product_color_id " +
            "JOIN product p ON pc.product_id = p.product_id) " +
            "SELECT OrderID, ProductName, Image, TotalAmount, OrderDate, Username, countP " +
            "FROM OrderedOrders " +
            "WHERE rn = 1",
            nativeQuery = true)
    List<Object[]> findOrderedOrders();

    @Query(value = "SELECT o.total_amount, o.order_date, ua.address, ua.fullname, ua.phone_number " +
            "FROM orders o " +
            "JOIN useraccount ua ON ua.user_id = o.user_id " +
            "WHERE o.order_id = :orderId", nativeQuery = true)
    List<Object[]> findOrderInfo(@Param("orderId") Integer orderId);

}
