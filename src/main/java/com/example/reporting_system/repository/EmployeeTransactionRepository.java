package com.example.reporting_system.repository;

import com.example.reporting_system.entity.EmployeeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeTransactionRepository extends JpaRepository<EmployeeTransaction,Long> {

    List<EmployeeTransaction>
    findByCreatedAtAfter(LocalDateTime time);
}
