package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.Users;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountService {
    List<Users> getAll();

    Page<Users> findPaginated(PageDTO pageable);

    void banUser(int userId);

    void unBanUser(int userId);

    Users getUserById(int userId);

    List<Users> searchUsers(String text);


}
