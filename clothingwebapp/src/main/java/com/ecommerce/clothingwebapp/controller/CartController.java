package com.ecommerce.clothingwebapp.controller;

import com.ecommerce.clothingwebapp.global.CartItem;
import com.ecommerce.clothingwebapp.global.GlobalData;
import com.ecommerce.clothingwebapp.model.Order;
import com.ecommerce.clothingwebapp.model.OrderItem;
import com.ecommerce.clothingwebapp.model.Product;
import com.ecommerce.clothingwebapp.repository.OrderRepository;
import com.ecommerce.clothingwebapp.service.ProductService;
import com.ecommerce.clothingwebapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CartController {
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    OrderRepository orderRepository;

    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable int id){
        GlobalData.addToCart(productService.getProductById(id).get());
        return "redirect:/shop";
    }

    @GetMapping("/cart")
    public String cart(Model model){

        model.addAttribute("cartCount", GlobalData.cart.size());
        model.addAttribute("total", GlobalData.cart.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum());
        model.addAttribute("cart", GlobalData.cart);
        return "cart";
    }
    @GetMapping("/cart/addQuantity/{id}")
    public String addQuantity(@PathVariable int id) {
        // Tìm sản phẩm trong giỏ hàng dựa trên id
        CartItem cartItem = GlobalData.cart.stream()
                .filter(item -> item.getProduct().getId() == id)
                .findFirst()
                .orElse(null);

        int x = cartItem.getQuantity() + 1;
        int y = cartItem.getProduct().getQuantity();
        cartItem.setQuantity(Math.min(x,y));

        return "redirect:/cart";
    }
    @GetMapping("/cart/minusQuantity/{id}")
    public String minusQuantity(@PathVariable int id) {
        // Tìm sản phẩm trong giỏ hàng dựa trên id
        CartItem cartItem = GlobalData.cart.stream()
                .filter(item -> item.getProduct().getId() == id)
                .findFirst()
                .orElse(null);

        int x = cartItem.getQuantity() - 1;
        cartItem.setQuantity(Math.max(x,1));

        return "redirect:/cart";
    }


    @GetMapping("/cart/removeItem/{id}")
    public String cartRemoveItem(@PathVariable int id){
        GlobalData.cart.remove(id);
        return "redirect:/cart";
    }
    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal){
        String userEmail = principal.getName(); // Lấy email
        double total = GlobalData.cart.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
        if(total > 0){
        model.addAttribute("user", userService.findByEmail(userEmail));
        model.addAttribute("total", total);
        model.addAttribute("cart",GlobalData.cart);
        model.addAttribute("itemType", GlobalData.cart.size());
        return "checkout";
        }
        else
            return "redirect:/cart";
    }
    @PostMapping("/checkout/paynow")
    public String payNow(Principal principal) {
        String userEmail = principal.getName();

        // Tính toán tổng giá trị đơn hàng từ giỏ hàng
        double total = GlobalData.cart.stream()
                .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .sum();

        // Tạo đối tượng Order
        Order order = new Order();
        order.setUser(userService.findByEmail(userEmail));
        order.setTotal(total);

        // Lưu chi tiết đơn hàng
        List<OrderItem> orderItems = GlobalData.cart.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setSubtotal(cartItem.getProduct().getPrice() * cartItem.getQuantity());
                    orderItem.setOrder(order);

                    // Cập nhật số lượng hàng trong kho
                    Product product = cartItem.getProduct();
                    product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                    productService.saveProduct(product);

                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        // Lưu đơn hàng vào cơ sở dữ liệu
        orderRepository.save(order);

        // Xóa giỏ hàng sau khi đã thanh toán
        GlobalData.cart.clear();

        return "payment-success"; // Thay đổi thành trang cần chuyển hướng sau khi thanh toán thành công
    }

}
