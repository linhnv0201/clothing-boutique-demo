package com.ecommerce.clothingwebapp.global;

import com.ecommerce.clothingwebapp.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GlobalData {
    public static List<CartItem> cart;
    static {
            cart=new ArrayList<>();
    }
    public static void addToCart(Product product) {
        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> Objects.equals(item.getProduct().getId(), product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
            existingItem.get().setQuantity(Math.min((existingItem.get().getQuantity() + 1), product.getQuantity()));
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, thêm vào giỏ hàng
            CartItem newItem = new CartItem(product, Math.min(1, product.getQuantity()));
            cart.add(newItem);
        }
    }
}


