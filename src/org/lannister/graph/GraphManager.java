package org.lannister.graph;

import java.util.LinkedList;

public class GraphManager {
	
	private static int requestCounts = 0;
	private static int TEAMSIZE		 = 12;
	
	private static Graph graph;
	
	private static LinkedList<String> baseNodes = new LinkedList<String>();
	
	public static Graph get() {
		return graph == null ? graph = new Graph() : graph;
	}
	
	private static LinkedList<String> getBaseNodes(int size) {
		if(!baseNodes.isEmpty()) {
			return baseNodes;
		}
		
		// update base nodes
		baseNodes = graph.findBaseNodes(size);
		System.out.println("BASE NODES ARE FOUND: " + baseNodes);
		return baseNodes;
	}
	
	public static String grabBaseNode() {
		baseNodes = baseNodes.isEmpty() ? getBaseNodes(TEAMSIZE) : baseNodes;
		return baseNodes.removeFirst();
	}
	
	public static void requestUpdate() {
		++requestCounts;
		System.out.println("Request received: " + requestCounts);
		if(requestCounts == TEAMSIZE) {
			// everybody requested, run APS algorithm
			graph.aps();
			
			// reset
			requestCounts = 0;
		}
	}
}


