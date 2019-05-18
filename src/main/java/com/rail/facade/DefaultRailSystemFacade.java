package com.rail.facade;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;
import com.rail.service.graph.GraphService;
import com.rail.service.graph.util.DateTimeUtil;

import lombok.Setter;

/**
 * Facade implementation to setup the rail network and find route between stations
 * Currently it uses graph library JGRAPHT, we can always create our own custom implementation of GraphService 
 * Or use any other graph library such as Google Guava, Jung etc
 * @author sagarwal
 */
@Primary
@Component("defaulyRailSystemFacade")
public class DefaultRailSystemFacade implements RailSystemFacade{
	
	@Autowired
	@Setter
	RailSystemFacadeUtil facadeUtil;
	
	@Autowired
	@Setter
	GraphService graphService;

	/**
	 * Intialize and setup the network of stations
	 * @param filePath
	 * @return boolean - based on success or failure to initialize the network
	 */
	public boolean setup(String filePath) {
		try {
			ArrayList<String[]> stationList = facadeUtil.readFile(filePath);
			if(stationList != null) {
				return graphService.initializeNetwork(stationList);
			}else {
				return false;
			}
		}catch(Exception e) {
			return false;
		}
	}

	/**
	 * Validate the input for mandatory fields and finds the shortest path via graph service 
	 * @param routeRequest
	 * @return routeResponse
	 */
	public RouteResponse findRoute(RouteRequest routeRequest) {
		
		validateInput(routeRequest);
		return graphService.findRoute(routeRequest);
	}

	/**
	 * Validates that the network is initialized
	 * Validates that the source and destination is provided in the request
	 * @param routeRequest
	 */
	private void validateInput(RouteRequest routeRequest) {
		String source = routeRequest.getSource().trim();
		String destination = routeRequest.getDestination().trim();
		if(StringUtils.isBlank(source) || StringUtils.isBlank(destination)) {
			 throw new IllegalArgumentException("Either source or destination is Blank");
		}
		if(StringUtils.equals(source, destination)) {
			throw new IllegalArgumentException("Source and destination cannot be same");
		}
		if(StringUtils.isNoneBlank(routeRequest.getTime())) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeUtil.DATE_FORMAT);
			try {
				LocalDateTime.parse(routeRequest.getTime(), formatter);
			}catch(Exception e) {
				throw new IllegalArgumentException("Unable to parse time "+routeRequest.getTime()+" required in format="+DateTimeUtil.DATE_FORMAT);
			}
		}
	}

	/**
	 * Work in progress - for future time based search
	 */
	@Override
	public RouteResponse findRouteTime(RouteRequest routeRequest) {
		validateInput(routeRequest);
		return graphService.findRouteTime(routeRequest);
	}

}
