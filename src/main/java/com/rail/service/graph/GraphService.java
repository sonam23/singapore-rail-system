package com.rail.service.graph;

import java.util.ArrayList;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;

public interface GraphService {
	
	boolean initializeNetwork(ArrayList<String[]> stationList);
	
	public RouteResponse findRoute(RouteRequest routeRequest);

}
