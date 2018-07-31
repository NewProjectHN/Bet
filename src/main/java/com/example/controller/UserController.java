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
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MatchRepository matchRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BetRepository betRepository;
	
	@Autowired
	private ArrangeTotalRepository arrangeTotalRepository;
	
	@RequestMapping(value="/user/home", method = RequestMethod.GET)
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
		modelAndView.addObject("money", user.getAmount() == null ? 0 : user.getAmount());
		modelAndView.setViewName("user/index");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/user/getAllMatchAcitve", method = RequestMethod.GET)
	public @ResponseBody List<Match> getAllMatchAcitve(){
		return matchRepository.findByActive(true);
	}
	
	@RequestMapping(value="/user/getAllBetByMatch/{match_id}", method = RequestMethod.GET)
	public @ResponseBody List<Bet> getAllMatchAcitve(@PathVariable("match_id")Integer match_id){
		return betRepository.findByMatch_id(match_id);
	}
	
	private final static Integer RATE = 10000;
	@RequestMapping(value="/user/bet/{matchId}/{isHome}/{point}", method = RequestMethod.GET)
	public @ResponseBody Result getAllMatchAcitve(@PathVariable("matchId")Integer matchId,@PathVariable("isHome")boolean isHome,@PathVariable("point")Integer point){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		Match match = matchRepository.findById(matchId);
		if(match == null){
			return new Result("NOT_FOUND");
		}
		if(!match.getActive()){
			return new Result("NOT_ACTIVE");
		}
		if(match.getComplete()){
			return new Result("COMPLETED");
		}
		if(match.getExpireTime().compareTo(new Date()) < 0){
			return new Result("TIME_OUT");
		}
		if(point < 2 || point > 20){
			return new Result("OUT_RANGE");
		}
		if(user.getAmount() == null || user.getAmount() < point * RATE){
			return new Result("NOT_ENOUGH_MONEY");
		}
		
		// TODO: check da dat chua
		List<Bet> bList = betRepository.findByMatch_idAndUser_id(matchId, user.getId());
		if(!bList.isEmpty()){
			return new Result("ALREADY_BET");
		}
		Bet b = new Bet();
		b.setBetHome(isHome);
		b.setCreated(new Date());
		b.setMatch(match);
		b.setPoint(point);
		b.setUser(user);
		b.setPayment(false);
		b.setSummary(false);
		betRepository.save(b);
		user.setAmount(user.getAmount() - point * RATE);{
			userRepository.save(user);
		}
		return new Result("SUCCESS");
	}
	
	@RequestMapping(value="/user/getAllUserActive", method = RequestMethod.GET)
	public @ResponseBody List<User> getAllUserActive(){
		return userRepository.findByActiveOrderByAmountDesc(1);
	}
	
	@RequestMapping(value="/user/all-user", method = RequestMethod.GET)
	public ModelAndView allUser(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
		modelAndView.addObject("money", user.getAmount() == null ? 0 : user.getAmount());
		modelAndView.setViewName("user/all-user");
		return modelAndView;
	}
	
	@RequestMapping(value="/user/getArrange", method = RequestMethod.GET)
	public ModelAndView getArrange(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
		modelAndView.addObject("money", user.getAmount() == null ? 0 : user.getAmount());
		modelAndView.setViewName("user/arrange-user");
		return modelAndView;
	}
	
	@RequestMapping(value="/user/getAllRange", method = RequestMethod.GET)
	public @ResponseBody List<ArrangeTotal> getAllRange(){
		return arrangeTotalRepository.findByOrderByAmountDesc();
	}
}
