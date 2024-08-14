package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.dto.request.WarrantyDTO;
import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.WarrantyRepair;
import com.example.PhoneManagement.repository.WarrantyRepairRepository;
import com.example.PhoneManagement.service.imp.WarrantyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    public long countAllByAdmin() {
        return warrantyRepairRepository.countAllBy();
    }

    @Override
    public long countAllByStatusByAdmin() {
        return warrantyRepairRepository.countWarrantyRepairBy();
    }

    @Override
    public Page<WarrantyRepair> findAllByProductNameByAdmin(PageDTO pageDTO, String query) {
        return warrantyRepairRepository.findAllByProductNameOrUser(query, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<WarrantyRepair> findByRepairDateByAdmin(PageDTO pageDTO, Date date) {
        return warrantyRepairRepository.findAllByRepairDate(date, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<WarrantyRepair> findByStatusByAdmin(PageDTO pageDTO, String status) {
        return warrantyRepairRepository.findAllByStatus(status, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    // In ra theo yeu cau(admin)
    @Override
    public Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByAdmin(PageDTO pageDTO, String status, Date date, String query) {
        return warrantyRepairRepository.findAllByProductNameOrUserAndStatusAndRepairDate(query, date, status, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
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

    @Override
    // Cập nhật status đơn
    public void changeStatus(int id, String status) {
        Optional<WarrantyRepair> optionalRepair = warrantyRepairRepository.findById(id);

        if (optionalRepair.isPresent()) {
            WarrantyRepair repair = optionalRepair.get();
            if (!repair.getStatus().equals(status)) {
                repair.setStatus(status);


                warrantyRepairRepository.save(repair);
//            } else {
//                throw new IllegalArgumentException("The status is already set to " + status);
//            }
//        } else {
//            throw new NoSuchElementException("WarrantyRepair with id " + id + " not found");
//        }
            }
        }
    }

    // Chap nhan don
    @Override
    public void acceptWarranty(int id) {
        Optional<WarrantyRepair> optionalRepair = warrantyRepairRepository.findById(id);

        if (optionalRepair.isPresent()) {
            WarrantyRepair repair = optionalRepair.get();
            if (repair.getStatus().equals("Warranty Pending")) {
                repair.setStatus("In Process");

                // Cập nhật trạng thái của Orders tương ứng
                warrantyRepairRepository.save(repair);
            } else {
                throw new IllegalArgumentException("Only pending warranties can be accepted.");
            }
        } else {
            throw new NoSuchElementException("WarrantyRepair with id " + id + " not found");
        }
    }

    @Override
    // Huy don
    public void cancelWarranty(int id) {
        Optional<WarrantyRepair> optionalRepair = warrantyRepairRepository.findById(id);

        if (optionalRepair.isPresent()) {
            WarrantyRepair repair = optionalRepair.get();
            if (repair.getStatus().equals("Warranty Pending")) {
                repair.setStatus("Warranty Cancel");

                warrantyRepairRepository.save(repair);
            } else {
                throw new IllegalArgumentException("Only pending warranties can be accepted.");
            }
        } else {
            throw new NoSuchElementException("WarrantyRepair with id " + id + " not found");
        }
    }

    @Override
    public Map<String, Long> getWarrantyCountByDate(int id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Object[]> result = warrantyRepairRepository.findCountByRepairDate(id);

        return result.stream()
                .collect(Collectors.toMap(
                        row -> dateFormat.format((Date) row[0]),
                        row -> (Long) row[1]
                ));
    }

    @Override
    public WarrantyRepair createWarrantyRepair(WarrantyDTO warrantyDTO, Orders order, Users user, Users technical, String productName) {
        WarrantyRepair newWarrantyRepair = new WarrantyRepair();
        newWarrantyRepair.setIssueDescription(warrantyDTO.getIssueDescription());
        newWarrantyRepair.setRepairDate(new Date());
        newWarrantyRepair.setDate_completed(new Date());
        newWarrantyRepair.setStatus("Warranty Pending");
        newWarrantyRepair.setDeleted(false);
        newWarrantyRepair.setUser(user);
        newWarrantyRepair.setOrder(order);
        newWarrantyRepair.setImage(warrantyDTO.getImage());
        newWarrantyRepair.setProductName(productName);
        newWarrantyRepair.setTechnical(technical);

        // Lưu đối tượng WarrantyRepair vào cơ sở dữ liệu
        return warrantyRepairRepository.save(newWarrantyRepair);
    }

    @Override
    public int getTechnicalMinOrder() {
        return warrantyRepairRepository.getTechnicalMinOrder();
    }

    @Override
    public List<WarrantyRepair> getByOrder(int oderId) {
        return  warrantyRepairRepository.findByOrderAndProduct(oderId);
    }


    @Override
    // Cập nhật status đơn
    public void changeStatusAndSaveNote(int id, String status,String note) {
        Optional<WarrantyRepair> optionalRepair = warrantyRepairRepository.findById(id);
        if (optionalRepair.isPresent()) {
            WarrantyRepair repair = optionalRepair.get();
            if (!repair.getStatus().equals(status)) {
                repair.setStatus(status);
                repair.setNoteTechnical(note);
                repair.setDate_completed(new Date());


                warrantyRepairRepository.save(repair);
//            } else {
//                throw new IllegalArgumentException("The status is already set to " + status);
//            }
//        } else {
//            throw new NoSuchElementException("WarrantyRepair with id " + id + " not found");
//        }
            }
        }
    }
    @Override
    public Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByTechnical(String productName, String status, Date repairDate, int technicalId, PageDTO pageDTO) {
        return warrantyRepairRepository.findAllByProductNameAndStatusAndDateByTechnical(productName, status, repairDate, technicalId, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<WarrantyRepair> findAllByProductNameAndRepairDateByTechnical(String productName, Date repairDate, int technicalId, PageDTO pageDTO) {
        return warrantyRepairRepository.findAllByProductNameAndRepairDateByTechnical(productName, repairDate, technicalId, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<WarrantyRepair> findAllByStatusAndRepairDateByTechnical(String status, Date repairDate, int technicalId, PageDTO pageDTO) {
        return warrantyRepairRepository.findAllByStatusAndRepairDateByTechnical(status, repairDate, technicalId, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<WarrantyRepair> findAllByProductNameAndStatusByTechnical(String productName, String status, int technicalId, PageDTO pageDTO) {
        return warrantyRepairRepository.findAllByProductNameAndStatusByTechnical(productName, status, technicalId, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }
    @Override
    public Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByAdmin(String productName, String status, Date repairDate, PageDTO pageDTO) {
        return warrantyRepairRepository.findAllByProductNameOrUserAndStatusAndRepairDate(productName, repairDate,status, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }
    @Override
    public Page<WarrantyRepair> findAllByProductNameAndRepairDateByAdmin(String productName, Date repairDate, PageDTO pageDTO) {
        return warrantyRepairRepository.findAllByProductNameAndRepairDateByAdmin(productName, repairDate, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<WarrantyRepair> findAllByStatusAndRepairDateByAdmin(String status, Date repairDate, PageDTO pageDTO) {
        return warrantyRepairRepository.findAllByStatusAndRepairDateByAdmin(status, repairDate, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<WarrantyRepair> findAllByProductNameAndStatusByAdmin(String productName, String status, PageDTO pageDTO) {
        return warrantyRepairRepository.findAllByProductNameAndStatusByAdmin(productName, status, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

}
