package com.erdem.repository;

import com.erdem.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IExpenseCategoryRepository extends JpaRepository<ExpenseCategory,Long> {

    Optional<ExpenseCategory> findByName(String name);
}
