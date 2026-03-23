package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Enrollment;
import com.example.demo.model.Product;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.EnrollmentService;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/enroll")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final ProductService productService;
    private final AccountRepository accountRepository;

    public EnrollmentController(EnrollmentService enrollmentService,
                                ProductService productService,
                                AccountRepository accountRepository) {
        this.enrollmentService = enrollmentService;
        this.productService = productService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{courseId}")
    public String enrollCourse(@PathVariable Integer courseId, Principal principal) {
        Product course = productService.getProductById(courseId);
        if (course == null || principal == null) {
            return "redirect:/courses";
        }
        Account student = accountRepository.findByLoginName(principal.getName()).orElse(null);
        if (student == null) {
            return "redirect:/courses";
        }

        enrollmentService.enroll(student, course);
        return "redirect:/courses";
    }

    @GetMapping("/my-courses")
    public String myCourses(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Account student = accountRepository.findByLoginName(principal.getName()).orElse(null);
        if (student == null) {
            return "redirect:/login";
        }

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsForStudent(student);
        model.addAttribute("enrollments", enrollments);
        return "my-courses";
    }
}
