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

//    void editAccount(Users users);

    boolean isPhoneExist(String phone);

    boolean isEmailExist(String email);

    void createUser(Users user);

    Page<Users> searchAndFilterUsers(String userName, PageDTO pageDTO, int role);

    Page<Users> searchUsers(String query, PageDTO pageDTO);

    Page<Users> filterRole(int roleId, PageDTO pageDTO);
    boolean isValidPhoneNumber(String phone);
    boolean isValidEmail(String email);


}
