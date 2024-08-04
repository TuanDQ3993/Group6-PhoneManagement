package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.WarrantyRepair;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface WarrantyService {
    Page<WarrantyRepair> findAll(PageDTO pageable);

    Page<WarrantyRepair> findPaginated(PageDTO pageable, int id);

    boolean isExistRepairDate(Date date);

     List<WarrantyRepair> getWarrantyByIdAndRepairDate(int technicalId, Date date);
     List<WarrantyRepair> getWarrantyByIdAndRepairDateByAdmin(Date date);
    void save(WarrantyRepair wr);

    Page<WarrantyRepair> findByProductNameAndUserName(PageDTO pageable, String query,int technicalId);
    Page<WarrantyRepair> findByRepairDate(PageDTO pageable, Date date,int technicalId);
    Page<WarrantyRepair> findByStatus(PageDTO pageable, String status,int technicalId);
    Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByTecnical(PageDTO pageable, String status, int technicalId, Date date,String query);
    void deleteWarrantyRepair(int id);

    long sumWarrantyRepairs(int technicalId);

    long sumWarrantyRepairsByRepairDate(int technicalId, Date date);

    long countWarrantyRepairsByRepairDateAndStatus(int technicalId, Date date);

    long countAllWarrantyRepairsAndStatus(int technicalId);


    Page<WarrantyRepair> findAllByProductNameByAdmin(PageDTO pageable,String query);
    Page<WarrantyRepair> findByRepairDateByAdmin(PageDTO pageable, Date date);
    Page<WarrantyRepair> findByStatusByAdmin(PageDTO pageable, String status);
    Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByAdmin(PageDTO pageable, String status, Date date,String query);

}
