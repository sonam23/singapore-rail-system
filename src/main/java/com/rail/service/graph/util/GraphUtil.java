package com.rail.service.graph.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rail.entity.RouteResponse;
import com.rail.entity.graph.Station;
import com.rail.entity.graph.StationEdge;
import com.rail.service.graph.StationGraph.LineToStationEdge;
import com.rail.service.graph.util.TravelTimeConfig.NightHour;
import com.rail.service.graph.util.TravelTimeConfig.NonPeakHour;
import com.rail.service.graph.util.TravelTimeConfig.PeakHour;

/**
 * Util class to construct the response of the graph service 	@return {@link TimeCategory}
 * @author sagarwal
 */
@Component
public class GraphUtil {
	
	@Autowired TravelTimeConfig travelConfig;
	
	/**
	 * This will create a unique set of Stations after reading the file.
	 * The junction stations will have more than one station codes associated with it.				
	 * @param stationList
	 * @return HashMap<String, Station>
	 */
	public HashMap<String, Station> contstructStationHashMap(ArrayList<String[]> stationList) {
		HashMap<String, Station> stationHashMap = new HashMap<String,Station>();
		stationList.forEach(station -> {
			String code = station[0];
			String name = station[1];
			String date = station[2];
			
			Station s;
			if(stationHashMap.containsKey(name)) {
				s = constructStation(code, name, stationHashMap);
			}else {
				s = constructStation(code, name, date);
			}
			stationHashMap.put(name, s);
		});
		return stationHashMap;
	}
	
	/**
	 * Constructs a new station
	 * @param code
	 * @param name
	 * @param date
	 * @return Station
	 */
	private Station constructStation(String code, String name, String date) {
		ArrayList<String> codes = new ArrayList<>();
		codes.add(code);
		return new Station(codes, name, date);
	}
	
	/**
	 * Updates the station code, if station is already present in the hashmap
	 * @param code
	 * @param name
	 * @param stationHashMap
	 * @return Station
	 */
	private Station constructStation(String code, String name, HashMap<String, Station> stationHashMap) {
		Station station = stationHashMap.get(name);
		ArrayList<String> codeExisting = station.getCode();
		codeExisting.add(code);
		station.setCode(codeExisting);
		return station;
	}
	
	/**
	 * This is the main method, which constructs the response given the result of the shortest path stations. It returns
	 *   1. Unique station codes in the route
	 *   2. List of the stations
	 *   3. Description of the routes, which also provides changing line details
	 * @param path
	 * @param timeCategory 
	 * @param time 
	 * @return RouteResponse
	 */
	public RouteResponse constructResponse(GraphPath<Station, StationEdge> path) {
		RouteResponse response = new RouteResponse();
		
		List<Station> stationList = path.getVertexList();
		ArrayList<String> routeStations = new ArrayList<>();
		int numberOfStationChanged = 0;
		
		Station prevStation = stationList.get(0);
		String currentStationCode = getCurrentStatusCode(prevStation, "");
		String currentStationLine = getCurrentLineFromCode(currentStationCode);
		routeStations.add(currentStationCode);
		for(int i=1; i<stationList.size();i++) {
			Station currentStation = stationList.get(i);
			currentStationCode = getCurrentStatusCode(currentStation, currentStationLine);
			//This can happen if the previous station we needed to change the line, find out the common line between the previous and new station
			if(StringUtils.isBlank(currentStationCode)) {
				ArrayList<String> stationListForJunction = getStationCodeListForJunction(currentStation, prevStation);
				stationListForJunction.forEach(station -> {
					routeStations.add(station);
				});
				String newStationLine =  getCurrentLineFromCode(stationListForJunction.get(0));
				response.addLineChange(currentStationLine, newStationLine);
				currentStationLine = newStationLine;	
				numberOfStationChanged++;
			}else {
				routeStations.add(currentStationCode);
				currentStationLine = getCurrentLineFromCode(currentStationCode);
				
			}
			response.addStationsLink(prevStation.getName(), currentStation.getName(), currentStationLine);
			prevStation = currentStation;
		}
			
		response.setDistance(stationList.size());
		response.setStationList(stationList);
		response.setRouteStations(routeStations);
		response.setNumberOfStationChanged(numberOfStationChanged);
		return response;
		
	}

	/**
	 * Given the station, it gets the station code on the line.
	 * This is particularly useful when the station is a junction and has more that one station codes.
	 * Case: that the station does not have any code in the line, it returns empty string then.
	 * @param currentStation
	 * @param currentStationLine
	 * @return
	 */
	private String getCurrentStatusCode(Station currentStation, String currentStationLine) {
		//If currentStationLine is empty, this is the first station. It can also happen that the first station itself is a junction. For simplicity we are picking up the first statuoon
		//Scope for improvement
		if(StringUtils.isEmpty(currentStationLine)) {
			return currentStation.getCode().get(0);
		}
		for(String code: currentStation.getCode()) {
			String codeLine = getCurrentLineFromCode(code);
			if(codeLine.equals(currentStationLine)) {
				return code;
			}
		}
		return "";
	}
	
	/**
	 * Given two stations, it returns the station code list for the junction. For example
	 * Station-Botanic Gardens(CC19,DT9), Station-Stevens(DT10, TE11)
	 * Response - (DT9, DT10)
	 * @param currentStation
	 * @param prevStation
	 * @return
	 */
	private ArrayList<String> getStationCodeListForJunction(Station currentStation, Station prevStation) {
		ArrayList<String> junctionStationList = new ArrayList<String>();
		for(String code: currentStation.getCode()) {
			String codeLine = getCurrentLineFromCode(code);
			for(String prevCode: prevStation.getCode()) {
				String prevCodeLine = getCurrentLineFromCode(prevCode);
				if(StringUtils.equals(codeLine, prevCodeLine)) {
					junctionStationList.add(prevCode);
					junctionStationList.add(code);
					return junctionStationList;
				}
			}
		}
		return null;
	}
	
	/**
	 * This splits the code into alphabets and numbers
	 * Assumption: Code will always contain a LINE and STATION NUMBER
	 * @param code
	 * @return
	 */
	public String getCurrentLineFromCode(String code) {
		String[] part = code.split("(?<=\\D)(?=\\d)");
		return part[0];
	}
	
	/**
	 * Peak hours (6am-9am and 6pm-9pm on Mon-Fri)
		NS and NE lines take 12 minutes per station
		All other train lines take 10 minutes
		Every train line change adds 15 minutes of waiting time to the journey
	 * @param stationEdgeHashMap
	 * @return
	 */
	public  Map<StationEdge, Double> getPeakHourTravelMap(ArrayList<LineToStationEdge> lineToStationEdgeList) {
		PeakHour peakHour = travelConfig.getPeakhour();
		Map<String, Double> others = peakHour.getOthers();
		Map<StationEdge, Double> hashMap = new HashMap<StationEdge, Double>();
		lineToStationEdgeList.forEach(lineEdge ->{	
			if(others.containsKey(lineEdge.getLine())){
				hashMap.put(lineEdge.getEdge(), others.get(lineEdge.getLine()));
			}else {
				hashMap.put(lineEdge.getEdge(), peakHour.getDefaultTime());
			}
		});
		return hashMap;
	}

	/**
	 * Non-Peak hours (all other times)
		DT and TE lines take 8 minutes per stop
		All trains take 10 minutes per stop
		Every train line change adds 10 minutes of waiting time to the journey
	 * @param stationEdgeHashMap
	 * @return
	 */
	public  Map<StationEdge, Double> getNonPeakHourTravelMap(ArrayList<LineToStationEdge> lineToStationEdgeList) {
		NonPeakHour nonpeakHour = travelConfig.getNonpeakhour();
		Map<String, Double> others = nonpeakHour.getOthers();
		Map<StationEdge, Double> hashMap = new HashMap<StationEdge, Double>();
		lineToStationEdgeList.forEach(lineEdge ->{
			if(others.containsKey(lineEdge.getLine())){
				hashMap.put(lineEdge.getEdge(), others.get(lineEdge.getLine()));
			}else {
				hashMap.put(lineEdge.getEdge(), nonpeakHour.getDefaultTime());
			}
		});
		return hashMap;
	}
	
	/**
	 * Night hours (10pm-6am on Mon-Sun)
		DT, CG and CE lines do not operate
		TE line takes 8 minutes per stop
		All trains take 10 minutes per stop
		Every train line change adds 10 minutes of waiting time to the journey
	 * @param stationEdgeHashMap
	 * @return
	 */
	public  Map<StationEdge, Double> getNightHourTravelMap(ArrayList<LineToStationEdge> lineToStationEdgeList) {
		NightHour nightHour = travelConfig.getNighthour();
		Map<String, Double> others = nightHour.getOthers();
		Map<StationEdge, Double> hashMap = new HashMap<StationEdge, Double>();
		lineToStationEdgeList.forEach(lineEdge ->{
			//The lines DT, CG and CE  are removed directly from graph
			if(others.containsKey(lineEdge.getLine())){
				hashMap.put(lineEdge.getEdge(), others.get(lineEdge.getLine()));
			}else {
				hashMap.put(lineEdge.getEdge(), nightHour.getDefaultTime());
			}
		});
		return hashMap;
	}
	
	/**
	 * Scope of improvement to add the station changing time while calculating the shortest path
	 * @param path
	 * @param time
	 * @param timeCategory
	 * @return
	 */
	public RouteResponse constructResponse(GraphPath<Station, StationEdge> path, Double time, TimeCategory timeCategory) {
		RouteResponse response = constructResponse(path);
		int numberOfStationChanged = response.getNumberOfStationChanged();
		if(TimeCategory.PEAK_HOURS.equals(timeCategory)) {
			response.setTime(time + 15 * numberOfStationChanged);
		}else {
			response.setTime(time + 10 * numberOfStationChanged);
		}
		response.setTimeCategory(timeCategory);
		return response;
	}

	/**
	 * For night hours we remove the lines that are not operational
	 * @param weightedGraph
	 * @param lineToStationEdgeList
	 * @return
	 */
	public Graph<Station, StationEdge> removeEdgesNotOperational(Graph<Station, StationEdge> weightedGraph, ArrayList<LineToStationEdge> lineToStationEdgeList) {
		NightHour nightHour = travelConfig.getNighthour();
		List<String> closedLines = nightHour.getClosed();
		lineToStationEdgeList.forEach(lineEdge ->{
			if(closedLines.contains(lineEdge.getLine())){
				weightedGraph.removeEdge(lineEdge.getEdge());
			}
		});
		return weightedGraph;
	}
}
