package com.railboard.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.railboard.demo.constants.StringConstants;
import com.railboard.demo.model.MassDotService;

@Controller
public class RailBoardController {

	@Autowired
	MassDotService mdService;
	
	@RequestMapping("/")
	String Home(Model model) {
		return "welcome";
	}
	
	@RequestMapping(value="/{station}", method = RequestMethod.GET)
	ModelAndView SchedulerNorthStation(@PathVariable String station) {
		ModelAndView mav = new ModelAndView("railboard");
		Map<String, String> filter = new HashMap<>();
		
		filter.put(StringConstants.STATION, station.toUpperCase());
		filter.put(StringConstants.IN_OUT_KEY, StringConstants.DEPARTURES);
		
		mdService.getBoardInfo(mav, filter);
		return mav;
	}
}
