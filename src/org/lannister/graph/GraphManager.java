package org.lannister.graph;

import java.util.List;

// a static delegate for Graph class

public class GraphManager {

	private static Graph graph;
	
	public static Graph get() {
		if(graph == null) {
			graph = new Graph();
		}
		
		return graph;
	}
	
	public static List<String> path(String s, String d) {
		List<String> path = graph.findPath(s, d);
		path.remove(0);
		return path;
	}
	
	public static String getUnvisited() {
		return graph.getUnvisited();
	}
	
	public static void removeUnvisited() {
		graph.removeUnvisited();
	}
	
	public static void setVisited(String vertex) {
		graph.setVisited(vertex);
	}
}
