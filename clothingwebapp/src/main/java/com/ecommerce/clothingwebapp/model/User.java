package com.ecommerce.clothingwebapp.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String firstName;
    @Column(nullable=false)
    private String lastName;

    @Column(nullable=false)
    private String phone;

    @Column(nullable=false)
    private String address;
    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    @ManyToMany (fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name="users_roles",
            joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
            inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ID")})
    private List<Role> roles = new ArrayList<>();

//    @PreRemove
//    private void preRemove() {
//        // Set user_id trong users_roles là null khi xóa user
//        for (Role role : roles) {
//            role.getUsers().remove(this);
//        }
//        roles.clear();
//    }

}