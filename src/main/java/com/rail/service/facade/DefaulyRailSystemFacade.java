package com.rail.service.facade;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;
import com.rail.service.graph.GraphService;

import lombok.Setter;

@Primary
@Component("defaulyRailSystemFacade")
public class DefaulyRailSystemFacade implements RailSystemFacade{
	
	@Autowired
	@Setter
	RailSystemFacadeUtil facadeUtil;
	
	@Autowired
	@Setter
	GraphService graphService;

	@Override
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

	@Override
	public RouteResponse findRoute(RouteRequest routeRequest) {
		validateInput(routeRequest);
		return graphService.findRoute(routeRequest);
	}

	private void validateInput(RouteRequest routeRequest) {
		String source = routeRequest.getSource().trim();
		String destination = routeRequest.getDestination().trim();
		if(StringUtils.isBlank(source) || StringUtils.isBlank(destination)) {
			 throw new IllegalArgumentException("Either source or destination is Blank");
		}
		if(StringUtils.equals(source, destination)) {
			throw new IllegalArgumentException("Source and destination cannot be same");
		}
	}

}
