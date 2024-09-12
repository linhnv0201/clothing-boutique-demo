package com.ecommerce.clothingwebapp.repository;

import com.ecommerce.clothingwebapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllUsersByUserId(Long userId);
    Order findOrderById(Long id);

}
