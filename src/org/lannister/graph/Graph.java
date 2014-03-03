package org.lannister.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Graph {
	
	private int visitedCount = 0;
	private int MAX_INT = 1000000000;
	
	private Map<String, Map<String, Integer>> graph = new HashMap<String, Map<String, Integer>>();
	private Map<String, Map<String, Integer>> dist  = new HashMap<String, Map<String, Integer>>();
	
	private List<String> unvisited = new LinkedList<String>();
	
	public void addVertex(String v1) {
		if(!graph.containsKey(v1)) {
			unvisited.add(0, v1);
			graph.put(v1, new HashMap<String, Integer>());
		}
	}

	public void addEdge(String v1, String v2, Integer w) {
		if(!graph.containsKey(v1)) {
			unvisited.add(0, v1);
			graph.put(v1, new HashMap<String, Integer>());
		}
		if(!graph.get(v1).containsKey(v2))
			graph.get(v1).put(v2, w);

		if(!graph.containsKey(v2)) {
			unvisited.add(0, v2);
			graph.put(v2, new HashMap<String, Integer>());
		}
		if(!graph.get(v2).containsKey(v1))
			graph.get(v2).put(v1, w);
	}
	
	// returns an unvisited node
	public String getUnvisited() {
		String node = unvisited.get(0);
		
		return node;
	}
	
	// removes a visited node
	public void removeUnvisited() {
		unvisited.remove(0);
	}
	
	// removes a node from unvisited queue
	public void setVisited(String vertex) {
		boolean ret = unvisited.remove(vertex);
		if(ret)
			System.out.println("Visited count: " + ++visitedCount);
	}
	
	// finding shortest paths between every pair of node
	public void allPairsShortestPath() {
		Set<String> vertices = graph.keySet();
		
		// initialize with infinity
		for(String v1 : vertices) {
			dist.put(v1, new HashMap<String, Integer>());
			for(String v2 : vertices) {
				dist.get(v1).put(v2, MAX_INT);
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
					}
				}		
	}
	
	// backtracking recursively
	public List<String> findPath(String s, String d) {
		Integer distance = dist.get(s).get(d);
		return findPath(s, d, distance);
	}
	
	private List<String> findPath(String s, String d, Integer distance) {
		
		List<String> path = new LinkedList<String>();
		path.add(s);
		
		if(s.equals(d)) {
			return path;
		}
		
		for(Map.Entry<String, Integer> entry : graph.get(s).entrySet()) {
			String neighbor = entry.getKey();
			Integer weight  = entry.getValue();
			Integer left    = dist.get(neighbor).get(d);
			if(left == distance - weight) {
				List<String> rest = findPath(neighbor, d, left);
				path.addAll(rest);
				return path;
			}
		}
		
		// should not reach here!
		return null;
	}
}
