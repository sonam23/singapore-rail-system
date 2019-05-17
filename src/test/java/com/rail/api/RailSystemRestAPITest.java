package com.rail.api;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rail.common.CommonTestUtils;
import com.rail.entity.RouteRequest;
import com.rail.entity.RouteResponse;
import com.rail.facade.RailSystemFacade;

@RunWith(MockitoJUnitRunner.class)
public class RailSystemRestAPITest {

	@Mock
	private RailSystemFacade mockRailSystemFacade;
	
	private RailSystemRestUtil railSystemRestUtil;
	
	@InjectMocks
	private RailSystemRestAPI railSystemRestAPI;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		railSystemRestUtil = new RailSystemRestUtil();
		railSystemRestAPI.setRailSystemRestUtil(railSystemRestUtil);
	}
	@Test
	public void test_Setup_Failed(){
		ResponseEntity<String> response = railSystemRestAPI.setup();
		Assert.assertNotNull(response);
		Assert.assertEquals("Expected InternalServerError", response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Test
	public void test_Setup_Succcesful(){
		when(mockRailSystemFacade.setup(anyString())).thenReturn(true);
		ResponseEntity<String> response = railSystemRestAPI.setup();
		Assert.assertNotNull(response);
		Assert.assertEquals("Expected Success", response.getStatusCode(), HttpStatus.ACCEPTED);
	}
	
	@Test
	public void test_FindRoute_Success() {
		RouteResponse routeResponse = CommonTestUtils.buildSuccessRouteResponse();
		when(mockRailSystemFacade.findRoute(any(RouteRequest.class))).thenReturn(routeResponse);
		
		RouteRequest routeRequest = new RouteRequest();
		ResponseEntity<RouteResponse> response= railSystemRestAPI.findRoute(routeRequest);
		Assert.assertNotNull(response);
		Assert.assertEquals("Expected Success", response.getStatusCode(), HttpStatus.ACCEPTED);
		Assert.assertNotNull(response.getBody());
		Assert.assertNotNull(response.getBody().getRouteStations());
	}
	
	@Test
	public void test_FindRoute_Failed_NoRoute() {
		when(mockRailSystemFacade.findRoute(any(RouteRequest.class))).thenReturn(null);
		
		RouteRequest routeRequest = new RouteRequest();
		ResponseEntity<RouteResponse> response= railSystemRestAPI.findRoute(routeRequest);
		Assert.assertNotNull(response);
		Assert.assertEquals("Expected Success", response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		Assert.assertNull(response.getBody());
		Assert.assertNotNull(response.getHeaders());
		String message = response.getHeaders().get("message").get(0);
		Assert.assertEquals("Mesage not equal",message,"No route found between the stations");
	}
	
	@Test 
	public void test_FindRoute_Failed_Exception(){
		String message = "Either source or destination is Blank";
		when(mockRailSystemFacade.findRoute(any(RouteRequest.class))).thenThrow(new IllegalArgumentException(message));
		
		RouteRequest routeRequest = new RouteRequest();
		try {
			railSystemRestAPI.findRoute(routeRequest);
		}catch(IllegalArgumentException e) {
			Assert.assertEquals("Mesage not equal",message, e.getMessage());
		}catch(Exception e) {
			Assert.fail("Expecting IllegalArgumentException exception");
		}
	}
}
