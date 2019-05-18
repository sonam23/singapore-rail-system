package com.rail.service.graph.util;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class DateTimeUtilTest {
	
	@Test
	public void testTime() {
		//Non peak hour date
		String date = "2019-01-31T16:00";
		
		assertEquals("Expected NON_PEAK_HOURS", TimeCategory.NON_PEAK_HOURS, DateTimeUtil.getTimeCategoryForTravel(date));
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeUtil.DATE_FORMAT);
		LocalDateTime parsedDate = LocalDateTime.parse(date, formatter);
		
		//Making it PeakHours
		LocalDateTime newDate = parsedDate.plusHours(4);
		assertEquals("Expected PEAK_HOURS", TimeCategory.PEAK_HOURS, DateTimeUtil.getTimeCategoryForTravel(newDate.toString()));
		
		//Making it NightHours
		LocalDateTime newDate2 = newDate.plusHours(4);
		assertEquals("Expected NIGHT_HOURS", TimeCategory.NIGHT_HOURS, DateTimeUtil.getTimeCategoryForTravel(newDate2.toString()));
		
		//Making it peakHoursAgain
		LocalDateTime newDate3 = newDate2.plusHours(7);
		assertEquals("Expected PEAK_HOURS", TimeCategory.PEAK_HOURS, DateTimeUtil.getTimeCategoryForTravel(newDate3.toString()));
		
		//Making it nonPeak hours by changing the DAY
		LocalDateTime newDate4 = newDate3.plusDays(1);
		assertEquals("Expected NON_PEAK_HOURS", TimeCategory.NON_PEAK_HOURS, DateTimeUtil.getTimeCategoryForTravel(newDate4.toString()));
		
		//Making it NightHours
		LocalDateTime newDate5 = newDate4.plusMinutes(930);
		assertEquals("Expected NIGHT_HOURS", TimeCategory.NIGHT_HOURS, DateTimeUtil.getTimeCategoryForTravel(newDate5.toString()));


	}

}
