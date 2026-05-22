package com.example.reporting_system.service;

import com.example.reporting_system.dto.TransactionRequestDto;
import com.example.reporting_system.entity.EmployeeTransaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    void saveTransaction(TransactionRequestDto dto);

    List<EmployeeTransaction> getAllTransactions();

    EmployeeTransaction getTransactionById(Long id);

    EmployeeTransaction updateTransaction(
            Long id,
            TransactionRequestDto dto);

    void deleteTransaction(Long id);
}
