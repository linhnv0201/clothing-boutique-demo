package com.ecommerce.clothingwebapp.controller;

import com.ecommerce.clothingwebapp.dto.ProductDTO;
import com.ecommerce.clothingwebapp.dto.UserDto;
import com.ecommerce.clothingwebapp.model.*;
import com.ecommerce.clothingwebapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    public static String uploadDir = System.getProperty("user.dir") + "/clothingwebapp/src/main/resources/static/productImages";

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
    @GetMapping("/admin")
    public String adminHome(){
        return "adminHome";
    }
    @GetMapping("/admin/categories")
    public String getCat(Model model){
        model.addAttribute("categories", categoryService.getAllCategory());
        return "categories";
    }
    @GetMapping("admin/categories/add")
    public String getCatAdd(Model model){
        model.addAttribute("category", new Category());
        return "categoriesAdd";
    }
    @PostMapping("admin/categories/add")
    public String postCatAdd(@ModelAttribute("category") Category category){
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }
    @GetMapping("admin/categories/update/{id}")
    public String updateCat(@PathVariable int id, Model model){
        Optional<Category> category = categoryService.getCategoryById(id);
        category.ifPresent(value -> model.addAttribute("category", value));
            return "categoriesAdd";
    }
    @GetMapping("admin/categories/delete/{id}")
    public String deleteCat(@PathVariable int id){
        List<Product> productList = productService.getAllProductsByCategoryId(id);
        for (Product product: productList){
            product.setCategory(null);
        }
        categoryService.removeCategoryByID(id);
        return "redirect:/admin/categories";
    }

    //Product Section
    @GetMapping("admin/products")
    public String getProduct(Model model){
        model.addAttribute("products", productService.getAllProduct());
        return "products";
    }
    @GetMapping("admin/products/add")
    public String addProduct(Model model){
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";
    }
    @PostMapping("admin/products/add")
    public String productAddPost(@ModelAttribute("productDTO") ProductDTO productDTO,
                                 @RequestParam("productImage")MultipartFile file,
                                 @RequestParam("imgName") String imgName) throws IOException{
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
        product.setSize(productDTO.getSize());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setDescription(productDTO.getDescription());
        String imageUUID;
        if (!file.isEmpty()){
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.write(fileNameAndPath,file.getBytes());
        } else {
            imageUUID = imgName;
        }
        product.setImageName(imageUUID);
        productService.addProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("admin/product/update/{id}")
    public String updateProduct(@PathVariable long id, Model model){
        Product product = productService.getProductById(id).get();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageName(product.getImageName());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setSize(product.getSize());
        Category category = product.getCategory();
        if (category != null) {
            productDTO.setCategoryId(category.getId());
        } else {
            // Nếu category là null, đặt giá trị unknown có id = 602 (Others)
            productDTO.setCategoryId(602);
        }
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("productDTO", productDTO);

        return "productsAdd";
    }

    @GetMapping("admin/product/delete/{id}")
    public String deleteProduct(@PathVariable long id, RedirectAttributes redirectAttributes) {
    try {
        productService.removeProductById(id);
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage",
                "The feature has not been updated because it affects orders. We will fix this issue soon.");
    }
    return "redirect:/admin/products";
}

    //User management
    @GetMapping("/admin/users")
    public String listRegisteredUsers(Model model){
        model.addAttribute("users", userService.findAllUsers());
        return "users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable long id, RedirectAttributes redirectAttributes){
        try {
        userService.removeUserById(id);
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage",
                    "The feature has not been updated because it affects orders. We will fix this issue soon.");
        }
        return "redirect:/admin/users";
    }
    @PostMapping("admin/users/update")
    public String update1UserProfile(@ModelAttribute("userDto") UserDto userDto){
        userService.changeUserInfo(
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPhone(),
                userDto.getAddress());
        return "redirect:/admin/users";
    }
    @GetMapping("/admin/users/update/{id}")
    public String show1UserProfileUpdateForm(@PathVariable Long id, Model model){
        User user = userService.getUserById(id).get();
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setAddress(user.getAddress());
        model.addAttribute("userDto", userDto);
        return "adminUpdateUser";
    }

// Revenue
    @GetMapping("/admin/revenue")
    public String showOrderDetail(Model model){
        double revenue = 0;
        List<Order> orderList = orderService.findAllOrder();
    for(Order order: orderList){
        revenue += order.getTotal();
    }
        model.addAttribute("revenue", revenue);
        model.addAttribute("orderList", orderList);
        return "revenue";
    }
    @GetMapping("/admin/revenue/orderDetail/{id}")
    public String showOrderDetail(@PathVariable Long id, Model model){
        List<OrderItem> orderItemList = orderItemService.getOrderItemsByOrderId(id);
        Order order = orderService.findOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("itemList", orderItemList);
        return "orderDetail";
    }
    //Admin info
    @GetMapping("/admin/profile")
    public String showAdminProfile(Model model, Principal principal) {
        String userEmail = principal.getName(); // Lấy email
        // Đưa thông tin vào model để sử dụng trên giao diện
        model.addAttribute("currentUser", userService.findByEmail((userEmail)));
        return "admin-profile";
    }

    @PostMapping("admin/profile/update")
    public String updateAdminProfile(@ModelAttribute("userDto") UserDto userDto){
        userService.changeUserInfo(
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPhone(),
                userDto.getAddress());
        return "redirect:/admin/profile";
    }
    @GetMapping("/admin/profile/update/{id}")
    public String showAdminProfileUpdateForm(@PathVariable Long id, Model model){
        User user = userService.getUserById(id).get();
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setAddress(user.getAddress());
        model.addAttribute("userDto", userDto);
        return "adminProfileUpdate";
    }
}


