package com.rail.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Request object to find the route between the two stations.
 * @author sagarwal
 */
@Setter
@Getter
@NoArgsConstructor
public class RouteRequest {
	
	private String source;
	private String destination;
	private String time;
}
