package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Bet;

@Repository("betRepository")
public interface BetRepository extends JpaRepository<Bet, Long> {
	List<Bet> findByMatch_id(Integer matchId);
	
	List<Bet> findByMatch_idAndUser_id(Integer matchId,Integer userId);
	
	Bet findById(Integer id);
}
