package com.example.PhoneManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Entity(name="useraccount")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    int userId;

    @Column(name = "username", nullable = false, length = 50)
    String userName;

    @Column(name = "password", nullable = false, length = 50)
    String password;

    @Column(name="fullname", nullable = false, length = 50)
    String fullName;

    @Column(name = "address", nullable = false, length = 50)
    String address;

    @Column(name = "phone_number", nullable = false, length = 50)
    String phoneNumber;

    @Column(name="active")
    boolean active=true;

    @Column(name="created_at")
    Date createdAt;

    @Lob
    @Column(name="avatar", columnDefinition = "LONGTEXT")
    String avatar;

    @ManyToOne
    @JoinColumn(name="role_id")
    Roles role;


    @OneToMany(mappedBy = "user")
    List<WarrantyRepair> warrantyRepairList;

    @OneToMany(mappedBy = "user")
    List<Orders> ordersList;

    @OneToMany(mappedBy = "user")
    List<Purchase> purchaseList;

}
