package com.example.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bet")
public class Bet {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "bet_id")
	private int id;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "match_id")
	private Match match;
	@Column(name = "created")
	private Date created;
	@Column(name = "point")
	private Integer point;
	@Column(name = "bet_home")
	private Boolean betHome;
	@Column(name = "payment", columnDefinition = "boolean default false", nullable = false)
	private Boolean payment; // Da duoc thanh toan hay chua
	
	@Column(name = "summary", columnDefinition = "boolean default false", nullable = false)
	private Boolean summary; // Da duoc tong hop xep hang

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public Boolean getBetHome() {
		return betHome;
	}

	public void setBetHome(Boolean betHome) {
		this.betHome = betHome;
	}

	public Boolean getPayment() {
		return payment;
	}

	public void setPayment(Boolean payment) {
		this.payment = payment;
	}

	public Boolean getSummary() {
		return summary;
	}

	public void setSummary(Boolean summary) {
		this.summary = summary;
	}
	
	

}
