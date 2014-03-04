package org.lannister.graph;

import java.util.List;

public class GraphManager {

	/**
	 * Singleton graph instance, any operation done on Graph should be one at a time.
	 */
	private static Graph graph;
	
	public static synchronized Graph get() {
		if(graph == null) {
			graph = new Graph();
		}
		
		return graph;
	}
	
	public static synchronized List<String> path(String s, String d) {
		return get().findPath(s, d);
	}
	
	public static synchronized int cost(String s, String d) {
		return get().cost(s, d);
	}
	
	public static synchronized String getUnvisited(String position) {
		return get().getUnvisited(position);
	}
	
	public static synchronized void setVisited(String vertex) {
		get().setVisited(vertex);
	}
	
	public static synchronized boolean isKnown(String vertex) {
		return get().isKnown(vertex);
	}
}
