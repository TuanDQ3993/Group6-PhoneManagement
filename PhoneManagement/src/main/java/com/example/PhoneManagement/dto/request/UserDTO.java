package com.example.PhoneManagement.dto.request;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    String userName;
    String fullName;
    String address;
    String phoneNumber;
    String roleName;
    String avatar;

}
