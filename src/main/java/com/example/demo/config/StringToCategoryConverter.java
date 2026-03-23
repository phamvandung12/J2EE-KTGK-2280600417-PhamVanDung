package com.example.demo.config;

import com.example.demo.model.Category;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCategoryConverter implements Converter<String, Category> {
    
    @Autowired
    private CategoryService categoryService;
    
    @Override
    public Category convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            Integer categoryId = Integer.valueOf(source);
            return categoryService.getCategoryById(categoryId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
