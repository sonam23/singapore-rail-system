package com.rail.service.graph.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Component;

import com.rail.entity.RouteResponse;
import com.rail.entity.graph.Station;
import com.rail.entity.graph.StationEdge;

@Component
public class GraphUtil {
	
	/**
	 * This will create a unique set of Stations after reading the file.
	 * The junction stations will have more than one station codes associated with it.				
	 * @param stationList
	 * @return
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
	
	private Station constructStation(String code, String name, String date) {
		ArrayList<String> codes = new ArrayList<>();
		codes.add(code);
		return new Station(codes, name, date);
	}
	
	private Station constructStation(String code, String name, HashMap<String, Station> stationHashMap) {
		Station station = stationHashMap.get(name);
		ArrayList<String> codeExisting = station.getCode();
		codeExisting.add(code);
		station.setCode(codeExisting);
		return station;
	}
	
	public RouteResponse constructResponse(GraphPath<Station, StationEdge> path) {
		RouteResponse response = new RouteResponse();
		
		List<Station> stationList = path.getVertexList();
		ArrayList<String> routeStations = new ArrayList<>();
		
		Station prevStation = stationList.get(0);
		String currentStationCode = getCurrentStatusCode(prevStation, "");
		String currentStationLine = getCurrentLineFromCode(currentStationCode);
		routeStations.add(currentStationCode);
		for(int i=1; i<stationList.size();i++) {
			Station currentStation = stationList.get(i);
			currentStationCode = getCurrentStatusCode(currentStation, currentStationLine);
			//This can happen if the previous station we needed to change the line, find out the common line between the previous and new station
			if(StringUtils.isBlank(currentStationCode)) {
				ArrayList<String> stationListForJunction = getStationListForJunction(currentStation, prevStation);
				stationListForJunction.forEach(station -> {
					routeStations.add(station);
				});
				String newStationLine =  getCurrentLineFromCode(stationListForJunction.get(0));
				response.addLineChange(currentStationLine, newStationLine);
				currentStationLine = newStationLine;		
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
		return response;
		
	}

	private String getCurrentStatusCode(Station currentStation, String currentStationLine) {
		//If currentStationLine is empty, this is the first station. It can also happen that the first station itself is a junction. For simplicity we are picking up the first statuoon
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
	
	private ArrayList<String> getStationListForJunction(Station currentStation, Station prevStation) {
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
}
