package com.ecommerce.clothingwebapp.service;

import com.ecommerce.clothingwebapp.model.Order;
import com.ecommerce.clothingwebapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    public List<Order> findAllOrdersByUserId(Long userId) {
        return orderRepository.findAllUsersByUserId(userId);
    }
    public Order findOrderById(Long userId){
        return orderRepository.findOrderById(userId);
    }
    public List<Order> findAllOrder(){
        return orderRepository.findAll();
    }
}
