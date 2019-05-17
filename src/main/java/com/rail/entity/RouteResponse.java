package com.rail.entity;

import java.util.ArrayList;
import java.util.List;

import com.rail.entity.graph.Station;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
/**
 * Response object to find the route between the two stations.
 * @author sagarwal
 */
public class RouteResponse {

	@Getter @Setter private int distance;
	@Getter @Setter private ArrayList<String> routeStations;
	@Getter private ArrayList<String> routeDescription;
	@Getter @Setter private List<Station> stationList;
	private ArrayList<EdgeStationLinks> stationsLinks;
	@Getter @Setter private String time;
	
	public RouteResponse(){
		routeDescription = new ArrayList<String>();
		stationsLinks = new ArrayList<RouteResponse.EdgeStationLinks>();
	}
	
	/**
	 * Inner class to represnt the link between the two stations and the line on which the route exists 
	 */
	@AllArgsConstructor
	private class EdgeStationLinks{
		@Getter @Setter private String source;
		@Getter @Setter private String destination;
		@Getter @Setter private String line;
	}
	
	/**
	 * Adds the source and destination on the given line, which forms the edge of the graph
	 * @param source
	 * @param destination
	 * @param line
	 */
	public void addStationsLink(String source, String destination, String line) {
		EdgeStationLinks link = new EdgeStationLinks(source, destination, line);
		stationsLinks.add(link);
		String desc = "Take "+line+" line from "+source+" to "+destination;
		//System.out.println(desc);
		routeDescription.add(desc);
	}
	
	/**
	 * Adds the information when the line has to be changed in the route provided
	 * @param sourceLine
	 * @param destLine
	 */
	public void addLineChange(String sourceLine, String destLine) {
		String desc = "Change from "+sourceLine+" line to "+destLine+" line";
		//System.out.println(desc);
		routeDescription.add(desc);
	}
}
