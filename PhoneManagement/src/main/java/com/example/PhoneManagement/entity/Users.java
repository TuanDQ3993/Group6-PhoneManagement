package com.example.PhoneManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity(name="useraccount")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.getRoleName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getUserName() {
        return userName;
    }

}
