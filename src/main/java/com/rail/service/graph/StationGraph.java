package com.rail.service.graph;

import java.util.ArrayList;
import java.util.HashMap;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;
import com.rail.entity.graph.Station;
import com.rail.entity.graph.StationEdge;
import com.rail.service.graph.util.GraphUtil;

import lombok.Getter;
import lombok.Setter;

@Component
public class StationGraph {
	
	@Autowired @Setter
	GraphUtil graphUtil;
	
	private StationGraph() {	
	}
	
	public static StationGraph getInstance() {
		return StationGraphSingletonHolder.INSTANCE;
	}
	
	private static class StationGraphSingletonHolder {
		private static final StationGraph INSTANCE = new StationGraph();
	}
	
	@Getter private HashMap<String, Station> stationHashMap;
	private ArrayList<String[]> stationList;
	
	@Getter 
	private Graph<Station, StationEdge> graph;

	public void init(HashMap<String, Station> stationHashMap, ArrayList<String[]> stationList) {
		this.stationHashMap = stationHashMap;
		this.stationList = stationList;
		graph = new SimpleGraph<>(StationEdge.class);
	}

	/**
	 * Constructs the entire network of Singapore RailWay Line, based on the given input file provided.
	 * Assumptions:
	 * 	1. The stations appear in the order provided in the input file
	 * 	2. The station lines are unique and when we encounter a different station line, it means that the current line has ended.   
	 */
	public void constructGraph() {
		Station prevStation = null;
		String prevLine = "";
		
		//This is used for graph construction
		for(int i =0; i< stationList.size(); i++) {
			String[] stationArr = stationList.get(i);
			String code = stationArr[0];
			String name = stationArr[1];
			String currentLine = graphUtil.getCurrentLineFromCode(code);
			Station station = stationHashMap.get(name);
			//CurrentLine has changed, so this would be the first node of the new line
			if(!currentLine.equals(prevLine)) {
				prevStation = null;	
			}
			
			upsertStation(station, prevStation);
			prevStation = station;
			prevLine = currentLine;
		}	
	}

	/**
	 * Adds a new Station in the graph, if station is not already present
	 * Connects the stations, by adding an edge between them
	 * @param station
	 * @param prevStation
	 */
	private void upsertStation(Station station, Station prevStation) {
		if(!graph.containsVertex(station)) {
			System.out.println("NEW station StationName="+station.getName());
			graph.addVertex(station);
		}else {
			System.out.println("STATION already exists StationName="+station.getName());
		}
		if(prevStation != null) {
			System.out.println("Added Edge between Station="+ station.getName()+" Prevstation="+prevStation.getName());
			graph.addEdge(prevStation, station);
		}else {
			System.out.println("NO edge added for new StationName="+ station.getName());
		}
	}

	/**
	 * Finds the shortest path based on DijkstraShortestPath algorithm
	 * @param routeRequest
	 */
	public RouteResponse findShortestPath(RouteRequest routeRequest) {
		Station startVertex = stationHashMap.get(routeRequest.getSource());
		Station endVertex = stationHashMap.get(routeRequest.getDestination());
		GraphPath<Station, StationEdge> path = DijkstraShortestPath.findPathBetween(graph, startVertex, endVertex);
		RouteResponse response = graphUtil.constructResponse(path);
		return response;
	}
}
