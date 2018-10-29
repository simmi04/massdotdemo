package com.railboard.demo.model;

import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

public interface MassDotService {	
	public void getBoardInfo(ModelAndView mav, Map<String, String> filter);
}
