package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.dto.request.WarrantyDTO;
import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.WarrantyRepair;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WarrantyService {
    Page<WarrantyRepair> findAll(PageDTO pageable);

    Page<WarrantyRepair> findPaginated(PageDTO pageable, int id);

    boolean isExistRepairDate(Date date);

    List<WarrantyRepair> getWarrantyByIdAndRepairDate(int technicalId, Date date);

    List<WarrantyRepair> getWarrantyByIdAndRepairDateByAdmin(Date date);

    void save(WarrantyRepair wr);

    Page<WarrantyRepair> findByProductNameAndUserName(PageDTO pageable, String query, int technicalId);

    Page<WarrantyRepair> findByRepairDate(PageDTO pageable, Date date, int technicalId);

    Page<WarrantyRepair> findByStatus(PageDTO pageable, String status, int technicalId);

    void deleteWarrantyRepair(int id);

    long sumWarrantyRepairs(int technicalId);

    long sumWarrantyRepairsByRepairDate(int technicalId, Date date);

    long countWarrantyRepairsByRepairDateAndStatus(int technicalId, Date date);

    long countAllWarrantyRepairsAndStatus(int technicalId);

    long countAllByAdmin();

    long countAllByStatusByAdmin();

    Page<WarrantyRepair> findAllByProductNameByAdmin(PageDTO pageable, String query);

    Page<WarrantyRepair> findByRepairDateByAdmin(PageDTO pageable, Date date);

    Page<WarrantyRepair> findByStatusByAdmin(PageDTO pageable, String status);

    Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByAdmin(PageDTO pageable, String status, Date date, String query);

    void changeStatus(int id, String status);

    void acceptWarranty(int id);

    void cancelWarranty(int id);

    Map<String, Long> getWarrantyCountByDate(int id);

    WarrantyRepair createWarrantyRepair(WarrantyDTO warrantyDTO, Orders order, Users user, Users technical, String productName);

    int getTechnicalMinOrder();

    List<WarrantyRepair> getByOrder(int oderId);

    Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByTechnical(String productName, String status, Date repairDate, int technicalId, PageDTO pageable);

    Page<WarrantyRepair> findAllByProductNameAndRepairDateByTechnical(String productName, Date repairDate, int technicalId, PageDTO pageable);

    Page<WarrantyRepair> findAllByStatusAndRepairDateByTechnical(String status, Date repairDate, int technicalId, PageDTO pageable);

    Page<WarrantyRepair> findAllByProductNameAndStatusByTechnical(String productName, String status, int technicalId, PageDTO pageable);

    Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByAdmin(String productName, String status, Date repairDate, PageDTO pageable);

    Page<WarrantyRepair> findAllByProductNameAndRepairDateByAdmin(String productName, Date repairDate, PageDTO pageable);

    Page<WarrantyRepair> findAllByStatusAndRepairDateByAdmin(String status, Date repairDate, PageDTO pageable);

    Page<WarrantyRepair> findAllByProductNameAndStatusByAdmin(String productName, String status, PageDTO pageable);

    void changeStatusAndSaveNote(int id, String status, String note);
}
