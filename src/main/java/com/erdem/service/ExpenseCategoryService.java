package com.erdem.service;

import com.erdem.dto.ExpenseCategoryResponse;
import com.erdem.model.ExpenseCategory;
import com.erdem.repository.IExpenseCategoryRepository;
import exception.BadRequestException;
import exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseCategoryService {

    private final IExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategoryService(IExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public ExpenseCategoryResponse createCategory(String name){

        if (expenseCategoryRepository.findByName(name).isPresent()){
            throw new BadRequestException("This category already exists...");
        }
        ExpenseCategory category=new ExpenseCategory();
        category.setName(name);
        return convertToDto(expenseCategoryRepository.save(category));

    }

    public List<ExpenseCategoryResponse> getAllCategories(){
        return expenseCategoryRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ExpenseCategoryResponse getCategoryById(Long id){
        return convertToDto(expenseCategoryRepository.findById(id).orElseThrow(()-> new NotFoundException("Category is not found...")));
    }
    @Transactional
    public ExpenseCategoryResponse updateCategory(Long id, String newName){
        ExpenseCategory category=expenseCategoryRepository.findById(id).orElseThrow(()-> new NotFoundException("Category is not found..."));

        expenseCategoryRepository.findByName(newName).ifPresent(existing -> {
            if (!existing.getId().equals(id)){
                throw new BadRequestException("This category name already exists...");
            }
        });
        category.setName(newName);
        return convertToDto(expenseCategoryRepository.save(category));
    }

    public void deleteCategory(Long id){
        ExpenseCategory category= expenseCategoryRepository.findById(id).orElseThrow(()-> new NotFoundException("Category is not found..."));
        expenseCategoryRepository.delete(category);
    }

    public ExpenseCategoryResponse convertToDto(ExpenseCategory category){
        return new ExpenseCategoryResponse(category.getId(), category.getName());
    }

}
