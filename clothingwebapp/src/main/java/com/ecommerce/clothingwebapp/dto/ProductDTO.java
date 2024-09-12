package com.ecommerce.clothingwebapp.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private int categoryId;
    private String size;
    private Double price;
    private int quantity;
    private String description;
    private String imageName;
}
