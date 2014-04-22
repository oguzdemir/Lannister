package org.lannister.graph;

import java.util.LinkedList;

import org.lannister.agents.AgentsController;

public class GraphManager {
	
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
		baseNodes = baseNodes.isEmpty() ? getBaseNodes(AgentsController.TEAMSIZE) : baseNodes;
		return baseNodes.removeFirst();
	}
}


