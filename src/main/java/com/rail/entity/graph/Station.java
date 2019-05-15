package com.rail.entity.graph;

import java.util.ArrayList;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Station {
	
	@Setter @Getter private ArrayList<String> code;
	@Getter final private String name;
	@Getter final private String date;
	
	public boolean equals(Object o){
		if (o == this) return true;
        if (!(o instanceof Station)) {
            return false;
        }
        Station station = (Station) o;
        return (station.name.equalsIgnoreCase(name)); 
    }
	
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
