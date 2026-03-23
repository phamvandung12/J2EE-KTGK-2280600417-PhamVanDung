package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.Enrollment;
import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByStudentAndCourse(Account student, Product course);
    List<Enrollment> findByStudent(Account student);
}
