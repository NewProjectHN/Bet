package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.ArrangeTotal;

@Repository("arrangeTotalRepository")
public interface ArrangeTotalRepository extends JpaRepository<ArrangeTotal, Long> {
	 List<ArrangeTotal> findByOrderByAmountDesc();
}
