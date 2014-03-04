package org.lannister.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Graph {
	
	private int visitedCount = 0;
	private int MAX_INT = 1000000000;
	
	private Map<String, Map<String, Integer>> graph = new HashMap<String, Map<String, Integer>>();
	private Map<String, Map<String, Integer>> dist  = new HashMap<String, Map<String, Integer>>();
	private Map<String, Map<String, String>> next  = new HashMap<String, Map<String, String>>(); 
	
	//private List<String> unvisited = new LinkedList<String>();
	private Map<String, Boolean> visited = new HashMap<String, Boolean>();
	
	
	public void addVertex(String v1) {
		if(!graph.containsKey(v1)) {
			visited.put(v1, false);
			graph.put(v1, new HashMap<String, Integer>());
		}
	}

	public void addEdge(String v1, String v2, Integer w) {
		if(!graph.containsKey(v1)) {
			visited.put(v1, false);
			graph.put(v1, new HashMap<String, Integer>());
		}
		if(!graph.get(v1).containsKey(v2))
			graph.get(v1).put(v2, w);

		if(!graph.containsKey(v2)) {
			visited.put(v2, false);
			graph.put(v2, new HashMap<String, Integer>());
		}
		if(!graph.get(v2).containsKey(v1))
			graph.get(v2).put(v1, w);
	}
	
	// another implementation to get an unvisited closest vertex
	// take O(n) time where n is the size of vertices
	public String getUnvisited(String position) {
		int minCost = Integer.MAX_VALUE;
		String unvisitedVertex = null;
		for(Map.Entry<String, Integer> en : dist.get(position).entrySet()) {
			String vertex = en.getKey();
			if(!visited.get(vertex)) {
				Integer cost  = en.getValue();
				if(cost < minCost) {
					minCost = cost;
					unvisitedVertex = vertex;
				}
			}
		}
		return unvisitedVertex;
	}
	
	// removes a node from unvisited queue
	public void setVisited(String vertex) {
		boolean ret = visited.get(vertex);
		if(!ret)
			System.out.println("Visited count: " + ++visitedCount);
		visited.put(vertex, true);
	}
	
	// returns true if a vertex is seen before
	public boolean isKnown(String vertex) {
		return graph.containsKey(vertex);
	}
	
	// finding shortest paths between every pair of node
	// takes O(n^3) time where n is the size of vertices
	public void allPairsShortestPath() {
		Set<String> vertices = graph.keySet();
		
		// initialize with infinity
		for(String v1 : vertices) {
			dist.put(v1, new HashMap<String, Integer>());
			next.put(v1, new HashMap<String, String>());
			for(String v2 : vertices) {
				dist.get(v1).put(v2, MAX_INT);
				next.get(v1).put(v2, null);
			}
		}
		
		// for each vertex shortest path to itself is zero 
		for(String v1 : vertices) {
			dist.get(v1).put(v1, 0);
		}
		
		// for each edge shortest path between vertices is the weight of the edge
		for(Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
			
			String v1 					= entry.getKey();
			Map<String, Integer> edges 	= entry.getValue();
			
			for(Map.Entry<String, Integer> entry2 : edges.entrySet()) {
				String v2 = entry2.getKey();
				Integer w = entry2.getValue();
				dist.get(v1).put(v2, w);
				next.get(v1).put(v2, v2);
			}
		}
		
		// dynamically updating distance matrix
		for(String v3 : vertices)
			for(String v1 : vertices)
				for(String v2 : vertices) {
					Integer init  = dist.get(v1).get(v2);
					Integer maybe = dist.get(v1).get(v3) + dist.get(v3).get(v2);
					if(init > maybe) {
						dist.get(v1).put(v2, maybe);            
						next.get(v1).put(v2, next.get(v1).get(v3)); // next[v1][v2] <- next[v1][v3]
					}
				}		
	}
	
	// backtracking recursively
	// takes O(m) time where m is the size of the vertices in the minimum path
	public List<String> findPath(String s, String d) {
		
		if(dist.get(s).get(d) == MAX_INT) {
			return null; // NO PATH
		}
		
		List<String> path = new LinkedList<String>();
		
		String nxt = next.get(s).get(d);
		while(nxt != null) {
			path.add(nxt);
			nxt = next.get(nxt).get(d);
		}
		
		return path;
	}
	
	public int cost(String s, String d) {
		return dist.get(s).get(d);
	}
}
