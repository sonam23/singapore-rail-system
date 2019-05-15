package com.rail.entity;

import java.util.ArrayList;
import java.util.List;

import com.rail.entity.graph.Station;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class RouteResponse {

	@Getter @Setter private int distance;
	@Getter @Setter private ArrayList<String> routeStations;
	@Getter private ArrayList<String> routeDescription;
	@Getter @Setter private List<Station> stationList;
	private ArrayList<EdgeStationLinks> stationsLinks;
	
	public RouteResponse(){
		routeDescription = new ArrayList<String>();
		stationsLinks = new ArrayList<RouteResponse.EdgeStationLinks>();
	}
	
	@AllArgsConstructor
	private class EdgeStationLinks{
		@Getter @Setter private String source;
		@Getter @Setter private String destination;
		@Getter @Setter private String line;
	}
	
	public void addStationsLink(String s, String d, String line) {
		EdgeStationLinks link = new EdgeStationLinks(s, d, line);
		stationsLinks.add(link);
		String desc = "Take "+line+" line from "+s+" to "+d;
		//System.out.println(desc);
		routeDescription.add(desc);
	}
	
	public void addLineChange(String sourceLine, String destLine) {
		String desc = "Change from "+sourceLine+" line to "+destLine+" line";
		//System.out.println(desc);
		routeDescription.add(desc);
	}
}
