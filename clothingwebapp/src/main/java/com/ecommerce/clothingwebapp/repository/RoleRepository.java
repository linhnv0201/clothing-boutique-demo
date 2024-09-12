package com.ecommerce.clothingwebapp.repository;

import com.ecommerce.clothingwebapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
