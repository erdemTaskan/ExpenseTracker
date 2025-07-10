package com.erdem.repository;

import com.erdem.model.Expense;
import com.erdem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IExpenseRepository extends JpaRepository<Expense,Long> {
    List<Expense> findByUser(User user);

    List<Expense> findByUserAndDateBetween(User user, LocalDateTime start, LocalDateTime end);
}
