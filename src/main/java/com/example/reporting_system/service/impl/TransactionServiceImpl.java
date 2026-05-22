package com.example.reporting_system.service.impl;

import com.example.reporting_system.dto.TransactionRequestDto;
import com.example.reporting_system.entity.EmployeeTransaction;
import com.example.reporting_system.exception.ResourceNotFoundException;
import com.example.reporting_system.repository.EmployeeTransactionRepository;
import com.example.reporting_system.service.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private EmployeeTransactionRepository employeeTransactionRepository;

    public TransactionServiceImpl(EmployeeTransactionRepository employeeTransactionRepository) {
        this.employeeTransactionRepository = employeeTransactionRepository;
    }

    @Override
    public void saveTransaction(TransactionRequestDto dto) {

        EmployeeTransaction employeeTransaction = new EmployeeTransaction();
        employeeTransaction.setEmployeeName(dto.getEmployeeName());
        employeeTransaction.setDepartment(dto.getDepartment());
        employeeTransaction.setAmount(dto.getAmount());
        employeeTransaction.setCreatedAt(LocalDateTime.now());

        employeeTransactionRepository.save(employeeTransaction);

    }

    @Override
    public List<EmployeeTransaction> getAllTransactions() {
        return employeeTransactionRepository.findAll();
    }

    @Override
    public EmployeeTransaction getTransactionById(Long id) {
        return employeeTransactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    @Override
    public EmployeeTransaction updateTransaction(Long id, TransactionRequestDto dto) {

        EmployeeTransaction transaction = employeeTransactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found with id: " + id
                        )
                );
        transaction.setEmployeeName(dto.getEmployeeName());
        transaction.setDepartment(dto.getDepartment());
        transaction.setAmount(dto.getAmount());


        return employeeTransactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        EmployeeTransaction empTransaction = employeeTransactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found with id: " + id
                        )
                );
        employeeTransactionRepository.delete(empTransaction);
    }
}
