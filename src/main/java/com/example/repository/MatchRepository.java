package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Match;

@Repository("matchRepository")
public interface MatchRepository extends JpaRepository<Match, Long> {
	List<Match> findByActive(Boolean active);
	Match findById(Integer id);
	
	List<Match> findBySummaryAndComplete(Boolean active,Boolean complete);
	
}
