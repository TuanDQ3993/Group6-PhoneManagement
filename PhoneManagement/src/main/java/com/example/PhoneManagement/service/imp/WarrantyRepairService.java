package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.WarrantyRepair;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WarrantyRepairService {

    Page<WarrantyRepair> findPaginated(PageDTO pageable,int id);
    void addWarrantyRepair(WarrantyRepair wr);
    void updateWarrantyRepair(WarrantyRepair wr);
    void deleteWarrantyRepair(int id);
    List<WarrantyRepair>  searchByName(String text);

}
