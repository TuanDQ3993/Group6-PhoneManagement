package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query(value = "SELECT od.order_id, p.product_id, p.product_name, pc.image, od.quantity, od.price " +
            "FROM orderdetail od " +
            "JOIN productcolor pc ON pc.product_color_id = od.product_color_id " +
            "JOIN products p ON p.product_id = pc.product_id " +
            "WHERE od.order_id = :orderId", nativeQuery = true)
    List<Object[]> findAllOrderDetails(@Param("orderId") Integer orderId);

}
