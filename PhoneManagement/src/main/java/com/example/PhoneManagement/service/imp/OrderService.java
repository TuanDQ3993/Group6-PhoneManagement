package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.dto.response.ProductTopSeller;
import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderService {
    List<OrderInfoDTO> getListOrder();

    List<OrderInfoDTO> getListOrderBySaleId(int id);

    OrderInfoDTO mapToOrderInfoDTO(Object[] result);

    List<OrderDetailDTO> getOrderDetails(int orderId);

    Orders getOrderInfo(int orderId);

    LocalDate convertToLocalDate(Date date);

    Page<OrderInfoDTO> findPaginated(PageableDTO pageable, LocalDate startDate, LocalDate endDate, String status, String find, UserDTO user);

    List<OrderInfoDTO> getListOrderFilter(LocalDate startDate, LocalDate endDate, String status, String find, UserDTO user);

    long countOrdersInCurrentMonth();

    long countSalesInCurrentMonth();

    Double getTotalAmountForCurrentMonth();

    Double getTotalAmountForEachMonth(int month);

    long countCustomerInCurrentMonth();

    List<ProductTopSeller> getProductTopSellers();

    void changeStatusOrder(int orderId, String status);

    void changeSale(int orderId,int saleId);

    void backProduct(int id);

    List<Users> getAllSale();

    public Page<Object[]> getOrdersByUserIdWithFilters(UserDTO user, String status, String search, int page, int size);

    public int countTotalOrders(UserDTO user);

    public List<Object[]> findOrderDetail(int orderId);

}