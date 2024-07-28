package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseDetailsRepository extends JpaRepository<PurchaseDetail,Integer> {
    @Query("SELECT DATE(p.purchaseDate), SUM(pd.quantity) FROM purchasedetail pd JOIN pd.purchase p WHERE p.user.userId = :userId GROUP BY DATE(p.purchaseDate)")
    List<Object[]> findDailyProductsForUser(@Param("userId") int userId);

    @Query("select p3.productName ,count(p3.productName) from purchasedetail p1 join productcolor p2 on p1.productColor.productcolorId=p2.productcolorId" +
            " join Products p3 on p2.products.productId=p3.productId" +
            " join Purchase p4 on p4.purchaseId=p1.purchase.purchaseId where p4.user.userId= :userId group by p3.productName")
    List<Object[]> findDailyPurchaseProduct(@Param("userId") int userId);
}
