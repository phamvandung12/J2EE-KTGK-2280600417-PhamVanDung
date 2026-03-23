package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "Mã học phần không được để trống")
    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode;
    
    @NotBlank(message = "Tên học phần không được để trống")
    @Column(nullable = false)
    private String name;
    
    @NotNull(message = "Số tín chỉ không được để trống")
    @Min(value = 1, message = "Số tín chỉ phải từ 1 đến 9999999")
    @Max(value = 9999999, message = "Số tín chỉ phải từ 1 đến 9999999")
    @Column(name = "credits", nullable = false)
    private Integer price; // dùng làm số tín chỉ

    @NotBlank(message = "Giảng viên không được để trống")
    @Column(name = "lecturer", nullable = false)
    private String lecturer;
    
    @Column(name = "image")
    private String imageName;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Danh mục không được để trống")
    private Category category;
}
