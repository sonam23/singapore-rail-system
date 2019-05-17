package com.rail.common;

import java.util.ArrayList;
import java.util.HashMap;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;
import com.rail.entity.graph.Station;

public class CommonTestUtils {

	public static RouteResponse buildSuccessRouteResponse() {
		RouteResponse response = new RouteResponse();
		response.setDistance(10);
		response.setRouteStations(new ArrayList<String>());
		return response;
	}
	
	public static RouteRequest buildRouteRequest(String source, String destination) {
		RouteRequest request = new RouteRequest();
		request.setSource(source);
		request.setDestination(destination);
		return request;
	}
	
	public static HashMap<String, Station> buildHashMap(ArrayList<String> list) {
		HashMap<String, Station> stationHashMap = new HashMap<String, Station>();
		list.forEach(item -> {
			Station station = new Station(null, "", "");
			stationHashMap.put(item, station);
		});
		return stationHashMap;
	}
}
