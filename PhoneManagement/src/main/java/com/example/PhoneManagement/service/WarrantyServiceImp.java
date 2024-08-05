package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.entity.WarrantyRepair;
import com.example.PhoneManagement.repository.OrderRepository;
import com.example.PhoneManagement.repository.WarrantyRepairRepository;
import com.example.PhoneManagement.service.imp.WarrantyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WarrantyServiceImp implements WarrantyService {
    @Autowired
    private WarrantyRepairRepository warrantyRepairRepository;
    @Autowired
    private OrderServiceImp orderServiceImp;

    // In ra tất cả đơn bảo hành
    @Override
    public Page<WarrantyRepair> findAll(PageDTO pageDTO) {
        return warrantyRepairRepository.findAllBy(PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    // Lấy đơn bảo hành theo từng technical
    @Override
    public Page<WarrantyRepair> findPaginated(PageDTO pageDTO, int id) {
        return warrantyRepairRepository.findPaginated(id, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    // Lưu thông tin đơn
    @Override
    @Transactional
    public void save(WarrantyRepair wr) {
        warrantyRepairRepository.save(wr);
    }

    // Tìm kiê theo tên sản phẩm hoặc tên khách hàng
    @Override
    public Page<WarrantyRepair> findByProductNameAndUserName(PageDTO pageDTO, String query, int technicalId) {
        return warrantyRepairRepository.findWarrantyRepairByProductNameAndAndUser(query, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()), technicalId);
    }

    // Tìm kiếm theo ngày
    @Override
    public Page<WarrantyRepair> findByRepairDate(PageDTO pageDTO, Date date, int technicalId) {
        return warrantyRepairRepository.findAllByRepairDate(date, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()), technicalId);
    }

    // TÌm kiến theo trang thái
    @Override
    public Page<WarrantyRepair> findByStatus(PageDTO pageDTO, String status, int technicalId) {
        return warrantyRepairRepository.findAllByStatus(status, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()), technicalId);
    }
// In ra theo yeu cau (technical)
    @Override
    public Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByTecnical(PageDTO pageDTO, String status, int technicalId, Date date,String query) {
        return warrantyRepairRepository.findAllByProductNameOrUserAndStatusAndRepairDateOrderByTechnical(query,date,status, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()), technicalId);
    }
    // Xoa don
    @Override
    @Transactional
    public void deleteWarrantyRepair(int id) {
        Optional<WarrantyRepair> optionalWarrantyRepair = warrantyRepairRepository.findById(id);
        if (optionalWarrantyRepair.isPresent()) {
            WarrantyRepair warrantyRepair = optionalWarrantyRepair.get();
            warrantyRepair.setDeleted(true);
            warrantyRepairRepository.save(warrantyRepair);
        }
    }

    // Tổng đơn bảo hành
    @Override
    public long sumWarrantyRepairs(int technicalId) {
        return warrantyRepairRepository.sumWarrantyRepairs(technicalId);
    }

    // Tổng đơn bảo hành theo ngày
    @Override
    public long sumWarrantyRepairsByRepairDate(int technicalId, Date date) {
        return warrantyRepairRepository.sumWarrantyRepairsByRepairDate(technicalId, date);
    }

    // Tổng đơn bảo hành thành công theo ngày(status = completed)
    @Override
    public long countWarrantyRepairsByRepairDateAndStatus(int technicalId, Date date) {
        return warrantyRepairRepository.countWarrantyRepairsByRepairDateAndStatus(technicalId, date);
    }

    // Tổng đơn bảo hành thành công
    @Override
    public long countAllWarrantyRepairsAndStatus(int technicalId) {
        return warrantyRepairRepository.countAllWarrantyRepairsAndStatus(technicalId);
    }

    @Override
    public Page<WarrantyRepair> findAllByProductNameByAdmin(PageDTO pageDTO, String query) {
        return  warrantyRepairRepository.findAllByProductNameOrUser(query,PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<WarrantyRepair> findByRepairDateByAdmin(PageDTO pageDTO, Date date) {
        return warrantyRepairRepository.findAllByRepairDate(date,PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<WarrantyRepair> findByStatusByAdmin(PageDTO pageDTO, String status) {
        return warrantyRepairRepository.findAllByStatus(status,PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }
// In ra theo yeu cau(admin)
    @Override
    public Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByAdmin(PageDTO pageDTO, String status, Date date,String query) {
        return warrantyRepairRepository.findAllByProductNameOrUserAndStatusAndRepairDate(query,date,status, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    // Kiểm tra ngày tồn tại
    @Override
    public boolean isExistRepairDate(Date date) {
        return warrantyRepairRepository.existsWarrantyRepairsByRepairDate(date);
    }

    // In ra excel theo ngày theo technical
    @Override
    public List<WarrantyRepair> getWarrantyByIdAndRepairDate(int technicalId, Date date) {
        return warrantyRepairRepository.findAllByTechnicalUserIdAndRepairDate(technicalId, date);
    }
    // In ra excel theo ngày admin
    @Override
    public List<WarrantyRepair> getWarrantyByIdAndRepairDateByAdmin(Date date) {
        return warrantyRepairRepository.findAllByTechnicalUserIdAndRepairDate(date);
    }

    public WarrantyRepair getById(int id) {
        Optional<WarrantyRepair> optionalWarrantyRepair = warrantyRepairRepository.findById(id);
        return optionalWarrantyRepair.orElse(null);
    }


    // Cập nhật status đơn
    public void changeStatus(int id, String status) {
        Optional<WarrantyRepair> optionalRepair = warrantyRepairRepository.findById(id);

        if (optionalRepair.isPresent()) {
            WarrantyRepair repair = optionalRepair.get();
            if (!repair.getStatus().equals(status)) {
                repair.setStatus(status);


                Orders order = repair.getOrder();
                if (order != null) {
                    int orderId = repair.getOrder().getOrderId();
                    switch (status) {
                        case "Completed":
                            orderServiceImp.changeStatusOrder(orderId, "Warranty Completed");
                            break;
                        case "Rejected":
                            orderServiceImp.changeStatusOrder(orderId, "Warranty Rejected");
                            break;
                        case "Pending":
                            orderServiceImp.changeStatusOrder(orderId, "Warranty Pending");
                            break;
                        case "In Process":
                            orderServiceImp.changeStatusOrder(orderId, "Warranty In Process");
                            break;
                        default:
                            orderServiceImp.changeStatusOrder(orderId, "Error");
                            break;
                    }
                }

                warrantyRepairRepository.save(repair);
            } else {
                throw new IllegalArgumentException("The status is already set to " + status);
            }
        } else {
            throw new NoSuchElementException("WarrantyRepair with id " + id + " not found");
        }
    }

// Chap nhan don
    public void acceptWarranty(int id) {
        Optional<WarrantyRepair> optionalRepair = warrantyRepairRepository.findById(id);

        if (optionalRepair.isPresent()) {
            WarrantyRepair repair = optionalRepair.get();
            if (repair.getStatus().equals("Pending")) {
                repair.setStatus("In Process");

                // Cập nhật trạng thái của Orders tương ứng
                Orders order = repair.getOrder(); // Lấy order từ repair
                if (order != null) {
                    int orderId = order.getOrderId();
                    orderServiceImp.changeStatusOrder(orderId, "Warranty In Process");
                }
                warrantyRepairRepository.save(repair);
            } else {
                throw new IllegalArgumentException("Only pending warranties can be accepted.");
            }
        } else {
            throw new NoSuchElementException("WarrantyRepair with id " + id + " not found");
        }
    }


    public Map<String, Long> getWarrantyCountByDate(int id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Object[]> result = warrantyRepairRepository.findCountByRepairDate(id);

        return result.stream()
                .collect(Collectors.toMap(
                        row -> dateFormat.format((Date) row[0]),
                        row -> (Long) row[1]
                ));
    }


}
