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
		List<String> path = get().findPath(s, d);
		path.remove(0);
		return path;
	}
	
	public static synchronized String getUnvisited() {
		return get().getUnvisited();
	}
	
	public static synchronized void removeUnvisited() {
		get().removeUnvisited();
	}
	
	public static synchronized void setVisited(String vertex) {
		get().setVisited(vertex);
	}
}
