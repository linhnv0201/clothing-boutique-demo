package com.ecommerce.clothingwebapp.repository;

import com.ecommerce.clothingwebapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
