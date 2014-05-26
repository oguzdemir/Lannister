package org.lannister.graph;

import java.util.LinkedList;

import org.lannister.agents.AgentsController;

public class GraphManager {
	
	private static Graph graph;
	
	private static LinkedList<String> baseNodes 	= new LinkedList<String>();
	private static LinkedList<String> baseNodesHist = new LinkedList<String>();
	private static boolean tokenTaken;
	
	public static Graph get() {
		return graph == null ? graph = new Graph() : graph;
	}
	
	private static LinkedList<String> getBaseNodes(int size) {
		if(!baseNodes.isEmpty()) {
			return baseNodes;
		}
		
		// update base nodes
		baseNodes = graph.findBaseNodes(size / 2, baseNodesHist);
		baseNodesHist.addAll(baseNodes);
		System.out.println("BASE NODES ARE FOUND: " + baseNodes);
		System.out.println("BASE NODE HISTORY: " + baseNodesHist);
		return baseNodes;
	}
	
	public static String grabBaseNode() {
		baseNodes = baseNodes.isEmpty() ? getBaseNodes(AgentsController.TEAMSIZE) : baseNodes;
		return baseNodes.removeFirst();
	}
	
	public static void resetToken() {
		tokenTaken = false;
	}
	
	public static boolean acquireToken() {
		boolean ret = tokenTaken == false;
		tokenTaken = true;
		return ret;
	}
}


