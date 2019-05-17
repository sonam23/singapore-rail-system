package com.rail.facade;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

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
import com.rail.service.graph.GraphService;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRailSystemFacadeTest {
	
	@InjectMocks
	DefaultRailSystemFacade railSystemFacade;

	@Mock
	RailSystemFacadeUtil mockFacadeUtil;
	
	@Mock
	GraphService graphService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_Setup_Success() {
		when(mockFacadeUtil.readFile(anyString())).thenReturn(new ArrayList<String[]>());
		when(graphService.initializeNetwork(any(ArrayList.class))).thenReturn(true);
		boolean result = railSystemFacade.setup("");
		Assert.assertTrue(result);
	}
	
	@Test
	public void test_Setup_Failed() {
		boolean result = railSystemFacade.setup("");
		Assert.assertFalse(result);
	}
	
	@Test
	public void test_FindRoute_Success() {
		RouteRequest request = CommonTestUtils.buildRouteRequest("A", "B");
		RouteResponse expectedResponse = CommonTestUtils.buildSuccessRouteResponse();
		when(graphService.findRoute(any(RouteRequest.class))).thenReturn(expectedResponse);
		
		RouteResponse response = railSystemFacade.findRoute(request);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void test_FindRoute_Failed_Vaildation_EmptySource() {
		RouteRequest request = CommonTestUtils.buildRouteRequest("", "B");
		try {
			railSystemFacade.findRoute(request);
		}catch(IllegalArgumentException e) {
			Assert.assertEquals("Mesage not equal","Either source or destination is Blank", e.getMessage());
		}catch(Exception e) {
			Assert.fail("Expecting IllegalArgumentException exception");
		}
	}
	
	@Test
	public void test_FindRoute_Failed_Vaildation_SameSourceDestination() {
		RouteRequest request = CommonTestUtils.buildRouteRequest("A", "A");
		try {
			railSystemFacade.findRoute(request);
		}catch(IllegalArgumentException e) {
			Assert.assertEquals("Mesage not equal","Source and destination cannot be same", e.getMessage());
		}catch(Exception e) {
			Assert.fail("Expecting IllegalArgumentException exception");
		}
	}
	
	@Test
	public void test_FindRoute_Failed_Exception() {
		String message = "No such station = A";
		when(graphService.findRoute(any(RouteRequest.class))).thenThrow(new IllegalArgumentException(message));

		RouteRequest request = CommonTestUtils.buildRouteRequest("A", "B");
		try {
			railSystemFacade.findRoute(request);
		}catch(IllegalArgumentException e) {
			Assert.assertEquals("Mesage not equal",message, e.getMessage());
		}catch(Exception e) {
			Assert.fail("Expecting IllegalArgumentException exception");
		}
	}
	
	@Test
	public void test_FindRouteTime_Failed() {
		RouteRequest request = CommonTestUtils.buildRouteRequest("A", "B");
		try {
			railSystemFacade.findRouteTime(request);
		}catch(InternalError e) {
			Assert.assertEquals("Mesage not equal","Work under progress", e.getMessage());
		}
	}
}
