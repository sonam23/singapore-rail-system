package com.rail.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.rail.entity.RouteResponse;

/**
 * This is the util class to construct responses and also headers, authorization etc
 * @author sagarwal
 */
@Component
public class RailSystemRestUtil {
	
	public ResponseEntity<String> constructResponse(boolean result) {
		String message = result?"Successfully Setup Network":"Rail Network Setup Failed";
		HttpHeaders headers = new HttpHeaders();
		headers.add("message", message);
		if(result) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).headers(headers).body(message);
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).body(message);
		}
	}
	
	public ResponseEntity<RouteResponse> constructResponse(RouteResponse response) {
		if(response == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", "No route found between the stations");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
}
