package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/courses")
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping
    public String listProducts(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "keyword", required = false) String keyword) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productService.searchProductsByName(keyword.trim(), pageable);
        } else {
            productPage = productService.getProducts(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("keyword", keyword);
        return "product-list";
    }

    @GetMapping("/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute Product product,
                           BindingResult result,
                           @RequestParam(value = "image", required = false) MultipartFile image,
                           Model model) {
        
        // Xử lý upload hình ảnh
        if (image != null && !image.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
            
            if (originalFilename != null) {
                try {
                    // Tạo thư mục nếu chưa tồn tại
                    File uploadDir = new File(UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    // Lưu file
                    Path path = Paths.get(UPLOAD_DIR + originalFilename);
                    Files.write(path, image.getBytes());
                    product.setImageName(originalFilename);
                } catch (IOException e) {
                    result.rejectValue("imageName", "error.product", 
                        "Lỗi khi upload hình ảnh");
                }
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "add-product";
        }

        productService.addProduct(product);
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "edit-product";
        }
        return "redirect:/courses";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable Integer id,
                            @Valid @ModelAttribute Product product,
                            BindingResult result,
                            @RequestParam(value = "image", required = false) MultipartFile image,
                            Model model) {
        
        // Giữ lại imageName cũ nếu không upload ảnh mới
        Product existingProduct = productService.getProductById(id);
        if (existingProduct != null && (image == null || image.isEmpty())) {
            product.setImageName(existingProduct.getImageName());
        }

        // Xử lý upload hình ảnh mới
        if (image != null && !image.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
            
            if (originalFilename != null) {
                try {
                    File uploadDir = new File(UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    Path path = Paths.get(UPLOAD_DIR + originalFilename);
                    Files.write(path, image.getBytes());
                    product.setImageName(originalFilename);
                } catch (IOException e) {
                    result.rejectValue("imageName", "error.product", 
                        "Lỗi khi upload hình ảnh");
                }
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "edit-product";
        }

        productService.updateProduct(id, product);
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return "redirect:/courses";
    }
}
