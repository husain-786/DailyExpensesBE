package com.expenses.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.expenses.entity.UserEntity;

public interface UserRepo extends JpaRepository<UserEntity, Integer> {

	@Query("SELECT u FROM UserEntity u WHERE u.userUuid =:userUuid and u.finalStatus = 1")
	Optional<UserEntity> getUserDetail(String userUuid);

	@Query("SELECT u FROM UserEntity u WHERE u.email =:email and u.finalStatus = 1")
	Optional<UserEntity> getUserDtlByEmail(String email);

	@Query("SELECT u FROM UserEntity u WHERE u.mobile =:mobile and u.finalStatus = 1")
	Optional<UserEntity> getUserDtlByMobile(String mobile);

	@Query("SELECT u FROM UserEntity u WHERE u.finalStatus = 1")
	List<UserEntity> getAllActiveUsers();

	// fetching all users list other that buyer user.....
	@Query("SELECT u FROM UserEntity u WHERE u.userUuid !=:userUuid and u.finalStatus = 1")
	List<UserEntity> getAllOtherActiveUsers(String userUuid);

}
