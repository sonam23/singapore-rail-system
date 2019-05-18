package com.rail.service.graph;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;
import com.rail.entity.graph.Station;
import com.rail.service.graph.util.GraphUtil;

import lombok.Setter;

/**
 * This is the default implementation of the GraphService Based on JGraohT
 * @author sagarwal3ss
 */
@Component
public class DefaultGraphService implements GraphService{
	
	@Autowired @Setter
	protected GraphUtil graphUtil;
	
	@Autowired @Setter
	protected StationGraph stationGraph;

	@Override
	public boolean initializeNetwork(ArrayList<String[]> stationList) {
		HashMap<String, Station> stationHashMap = graphUtil.contstructStationHashMap(stationList);
		stationGraph.init(stationHashMap, stationList);
		stationGraph.constructGraph();
		return true;
	}

	@Override
	public RouteResponse findRoute(RouteRequest routeRequest) {
		validateInputStation(routeRequest);
		RouteResponse response = stationGraph.findShortestPathTime(routeRequest);
		return response;
	}

	/**
	 * Validates that the graph is already initialized when the call is made
	 * Validates that the station names provided are corrct and exists in the network.
	 * @param routeRequest
	 */
	private void validateInputStation(RouteRequest routeRequest) {
		String source = routeRequest.getSource();
		String destination = routeRequest.getDestination();
		HashMap<String, Station> stationHashMap = stationGraph.getStationHashMap();
		if(stationHashMap == null || stationHashMap.size() == 0) {
			throw new InternalError("Please initialize the network first!");
		}
		Station startVertex = stationHashMap.get(source);
		Station endVertex = stationHashMap.get(destination);		
		if(startVertex == null) {
			 throw new IllegalArgumentException("No such station = "+source);
		}
		if(endVertex == null) {
			throw new IllegalArgumentException("No such station = "+destination);
		}
	}

	@Override
	public RouteResponse findRouteTime(RouteRequest routeRequest) {
		validateInputStation(routeRequest);
		RouteResponse response = stationGraph.findShortestPathTime(routeRequest);
		return response;
	}

}
