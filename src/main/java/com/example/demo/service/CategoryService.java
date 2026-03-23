package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    public void init() {
        // Khởi tạo dữ liệu mẫu nếu database trống
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category(null, "Cơ sở"));
            categoryRepository.save(new Category(null, "Chuyên ngành"));
        }
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, Category updatedCategory) {
        if (categoryRepository.existsById(id)) {
            updatedCategory.setId(id);
            return categoryRepository.save(updatedCategory);
        }
        return null;
    }

    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}
