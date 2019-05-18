package com.rail.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;
import com.rail.facade.RailSystemFacade;

import lombok.Setter;

/**
 * This is the Main API class which has all the end points exposed as service
 * @author sagarwal
 */
@RestController
public class RailSystemRestAPI {
	
	@Setter @Autowired
	private RailSystemFacade railSystemFacade;
	
	@Setter @Autowired
	private RailSystemRestUtil railSystemRestUtil;

	/**
	 * Rest API for initialization of the Railway System
	 * @return
	 */
	@RequestMapping("/setup")
	public ResponseEntity<String> setup() {
		boolean result = railSystemFacade.setup("bin/StationMap.csv");
		return railSystemRestUtil.constructResponse(result);
	}

	/**
	 * Rest API for finding the shortest path between the two given stations
	 * @param routeRequest
	 * @return
	 */
	@RequestMapping(value = "/find-route", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<RouteResponse> findRoute(@RequestBody RouteRequest routeRequest) {
		RouteResponse response = railSystemFacade.findRoute(routeRequest);
		printResponse(response);
		return railSystemRestUtil.constructResponse(response);
	}

	/**
	 * Rest API for finding the shortest path between the two given stations at the given time.
	 * @param routeRequest
	 * @return
	 */
	@RequestMapping(value = "/find-route-time", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<RouteResponse> findRouteWithTime(@RequestBody RouteRequest routeRequest) {
		RouteResponse response = railSystemFacade.findRouteTime(routeRequest);
		printResponse(response);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	private void printResponse(RouteResponse response) {
		System.out.println("___________________________________________________________________________");
		System.out.println("Stations travelled: "+response.getDistance());
		System.out.println("Number of times lines were changed: "+response.getNumberOfStationChanged());
		System.out.println("Route: "+response.getRouteStations());
		System.out.println(response.getTime()!=null?"TimeTaken: "+response.getTime():"");
		response.getRouteDescription().forEach(System.out::println);
		System.out.println("___________________________________________________________________________");
		response.getStationList().forEach(station -> {
			System.out.println(station.getName()+ " -> "+station.getCode());
		});
		
	}
}
