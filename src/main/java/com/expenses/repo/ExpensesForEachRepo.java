package com.expenses.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expenses.entity.ExpensesForEachEntity;

public interface ExpensesForEachRepo extends JpaRepository<ExpensesForEachEntity, Integer> {

}
