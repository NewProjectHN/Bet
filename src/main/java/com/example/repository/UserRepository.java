package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
	 User findByEmail(String email);
	 
	 List<User> findByActiveOrderByAmountDesc(Integer active);
}
