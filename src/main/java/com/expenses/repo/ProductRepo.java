package com.expenses.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.expenses.entity.ProductEntity;

public interface ProductRepo extends JpaRepository<ProductEntity, Integer> {

	@Query("SELECT p FROM ProductEntity p WHERE p.buyerUserUuid =:buyerUserUuid AND p.buyDate <= :todayDate AND p.finalStatus = 0 ")
	List<ProductEntity> getAllUnpaidProductList(String buyerUserUuid, Date todayDate);

}
