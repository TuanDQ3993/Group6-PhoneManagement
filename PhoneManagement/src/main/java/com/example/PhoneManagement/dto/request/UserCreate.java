package com.example.PhoneManagement.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreate {

    String userName;
    String password;
    String fullName;
    String address;
    String phoneNumber;
    int roleId;
}
