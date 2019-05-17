package com.rail.service.graph.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class DateTimeUtil {
	
	@AllArgsConstructor
	public static class TimeInterval{
		@Getter @Setter private LocalTime start;
		@Getter @Setter private LocalTime stop;
	}
	
	public static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
	
	//Peak hours (6am-9am and 6pm-9pm on Mon-Fri)
	static ArrayList<TimeInterval> PEAK_TIME_HOURS;
	static ArrayList<DayOfWeek> PEAK_TIME_DAYS; 
	
	//Night hours (10pm-6am on Mon-Sun)
	static int[] NIGHT_TIME_HOURS = {22, 23, 00, 1, 2, 3, 4, 5, 6};
	
	static {
		PEAK_TIME_HOURS = new ArrayList<TimeInterval>();
		TimeInterval t1 = new TimeInterval(LocalTime.of(6,0,0), LocalTime.of(9,0,0));
		TimeInterval t2 = new TimeInterval(LocalTime.of(18,0,0), LocalTime.of(21,0,0));
		PEAK_TIME_HOURS.add(t1);
		PEAK_TIME_HOURS.add(t2);
		PEAK_TIME_DAYS = new ArrayList<DayOfWeek>();
		PEAK_TIME_DAYS.add(DayOfWeek.MONDAY);PEAK_TIME_DAYS.add(DayOfWeek.TUESDAY);PEAK_TIME_DAYS.add(DayOfWeek.WEDNESDAY);
		PEAK_TIME_DAYS.add(DayOfWeek.THURSDAY);PEAK_TIME_DAYS.add(DayOfWeek.FRIDAY);
	}
	
	public static TimeCategory getTimeCategoryForTravel(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDateTime parsedDate = LocalDateTime.parse(date, formatter);
		TimeCategory timeCategory = null;
		
		
		boolean isPeakTimeTravel = isPeakTimeTravel(parsedDate);
		if(!isPeakTimeTravel) {
			boolean isNightTimeTravel = isNightTimeTravel(parsedDate);
			if(!isNightTimeTravel) {
				timeCategory = TimeCategory.NON_PEAK_HOURS;
			}else {
				timeCategory = TimeCategory.NIGHT_HOURS;
			}
		}else {
			timeCategory = TimeCategory.PEAK_HOURS;
		}
		
		System.out.println(String.format("Date=%s DayOfWeek=%s Time=%s Date=%s TimeCategory=%s", parsedDate, parsedDate.getDayOfWeek(), parsedDate.toLocalTime(), parsedDate.toLocalDate(), timeCategory));
		return timeCategory;
	}
	
	private static boolean isNightTimeTravel(LocalDateTime dateTime) {
		for(int i=0; i<NIGHT_TIME_HOURS.length; i++) {
			LocalTime readTime = LocalTime.of(NIGHT_TIME_HOURS[i], 0,0);
			if(dateTime.toLocalTime().equals(readTime)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isPeakTimeTravel(LocalDateTime dateTime) {
		boolean isPeakDayTravel = false;
		for(DayOfWeek day: PEAK_TIME_DAYS) {
			if(day.equals(dateTime.getDayOfWeek())){
				isPeakDayTravel = true;
				break;
			}
		}
		if(isPeakDayTravel) {
			return compareTime(dateTime, PEAK_TIME_HOURS);
		}
		return false;
	}

	private static boolean compareTime(LocalDateTime dateTime, ArrayList<TimeInterval> timeHours) {
		for(TimeInterval timeTravel: timeHours) {
			LocalTime time = dateTime.toLocalTime();
			Boolean isTargetAfterStartAndBeforeStop = ( time.isAfter( timeTravel.start ) && time.isBefore( timeTravel.stop ) ) ;
			if(isTargetAfterStartAndBeforeStop) {
				return true;
			}
		}
		return false;
	}
}
