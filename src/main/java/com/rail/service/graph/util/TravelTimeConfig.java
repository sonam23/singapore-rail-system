package com.rail.service.graph.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("traveltime")
public class TravelTimeConfig {
	
	@Getter @Setter private PeakHour peakhour = new PeakHour();
	@Getter @Setter private NonPeakHour nonpeakhour = new NonPeakHour();
	@Getter @Setter private NightHour nighthour = new NightHour();
	
	public class PeakHour {
		@Getter @Setter private double defaultTime;
		@Getter @Setter private Map<String, Double> others = new HashMap<String, Double>();
	}
	
	public class NonPeakHour {
		@Getter @Setter private double defaultTime;
		@Getter @Setter private Map<String, Double> others = new HashMap<String, Double>();
	}
	
	public class NightHour {
		@Getter @Setter private double defaultTime;
		@Getter @Setter private Map<String, Double> others = new HashMap<String, Double>();
		@Getter @Setter private List<String> closed = new ArrayList<String>();
	}
}
