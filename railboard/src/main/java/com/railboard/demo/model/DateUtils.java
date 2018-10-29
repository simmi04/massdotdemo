package com.railboard.demo.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {

	public static String covertToTime(String stringToParse) {
	    String currTime = null;
		Date date = null;
		if (stringToParse != null) {
		    ZonedDateTime zdt = ZonedDateTime.parse(stringToParse);
			date = Date.from(zdt.toInstant());
			currTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
		}
		
	    return currTime;
	}
	
	public static String getMinTimeFilter() {
		Date dt = new Date();
		int hour = Integer.parseInt(String.format("%1$tH", dt));
		
		if (hour < 3) {
			hour += 24;
		}
		
		return String.valueOf(hour) + ":" + String.format("%1$tM", dt);
	}
	
	public static String getDateFilter(int add) {
		Date dt = new Date();
		if (add > 0) {
			Calendar c = Calendar.getInstance(); 
			c.setTime(dt); 
			c.add(Calendar.DATE, add);
			dt = c.getTime();
		}
		
		return String.format("%1$tF", dt);
	}
}
