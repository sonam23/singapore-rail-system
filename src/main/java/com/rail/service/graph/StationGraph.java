package com.rail.service.graph;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;
import com.rail.entity.graph.Station;
import com.rail.entity.graph.StationEdge;
import com.rail.service.graph.util.DateTimeUtil;
import com.rail.service.graph.util.GraphUtil;
import com.rail.service.graph.util.TimeCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Primary class for all the graph related operations.
 * Is responsible for the setup and construction of the graph based on the CSV file input.
 * Also, uses Shortest path algorithm to find the best route between stations.
 * @author sagarwal
 */
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
	
	@AllArgsConstructor
	public class LineToStationEdge{
		@Getter @Setter String line;
		@Getter @Setter StationEdge edge;
	}
	
	@Getter private HashMap<String, Station> stationHashMap;
	
	private ArrayList<String[]> stationList;
	
	@Getter private Graph<Station, StationEdge> graph;
	
	private ArrayList<LineToStationEdge> lineToStationEdgeList;

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
		lineToStationEdgeList = new ArrayList<StationGraph.LineToStationEdge>();
		for(int i =0; i< stationList.size(); i++) {
			String[] stationArr = stationList.get(i);
			String code = stationArr[0];
			String name = stationArr[1];
			String currentLine = graphUtil.getCurrentLineFromCode(code);
			Station station = stationHashMap.get(name);
			//CurrentLine has changed, so this would be the first node of the new line
			if(!currentLine.equals(prevLine)) {
				System.out.println(currentLine);
				prevStation = null;	
			}
			
			upsertStation(station, prevStation, currentLine);
			prevStation = station;
			prevLine = currentLine;
		}	
	}

	/**
	 * Adds a new Station in the graph, if station is not already present
	 * Connects the stations, by adding an edge between them
	 * Stores the edges for each line, this would be useful for assosiating weights as required
	 * @param station
	 * @param prevStation
	 */
	private void upsertStation(Station station, Station prevStation, String currentLine) {
		if(!graph.containsVertex(station)) {
			graph.addVertex(station);
		}
		if(prevStation != null) {
			StationEdge edge = graph.addEdge(prevStation, station);
			lineToStationEdgeList.add(new LineToStationEdge(currentLine, edge));
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
		if(path.getLength() == 0) {
			//This would imply that no route has been found
			return null;
		}
		RouteResponse response = graphUtil.constructResponse(path);
		return response;
	}
	
	/**
	 * Finds the shortest path based on DijkstraShortestPath algorithm
	 * This ensures that we have weighted graphs based on PEAK, NON_pEAK and NIGHT Hours
	 * @param routeRequest
	 */
	public RouteResponse findShortestPathTime(RouteRequest routeRequest) {
		Station startVertex = stationHashMap.get(routeRequest.getSource());
		Station endVertex = stationHashMap.get(routeRequest.getDestination());
		
		Map<StationEdge, Double> hashMapWeights = null;
		TimeCategory timeCategory = DateTimeUtil.getTimeCategoryForTravel(routeRequest.getTime());
		switch(timeCategory) {
			case PEAK_HOURS:
				hashMapWeights = graphUtil.getPeakHourTravelMap(lineToStationEdgeList);
				break;
			case NON_PEAK_HOURS:
				hashMapWeights = graphUtil.getNonPeakHourTravelMap(lineToStationEdgeList);
				break;
			case NIGHT_HOURS:
				hashMapWeights = graphUtil.getNightHourTravelMap(lineToStationEdgeList);
		}
		
		Graph<Station, StationEdge> weightedGraph = new AsWeightedGraph<Station, StationEdge>(graph, hashMapWeights);	
		if(timeCategory.equals(TimeCategory.NIGHT_HOURS)) {
			weightedGraph = graphUtil.removeEdgesNotOperational(weightedGraph,lineToStationEdgeList);
		}
		
		GraphPath<Station, StationEdge> path = DijkstraShortestPath.findPathBetween(weightedGraph, startVertex, endVertex);
		Double time = path.getWeight();
		if(path.getLength() == 0) {
			//This would imply that no route has been found
			return null;
		}
		RouteResponse response = graphUtil.constructResponse(path, time, timeCategory);
		return response;
	}
}
