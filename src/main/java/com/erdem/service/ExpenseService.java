package com.erdem.service;

import com.erdem.dto.ExpenseResponse;
import com.erdem.model.Expense;
import com.erdem.model.ExpenseCategory;
import com.erdem.model.User;
import com.erdem.repository.IExpenseCategoryRepository;
import com.erdem.repository.IExpenseRepository;
import com.erdem.repository.IUserRepository;
import com.erdem.exception.NotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final IExpenseRepository expenseRepository;
    private final IUserRepository userRepository;
    private final IExpenseCategoryRepository categoryRepository;

    public ExpenseService(IExpenseRepository expenseRepository, IUserRepository userRepository, IExpenseCategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public ExpenseResponse addExpense(String description, BigDecimal amount, Long categoryId){

        String username= ((UserDetails)SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .getUsername();


        User user=userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found... "+username));

        ExpenseCategory category=categoryRepository.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found... "+categoryId));

        Expense expense=new Expense();

        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setDate(LocalDateTime.now());
        expense.setCategory(category);
        expense.setUser(user);

        return convertDTO(expenseRepository.save(expense));
    }

    public List<ExpenseResponse> getExpenseForUser(String username){

        User user=userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found... "+username));

        return expenseRepository.findByUser(user).stream().map(this::convertDTO).collect(Collectors.toList());
    }

    public List<ExpenseResponse> getExpensesForUserBetweenDates(String username,LocalDateTime start,LocalDateTime end){

        User user= userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found... "+username));

        return expenseRepository.findByUserAndDateBetween(user,start,end).stream().map(this::convertDTO).collect(Collectors.toList());
    }

    public ExpenseResponse updateExpense(Long id, String description, BigDecimal amount,Long categoryId){

        Expense expense=expenseRepository.findById(id).orElseThrow(()->new NotFoundException("Expense not found... "+id));

        ExpenseCategory category= categoryRepository.findById(categoryId).orElseThrow(()-> new NotFoundException(" Category not found... "+categoryId));

        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setDate(LocalDateTime.now());
        expense.setCategory(category);

        return convertDTO(expenseRepository.save(expense));

    }

    public void deleteExpense(Long id){
        Expense expense=expenseRepository.findById(id).orElseThrow(()-> new NotFoundException("Expense not found... "+id));

         expenseRepository.delete(expense);
    }

    private ExpenseResponse convertDTO(Expense expense){
        return new ExpenseResponse(expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getDate(),
                expense.getCategory().getName());
    }
}
