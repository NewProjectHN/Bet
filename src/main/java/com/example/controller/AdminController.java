package com.example.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.bean.Result;
import com.example.model.ArrangeTotal;
import com.example.model.Bet;
import com.example.model.Match;
import com.example.model.User;
import com.example.repository.ArrangeTotalRepository;
import com.example.repository.BetRepository;
import com.example.repository.MatchRepository;
import com.example.repository.UserRepository;
import com.example.service.UserService;

@Controller
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private BetRepository betRepository;
	
	@Autowired
	private ArrangeTotalRepository arrangeTotalRepository;

	@RequestMapping(value = "/admin/home", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.setViewName("admin/index-home");
		return modelAndView;
	}

	@RequestMapping(value = "/admin/changeBet/{betId}/{homePoint}/{awayPoint}", method = RequestMethod.GET)
	public @ResponseBody Result changeBet(@PathVariable("betId") Integer betId,
			@PathVariable("homePoint") Integer homePoint, @PathVariable("awayPoint") Integer awayPoint) {
		Bet bet = betRepository.findById(betId);
		User user = bet.getUser();

		if (homePoint < 0 || awayPoint < 0) {
			return new Result("LESS_0");
		} else if (homePoint > 0 && awayPoint > 0) {
			return new Result("BOTH_BET");
		} else if (homePoint > 0) {
			if (!bet.getBetHome()) {
				bet.setBetHome(true);
			}
			if (bet.getPoint() != homePoint) {
				if (homePoint < bet.getPoint()) {
					user.setAmount(user.getAmount() + (bet.getPoint() - homePoint) * 10000);
				} else {
					Integer amount = (homePoint - bet.getPoint()) * 10000;
					if (user.getAmount() >= amount) {
						user.setAmount(user.getAmount() - amount);
					} else {
						return new Result("NOT_ENOUGH_MONEY");
					}
				}
			}
			bet.setPoint(homePoint);
			userRepository.save(user);
		} else if (awayPoint > 0) {
			if (bet.getBetHome()) {
				bet.setBetHome(false);
			}
			if (bet.getPoint() != awayPoint) {
				if (awayPoint < bet.getPoint()) {
					user.setAmount(user.getAmount() + (bet.getPoint() - awayPoint) * 10000);
				} else {
					Integer amount = (awayPoint - bet.getPoint()) * 10000;
					if (user.getAmount() >= amount) {
						user.setAmount(user.getAmount() - amount);
					} else {
						return new Result("NOT_ENOUGH_MONEY");
					}
				}
			}
			bet.setPoint(awayPoint);
			userRepository.save(user);
			
		} else {
			user.setAmount(user.getAmount() + bet.getPoint() * 10000);
			betRepository.delete(bet);
			userRepository.save(user);
		}

		return new Result("SUCCESS");
	}

	@RequestMapping(value = "/admin/resetForMatch/{matchId}", method = RequestMethod.GET)
	public @ResponseBody Result resetForMatch(@PathVariable("matchId") Integer matchId) {
		Match match = matchRepository.findById(matchId);
		if(match.getComplete()){
			return new Result("COMPLETED");
		}
		if(!match.getActive()){
			return new Result("DEACTIVE");
		}
		
		List<Bet> betList = betRepository.findByMatch_id(matchId);
		for(Bet bet: betList){
			Result rs = changeBet(bet.getId(), 0, 0);
			if(!rs.getCode().equals("SUCCESS")){
				return rs;
			}
		}
		return new Result("SUCCESS");
	}
	
	@RequestMapping(value = "/admin/paymentForMatch/{matchId}", method = RequestMethod.GET)
	public @ResponseBody Result paymentForMatch(@PathVariable("matchId") Integer matchId) {
		Match match = matchRepository.findById(matchId);
		if (match.getAwayScore() == null || match.getHomeScore() == null) {
			return new Result("NOT_INPUT_SCORE");
		}
		if (match.getComplete()) {
			return new Result("MATCH_COMPLETED");
		}
		if(match.getExpireTime().compareTo(new Date()) > 0){
			return new Result("MATCH_NOT_FINISH");
		}

		Integer homeScore = match.getHomeScore();
		Integer awayScore = match.getAwayScore();
		float rate = match.getRate();
		float rateResult = 10;
		Boolean isHomeWin = true;
		float diff = homeScore - (awayScore + rate);

		System.out.println("diff:" + diff);
		if (diff == 0f) {
			isHomeWin = null;
			rateResult = 0;

		} else if (diff > 0) {
			isHomeWin = true;
			if (diff > 0.25) {
				rateResult = 1;
			} else {
				rateResult = 0.5f;
			}
		} else {
			isHomeWin = false;
			if (diff < -0.25) {
				rateResult = 1;
			} else {
				rateResult = 0.5f;
			}
		}
		System.out.println("isHomeWin:" + isHomeWin);
		System.out.println("rateResult:" + rateResult);

		List<Bet> betList = betRepository.findByMatch_id(matchId);
		for (Bet bet : betList) {
			User user = bet.getUser();
			Float amount = (bet.getPoint() * 10000 * (rateResult + 1));
			Integer amountRs = amount.intValue();
			if (isHomeWin == null) {
				user.setAmount(user.getAmount() + amountRs);
			} else if (isHomeWin) {
				if (bet.getBetHome()) {
					user.setAmount(user.getAmount() + amountRs);
				}else{
					if(rateResult == 0.5){
						Float tmp = user.getAmount() + bet.getPoint() * 10000 * rateResult;
						user.setAmount(tmp.intValue());
					}
				}
			} else {
				if (!bet.getBetHome()) {
					user.setAmount(user.getAmount() + amountRs);
				}else{
					if(rateResult == 0.5){
						Float tmp = user.getAmount() + bet.getPoint() * 10000 * rateResult;
						user.setAmount(tmp.intValue());
					}
				}
			}
			bet.setPayment(true);
			betRepository.save(bet);
			userRepository.save(user);
		}
		match.setComplete(true);
		matchRepository.save(match);
		return new Result("SUCCESS");
	}
	
	@RequestMapping(value = "/admin/updateRange", method = RequestMethod.GET)
	public @ResponseBody Result updateRange() {
		List<Match> matchList = matchRepository.findBySummaryAndComplete(false,true);
		
		List<ArrangeTotal> arrangeTotalList = arrangeTotalRepository.findAll();
		
		for(Match match:matchList){
			
			Integer homeScore = match.getHomeScore();
			Integer awayScore = match.getAwayScore();
			float rate = match.getRate();
			float rateResult = 10;
			Boolean isHomeWin = true;
			float diff = homeScore - (awayScore + rate);

			System.out.println("diff:" + diff);
			if (diff == 0f) {
				isHomeWin = null;
				rateResult = 0;

			} else if (diff > 0) {
				isHomeWin = true;
				if (diff > 0.25) {
					rateResult = 1;
				} else {
					rateResult = 0.5f;
				}
			} else {
				isHomeWin = false;
				if (diff < -0.25) {
					rateResult = 1;
				} else {
					rateResult = 0.5f;
				}
			}
			System.out.println("isHomeWin:" + isHomeWin);
			System.out.println("rateResult:" + rateResult);
			
			List<Bet> betList = betRepository.findByMatch_id(match.getId());
			for (Bet bet : betList) {
				if(!bet.getSummary()){
				User user = bet.getUser();
				ArrangeTotal arrangeTotalCurrent = null;
					for (ArrangeTotal arrangeTotal : arrangeTotalList) {
						if (arrangeTotal.getUser().getId() == user.getId()) {
							arrangeTotalCurrent = arrangeTotal;
							break;
						}
					}
					if (arrangeTotalCurrent == null) {
						arrangeTotalCurrent = new ArrangeTotal();
						arrangeTotalCurrent.setUser(user);
						arrangeTotalList.add(arrangeTotalCurrent);
					}
					Float amount = (bet.getPoint() * 10000 * (rateResult));
					Integer amountRs = amount.intValue();
					Integer rsAmount = null;
					int win =0;
					int balance = 0;
					int lose = 0;
					if (isHomeWin == null) {
						rsAmount = 0;
						balance = 1;
					} else if (isHomeWin) {
						if (bet.getBetHome()) {
							rsAmount = amountRs;
							win = 1;
						} else {
							rsAmount = -amountRs;
							lose = 1;
						}
					} else {
						if (bet.getBetHome()) {
							rsAmount = -amountRs;
							lose = 1;
						} else {
							rsAmount = amountRs;
							win = 1;
						}
					}
					if (arrangeTotalCurrent.getAmount() == null) {
						arrangeTotalCurrent.setAmount(rsAmount);
					} else {
						arrangeTotalCurrent.setAmount(rsAmount + arrangeTotalCurrent.getAmount());
					}
					if (arrangeTotalCurrent.getLose() == null) {
						arrangeTotalCurrent.setLose(lose);
					} else {
						arrangeTotalCurrent.setLose(lose + arrangeTotalCurrent.getLose());
					}
					if (arrangeTotalCurrent.getWin() == null) {
						arrangeTotalCurrent.setWin(win);
					} else {
						arrangeTotalCurrent.setWin(win + arrangeTotalCurrent.getWin());
					}
					if (arrangeTotalCurrent.getBalance() == null) {
						arrangeTotalCurrent.setBalance(balance);
					} else {
						arrangeTotalCurrent.setBalance(balance + arrangeTotalCurrent.getBalance());
					}
					bet.setSummary(true);
					betRepository.save(bet);
				}
			}
			match.setSummary(true);
			matchRepository.save(match);
		}
		
		arrangeTotalRepository.save(arrangeTotalList);
		return new Result("SUCCESS");
	}

	public static void main(String[] args) {
		Integer homeScore = 1;
		Integer awayScore = 1;
		float rate = 0f;
		float rateResult = 1;
		Boolean isHomeWin = true;
		float diff = homeScore - (awayScore + rate);

		System.out.println("diff:" + diff);
		if (diff == 0f) {
			isHomeWin = null;
			rateResult = 1;

		} else if (diff > 0) {
			isHomeWin = true;
			if (diff > 0.25) {
				rateResult = 1;
			} else {
				rateResult = 0.5f;
			}
		} else {
			isHomeWin = false;
			if (diff < -0.25) {
				rateResult = 1;
			} else {
				rateResult = 0.5f;
			}
		}
		System.out.println("isHomeWin:" + isHomeWin);
		System.out.println("rateResult:" + rateResult);
	}

}
