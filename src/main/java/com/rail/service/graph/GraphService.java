package com.rail.service.graph;

import java.util.ArrayList;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;

public interface GraphService {
	
	/**
	 * Initiliazes the graph based on the stationList input
	 * @param stationList
	 * @return
	 */
	boolean initializeNetwork(ArrayList<String[]> stationList);
	
	/**
	 * Finds the shortest route between the two stations provided based on any algorithm.
	 * @param routeRequest
	 * @return
	 */
	public RouteResponse findRoute(RouteRequest routeRequest);

}
