package com.rail.service.graph;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.rail.common.CommonTestUtils;
import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;
import com.rail.entity.graph.Station;
import com.rail.service.graph.util.GraphUtil;


@RunWith(MockitoJUnitRunner.class)
public class DefaultGraphServiceTest {
	
	@InjectMocks
	DefaultGraphService defaultGraphService;
	
	@Mock 
	GraphUtil graphUtil;
	
	@Mock
	StationGraph stationGraph;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void test_findRoute_Success() {
		RouteRequest routeRequest = CommonTestUtils.buildRouteRequest("A", "B");
		RouteResponse expectedResponse = CommonTestUtils.buildSuccessRouteResponse();
		ArrayList<String> str = new ArrayList<String>(); str.add("A"); str.add("B");
		HashMap<String, Station>  list = CommonTestUtils.buildHashMap(str);
		when(stationGraph.findShortestPath(any(RouteRequest.class))).thenReturn(expectedResponse);
		when(stationGraph.getStationHashMap()).thenReturn(list);
		RouteResponse response = defaultGraphService.findRoute(routeRequest);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void test_findRoute_Failed_Validation_NetworkInitialization() {
		RouteRequest routeRequest = CommonTestUtils.buildRouteRequest("A", "B");	
		when(stationGraph.getStationHashMap()).thenReturn(null);
		try {
			defaultGraphService.findRoute(routeRequest);
		}catch(InternalError e) {
			Assert.assertEquals("Mesage not equal","Please initialize the network first!", e.getMessage());
		}catch(Exception e) {
			Assert.fail("Expecting InternalError exception");
		}
	}
	
	@Test
	public void test_findRoute_Failed_Validation_NoSuchStation() {
		RouteRequest routeRequest = CommonTestUtils.buildRouteRequest("A", "B");
		ArrayList<String> str = new ArrayList<String>(); str.add("A");
		HashMap<String, Station>  list = CommonTestUtils.buildHashMap(str);
		when(stationGraph.getStationHashMap()).thenReturn(list);
		try {
			defaultGraphService.findRoute(routeRequest);
		}catch(IllegalArgumentException e) {
			Assert.assertEquals("Mesage not equal","No such station = B", e.getMessage());
		}catch(Exception e) {
			Assert.fail("Expecting IllegalArgumentException exception");
		}
	}
}
