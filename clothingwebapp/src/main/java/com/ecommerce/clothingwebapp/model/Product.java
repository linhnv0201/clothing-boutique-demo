package com.ecommerce.clothingwebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;
    private String size;
    private double price;
    @Min(value = 0, message = "Quantity must be non-negative")
    private int quantity;
    private String description;
    private String imageName;
//    @PreRemove
//    private void preRemove() {
//        // Set category_id trong Product là null khi xóa Category
//        this.category = null;
//    }
}
