package com.rail.service.graph.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Util class for finding out PEAK, NON_PEAK and NIGHT HOURS.
 * Peak hours (6am-9am and 6pm-9pm on Mon-Fri)
 * Night hours (10pm-6am on Mon-Sun)
 * Non-Peak hours (all other times)
 * @author sagarwal
 */
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
	static ArrayList<TimeInterval> NIGHT_TIME_HOUR;
	
	/**
	 * Initializing the list of time intervals of PEAK, NON_PEAK, NIGHT hours as per the requirement
	 */
	static {
		PEAK_TIME_HOURS = new ArrayList<TimeInterval>();
		TimeInterval t1 = new TimeInterval(LocalTime.of(6,0,0), LocalTime.of(9,0,0));
		TimeInterval t2 = new TimeInterval(LocalTime.of(18,0,0), LocalTime.of(21,0,0));
		PEAK_TIME_HOURS.add(t1);
		PEAK_TIME_HOURS.add(t2);
		PEAK_TIME_DAYS = new ArrayList<DayOfWeek>();
		PEAK_TIME_DAYS.add(DayOfWeek.MONDAY);PEAK_TIME_DAYS.add(DayOfWeek.TUESDAY);PEAK_TIME_DAYS.add(DayOfWeek.WEDNESDAY);
		PEAK_TIME_DAYS.add(DayOfWeek.THURSDAY);PEAK_TIME_DAYS.add(DayOfWeek.FRIDAY);
		NIGHT_TIME_HOUR = new ArrayList<TimeInterval>();
		TimeInterval t3 = new TimeInterval(LocalTime.of(22,0,0), LocalTime.of(23,59,0));
		TimeInterval t4 = new TimeInterval(LocalTime.of(0,1,0), LocalTime.of(6,0,0));
		NIGHT_TIME_HOUR.add(t3);
		NIGHT_TIME_HOUR.add(t4);
	}
	
	/**
	 * Responsible for returning the TimeCategory based on the given date in the format - yyyy-MM-dd'T'HH:mm
	 * @param date
	 * @return {@link TimeCategory}
	 */
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
	
	/**
	 * Scope of improvement, 0.0.0 is a bit ambiguious in java.date
	 * Use other date libraries, we can avoid this manual return.
	 * @param dateTime
	 * @return
	 */
	private static boolean isNightTimeTravel(LocalDateTime dateTime) {
		LocalTime time = dateTime.toLocalTime();
		if(time == LocalTime.of(0,0,0)) {
			return true;
		}
		return compareTime(dateTime, NIGHT_TIME_HOUR);
	}

	/**
	 * Peak_time based on requirement
	 * @param dateTime
	 * @return
	 */
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
			Boolean isAfter = time.isAfter(timeTravel.start) ;
			Boolean isBefore = time.isBefore(timeTravel.stop) ;
			Boolean isTargetAfterStartAndBeforeStop = (isAfter && isBefore) ;
			if(isTargetAfterStartAndBeforeStop) {
				return true;
			}
		}
		return false;
	}
}
