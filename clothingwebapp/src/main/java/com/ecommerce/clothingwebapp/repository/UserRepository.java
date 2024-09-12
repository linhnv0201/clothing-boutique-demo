package com.ecommerce.clothingwebapp.repository;

import com.ecommerce.clothingwebapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}
