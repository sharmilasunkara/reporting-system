package com.example.reporting_system.controller;

import com.example.reporting_system.dto.ApiResponseDto;
import com.example.reporting_system.dto.TransactionRequestDto;
import com.example.reporting_system.entity.EmployeeTransaction;
import com.example.reporting_system.service.TransactionService;
import com.example.reporting_system.service.impl.TransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController (TransactionService transactionService){
        this.transactionService=transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto saveTransaction(@Valid @RequestBody TransactionRequestDto dto){
         transactionService.saveTransaction(dto);
         return new ApiResponseDto("SUCCESS","Transaction Saved Successfully");
    }

    @GetMapping
    public List<EmployeeTransaction> getAllTransactions(){

       return  transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public EmployeeTransaction getTransactionById(@PathVariable Long id){

        return transactionService.getTransactionById(id);
    }

    @PutMapping("/{id}")
    public EmployeeTransaction updateTransactionByID(@Valid @PathVariable Long id,@RequestBody TransactionRequestDto dto){

        return transactionService.updateTransaction(id,dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponseDto deleteTransaction(
            @PathVariable Long id) {

        transactionService.deleteTransaction(id);

        return new ApiResponseDto(
                "SUCCESS",
                "Transaction deleted successfully"
        );
    }
}
