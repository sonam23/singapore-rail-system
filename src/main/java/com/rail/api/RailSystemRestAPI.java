package com.rail.api;

import java.util.ArrayList;

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
import com.rail.service.facade.RailSystemFacade;

import lombok.Setter;

@RestController
public class RailSystemRestAPI {
	
	@Setter @Autowired
	private RailSystemFacade railSystemFacade;

	@RequestMapping("/setup")
	public String setup() {
		boolean result = railSystemFacade.setup("bin/StationMap.csv");
		if(result)
			return "Successfully Setup Network";
		else
			return "Rail Network Setup Failed";
	}

	@RequestMapping(value = "/find-route", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<RouteResponse> findRoute(@RequestBody RouteRequest routeRequest) {
		RouteResponse response = railSystemFacade.findRoute(routeRequest);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
}
