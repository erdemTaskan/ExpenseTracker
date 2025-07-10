package com.erdem.controller;

import com.erdem.dto.ExpenseRequest;
import com.erdem.dto.ExpenseResponse;
import com.erdem.model.Expense;
import com.erdem.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(@Valid @RequestBody ExpenseRequest request){

        ExpenseResponse expense= expenseService.addExpense(request.getDescription(),request.getAmount(),request.getCategoryId());


        return ResponseEntity.ok(expense);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesForUser(@PathVariable String username){
        List<ExpenseResponse> expenses=expenseService.getExpenseForUser(username);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{username}/between")
    public ResponseEntity<List<ExpenseResponse>> getExpensesForUserBetweenDates(
            @PathVariable String username,
            @RequestParam String start,
            @RequestParam String end
    ){
        LocalDateTime startDate= LocalDateTime.parse(start);
        LocalDateTime endDate=LocalDateTime.parse(end);
        List<ExpenseResponse> expenses=expenseService.getExpensesForUserBetweenDates(username,startDate,endDate);


        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request){

        ExpenseResponse updateExpense= expenseService.updateExpense(
                id,
                request.getDescription(),
                request.getAmount(),
                request.getCategoryId());


        return ResponseEntity.ok(updateExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense deleted...");
    }
}
