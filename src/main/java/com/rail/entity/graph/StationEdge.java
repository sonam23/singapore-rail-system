package com.rail.entity.graph;

import org.jgrapht.graph.DefaultEdge;

/**
 * This is the edge between the two station nodes, in the network
 * @author sagarwal
 */
public class StationEdge extends DefaultEdge{
	
	private static final long serialVersionUID = -4317622169415963024L;

	@Override
    public String toString(){
    	Station source = (Station) getSource();
    	Station destination = (Station) getTarget();
        return "(" + destination.getCode() + " : " + source.getCode() + ")";
    }

}
