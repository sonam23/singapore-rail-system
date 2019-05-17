package com.rail.facade;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;

public interface RailSystemFacade {
	
	/**
	 * Required for the one time setup of the network, which is singleton in nature
	 * @param fileName
	 * @return
	 */
	public boolean setup(String fileName);
	
	/**
	 * Finding the shortest path between two given stations
	 * @param routeRequest
	 * @return RouteResponse
	 */
	public RouteResponse findRoute(RouteRequest routeRequest);
	
	/**
	 * Finding the shortest path between two given stations, considering the overall time
	 * @param routeRequest
	 * @return
	 */
	public RouteResponse findRouteTime(RouteRequest routeRequest);
}
