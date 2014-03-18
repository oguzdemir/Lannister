package org.lannister.graph;

import java.util.HashMap;
import java.util.Map;

public class GraphManager {

	private static Map<String, Graph> graphs = new HashMap<String, Graph>();
	
	public static synchronized Graph get(String agentName) {
		if(graphs.get(agentName) == null) {
			graphs.put(agentName, new Graph());
		}
		
		return graphs.get(agentName);
	}
}
