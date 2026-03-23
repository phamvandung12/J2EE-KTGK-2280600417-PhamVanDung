package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.Enrollment;
import com.example.demo.model.Product;
import com.example.demo.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public void enroll(Account student, Product course) {
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            return; // đã đăng ký rồi thì bỏ qua
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsForStudent(Account student) {
        return enrollmentRepository.findByStudent(student);
    }
}
