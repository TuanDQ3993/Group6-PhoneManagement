package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.OrderDetailDTO;
import com.example.PhoneManagement.dto.request.OrderDetailInfoDTO;
import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.dto.request.PageableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
List<OrderInfoDTO> getListOrder();
    OrderInfoDTO mapToOrderInfoDTO(Object[] result);
    List<OrderDetailDTO> getOrderDetails(int orderId);
    OrderDetailInfoDTO getOrderInfo(int orderId);
    Page<OrderInfoDTO> findPaginated(PageableDTO pageable);
}
