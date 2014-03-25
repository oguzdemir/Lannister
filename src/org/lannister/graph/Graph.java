package org.lannister.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class Graph {
	
	private int MAX_INT = 1000000000;
	
	// maps internal representation of nodes
	private Map<String, Integer> r = new HashMap<String, Integer>();
	// reverse map of internal representation of nodes
	private Map<Integer, String> rr = new HashMap<Integer, String>();
	// probed information of nodes
	private Map<String, Integer> pr = new HashMap<String, Integer>();
	
	private int[][]   g = new int[400][400]; // all zero initially
	private int[][]   d = new int[400][400]; // all zero initially
	private int[][]   p = new int[400][400];
	private boolean[] v = new boolean[400];
	private int       cur;
	private int 	  visited;
	
	// register a new vertex
	private void register(String v) {
		r.put(v, cur);
		rr.put(cur, v);
		cur++;
	}
	
	public void addVertex(String v1) {
		if(!isKnown(v1)) {
			register(v1);
		}
	}
	
	public void addEdge(String v1, String v2, Integer w) {
		if(!isKnown(v1)) {
			register(v1);
		}
		if(!isKnown(v2)) {
			register(v2);
		}
		int i = r.get(v1);
		int j = r.get(v2);
		g[i][j] = g[j][i] = w;
	}
	
	// gets an unvisited closest vertex that no other agent is targetted that vertex.
	// takes O(n) time where n is the size of vertices
	public String getUnvisited(String vertex, Collection<String> otherAgentsTargets) {
		
		// register target nodes if they are not known by the agent
		for(String target : otherAgentsTargets) {
			if(!isKnown(target)) {
				register(target);
			}
		}
		
		int cost = Integer.MAX_VALUE;
		int i    = r.get(vertex);
		int cand = 0;
		
		for(int j = 0; j < cur; j++) {
			if(!v[j] && cost > d[i][j] && !otherAgentsTargets.contains(rr.get(j))) {
				cost = d[i][j];
				cand = j;
			}
		}
		return !v[cand] ? rr.get(cand) : null;
	}
	
	// gets an unprobed closest vertex that no other agent is targetted that vertex.
	// takes O(n) time where n is the size of vertices
	public String getUnprobed(String vertex, Collection<String> otherAgentsTargets) {
		// register target nodes if they are not known by the agent
		for(String target : otherAgentsTargets) {
			if(!isKnown(target)) {
				register(target);
			}
		}
		
		int cost = Integer.MAX_VALUE;
		int i    = r.get(vertex);
		int cand = 0;
		
		for(int j = 0; j < cur; j++) {
			String v = rr.get(j);
			if(!pr.containsKey(v) && cost > d[i][j] && !otherAgentsTargets.contains(rr.get(j))) {
				cost = d[i][j];
				cand = j;
			}
		}
		return !pr.containsKey(rr.get(cand)) ? rr.get(cand) : null;
	}
	
	// removes a node from unvisited queue
	public void setVisited(String vertex) {
		// register if not known
		if(!isKnown(vertex)) {
			register(vertex);
		}
		
		int i = r.get(vertex);
		if(!v[i]) visited++;
		v[i] = true;
	}
	
	public boolean isProbed(String vertex) {
		return pr.containsKey(vertex);
	}
	
	public void setProbed(String vertex, Integer weight) {
		pr.put(vertex, weight);
	}
	
	public int probeValue(String vertex) {
		return isProbed(vertex) ? pr.get(vertex) : 0;
	}
	
	// returns best closest vertex to probe
	public String getBestProbe(String vertex) {
		int i = r.get(vertex);
		int cand = i;
		int maxProbe = 0;
		
		for(int j = 0; j < cur; j++) {
			if(probeValue(rr.get(j)) > maxProbe) {
				cand = j;
				maxProbe = probeValue(rr.get(j));
			} 
			else if(probeValue(rr.get(j)) == maxProbe && d[i][j] < d[i][cand]) {
				cand = j;
			}
		}
		
		return rr.get(cand);
	}
	
	// returns true if a vertex is seen before
	public boolean isKnown(String vertex) {
		return r.containsKey(vertex);
	}
	
	public void aps() {
		for(int i = 0; i < cur; i++)
			for(int j = 0; j < cur; j++) {
				d[i][j] = g[i][j] != 0 ? g[i][j] : MAX_INT;
				p[i][j] = g[i][j] != 0 ? i : 0;
			}
		for(int i = 0; i < cur; i++)
			d[i][i] = 0;
		for(int k = 0; k < cur; k++)
			for(int i = 0; i < cur; i++)
				for(int j = 0; j < cur; j++) {
					int mb = d[i][k] + d[k][j];
					if(mb < d[i][j]) {
						d[i][j] = mb;
						p[i][j] = p[k][j];
					}
				}
	}
	
	public LinkedList<String> path(String s, String d) {
		int i = r.get(s);
		int j = r.get(d);
		
		List<String> path = new LinkedList<String>();
		
		int pre = j;
		while(i != pre) {
			String node = rr.get(pre);
			path.add(node);
			pre = p[i][pre];
		}
		
		return new LinkedList<String>(Lists.reverse(path));
	}
	
	public int visited() {
		return visited;
	}
	
	public int size() {
		return cur;
	}
}
