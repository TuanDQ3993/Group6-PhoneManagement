package com.example.PhoneManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Entity(name="useraccount")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    int userId;

    @Column(name = "username")
    String userName;

    @Column(name = "password")
    String password;

    @Column(name="fullname")
    String fullName;

    @Column(name = "address")
    String address;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name="active")
    boolean active=true;

    @Column(name="created_at")
    Date createdAt;

    @ManyToOne
    @JoinColumn(name="role_id")
    Roles role;

    @OneToMany(mappedBy = "user")
    List<Sales> salesList;

    @OneToMany(mappedBy = "user")
    List<WarrantyRepair> warrantyRepairList;

    @OneToMany(mappedBy = "user")
    List<ReturnProduct> returnProductList;


}
