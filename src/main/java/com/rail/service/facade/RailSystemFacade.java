package com.rail.service.facade;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;

public interface RailSystemFacade {
	
	/**
	 * Required for the one time setup of the network, which is singleton in nature
	 * @param fileName
	 * @return
	 */
	public boolean setup(String fileName);
	
	public RouteResponse findRoute(RouteRequest routeRequest);
}
