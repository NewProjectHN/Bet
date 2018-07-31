package com.example.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "b_match")
public class Match {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "match_id")
	private int id;
	@Column(name = "home_team")
	private String homeTeam;
	@Column(name = "away_team")
	private String awayTeam;
	@Column(name = "rate_match")
	private float rate;
	@Column(name = "expire_time")
	private Date expireTime;
	@Column(name = "complete")
	private Boolean complete;// TRUE NEU TRAN nay da duoc thanh toan
	@Column(name = "active")
	private Boolean active;
	@Column(name = "created")
	private Date created;
	@Column(name = "home_score")
	private Integer homeScore;
	@Column(name = "away_score")
	private Integer awayScore;
	
	@Column(name = "summary", columnDefinition = "boolean default false", nullable = false)
	private Boolean summary; // Da duoc tong hop xep hang

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Boolean getComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	public Integer getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(Integer awayScore) {
		this.awayScore = awayScore;
	}

	public Boolean getSummary() {
		return summary;
	}

	public void setSummary(Boolean summary) {
		this.summary = summary;
	}
	
	

}
