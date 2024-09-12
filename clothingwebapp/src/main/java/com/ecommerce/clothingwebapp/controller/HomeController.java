package com.ecommerce.clothingwebapp.controller;

import com.ecommerce.clothingwebapp.dto.UserDto;
import com.ecommerce.clothingwebapp.model.Order;
import com.ecommerce.clothingwebapp.model.OrderItem;
import com.ecommerce.clothingwebapp.model.User;
import com.ecommerce.clothingwebapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    @GetMapping({"/", "/index", "/home"})
    public String home() {
        return "index";
    }

    @GetMapping("/shop")
    public String shop(Model model) {
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", productService.getAllProduct());
        return "shop";
    }

    @GetMapping("/shop/category/{id}")
    public String shopByCategory(Model model, @PathVariable int id) {
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", productService.getAllProductsByCategoryId(id));
        return "shop";
    }

    @GetMapping("/shop/viewproduct/{id}")
    public String viewProduct(Model model, @PathVariable int id) {
        model.addAttribute("product", productService.getProductById(id).get());
        return "viewProduct";
    }

    @GetMapping("/shop/profile")
    public String showUserProfile(Model model, Principal principal) {
        String userEmail = principal.getName(); // Lấy email

        User user = userService.findByEmail(userEmail);
        List<Order> userOrders = orderService.findAllOrdersByUserId(user.getId());

        model.addAttribute("userOrders", userOrders);
        // Đưa thông tin vào model để sử dụng trên giao diện
        model.addAttribute("current", userService.findByEmail((userEmail)));
        return "user-profile";
    }
    @PostMapping("shop/profile/update")
    public String updateUserProfile(@ModelAttribute("userDto") UserDto userDto){
        userService.changeUserInfo(
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPhone(),
                userDto.getAddress());
        return "redirect:/shop/profile";
    }
    @GetMapping("/shop/profile/update/{id}")
    public String showUserProfileUpdateForm(@PathVariable Long id, Model model){
        User user = userService.getUserById(id).get();
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setAddress(user.getAddress());
        model.addAttribute("userDto", userDto);
        return "userProfileUpdate";
    }

    @GetMapping("/shop/profile/orderDetail/{id}")
    public String showOrderDetail(@PathVariable Long id, Model model){
        List<OrderItem> orderItemList = orderItemService.getOrderItemsByOrderId(id);
        Order order = orderService.findOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("itemList", orderItemList);
        return "orderDetail";
    }
}