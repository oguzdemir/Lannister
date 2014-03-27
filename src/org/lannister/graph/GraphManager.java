package org.lannister.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphManager {

	private static Map<String, Graph> graphs = new HashMap<String, Graph>();
	
	private static List<String> baseNodes = new LinkedList<String>();
	
	public static synchronized Graph get(String agentName) {
		if(graphs.get(agentName) == null) {
			graphs.put(agentName, new Graph());
		}
		
		return graphs.get(agentName);
	}
	
	public static synchronized List<String> getBaseNodes(int size) {
		if(!baseNodes.isEmpty()) {
			return baseNodes;
		}
		
		// get any graph
		Graph g = graphs.values().iterator().next();
		
		// update base nodes
		baseNodes = g.findBaseNodes(size);
		
		// return
		return baseNodes;
	}
}
