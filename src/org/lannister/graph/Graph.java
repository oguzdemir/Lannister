package org.lannister.graph;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.lannister.graph.BFS.BFSPredicate;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Graph {
	
	private static int VERTEXCOUNT = 400;
	private int MAX_INT = 1000000000;
	
	// maps internal representation of nodes
	private Map<String, Integer> r = new HashMap<String, Integer>();
	// reverse map of internal representation of nodes
	private Map<Integer, String> rr = new HashMap<Integer, String>();
	// probed information of nodes
	private Map<String, Integer> pr = new HashMap<String, Integer>();
	
	private int[][]   g = new int[VERTEXCOUNT][VERTEXCOUNT]; // g[i][j] = 1 if there is an edge, else zero
	private int[][]   d = new int[VERTEXCOUNT][VERTEXCOUNT]; // d[i][j] represents the shortest edge distance 
	private int[][]   p = new int[VERTEXCOUNT][VERTEXCOUNT]; // predecessor info for path finding
	private int[][]   w = new int[VERTEXCOUNT][VERTEXCOUNT]; // w[i][j] represents edge cost between i and j if they are neighbors, else zero
	private boolean[] v = new boolean[VERTEXCOUNT];  // visited info for vertices
	private boolean[] s = new boolean[VERTEXCOUNT];  // surveyed info for vertices
	private int       cur 		= 0;
	private int 	  visited 	= 0;
	private int 	  probed	= 0;
	private int 	  surveyed  = 0;
	
	public Graph() {
		for(int i = 0; i < VERTEXCOUNT; i++)
			for(int j = 0; j < VERTEXCOUNT; j++) 
				w[i][j] = Integer.MAX_VALUE;
	}
	
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
	
	// equivalent of getUnvisited method, but it doesn't require that aps is run before.
	private String getUnvisitedBFS(String vertex, final Collection<String> otherAgentsTargets) {
		// register target nodes if they are not known by the agent
		for(String target : otherAgentsTargets) {
			if(!isKnown(target)) {
				register(target);
			}
		}
		final int i = r.get(vertex);
		LinkedList<Integer> path = BFS.run(g, cur, i, new BFSPredicate() {

			@Override
			public boolean isTrue(int t) {
				return i != t && !v[t] && !otherAgentsTargets.contains(rr.get(t));
			}
			
		});
		
		return path.isEmpty() ? null : rr.get(path.getLast());
	}
	
	private LinkedList<String> pathBFS(final String s, final String d) {
		// find path
		LinkedList<Integer> path = BFS.run(g, cur, r.get(s), new BFSPredicate() {
			public boolean isTrue(int t) {
				return t == r.get(d);
			}
		});
		// convert and return
		return new LinkedList<String>(Lists.transform(path, new Function<Integer, String>() {
			@Override
			public String apply(Integer i) {
				return rr.get(i);
			}
		}));
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
			if(!pr.containsKey(v) && cost > d[i][j] && !otherAgentsTargets.contains(v)) {
				cost = d[i][j];
				cand = j;
			}
		}
		return !pr.containsKey(rr.get(cand)) ? rr.get(cand) : null;
	}
	
	// equivalent of getUnprobed method, but it doesn't require that aps is run before.
	private String getUnprobedBFS(String vertex, final Collection<String> otherAgentsTargets) {
		// register target nodes if they are not known by the agent
		for(String target : otherAgentsTargets) {
			if(!isKnown(target)) {
				register(target);
			}
		}
		final int i = r.get(vertex);
		LinkedList<Integer> path = BFS.run(g, cur, r.get(vertex), new BFSPredicate() {

			@Override
			public boolean isTrue(int t) {
				return i != t && !pr.containsKey(rr.get(t)) && !otherAgentsTargets.contains(rr.get(t));
			}
			
		});
		
		String res = path.isEmpty() ? null : rr.get(path.getLast());
		
		System.out.println(res + " unprobed " + cur);
		
		return res;
	}
	
	// gets an unsurveyed closest vertex that no other agent is targetted that vertex.
	public String getUnsurveyed(String vertex, Collection<String> otherAgentsTargets) {
		// register target nodes if they are not known by the agent
		for(String target : otherAgentsTargets) {
			if(!isKnown(target)) {
				register(target);
			}
		}
		
		int cost = Integer.MAX_VALUE;
		int i	 = r.get(vertex);
		int cand = 0;
		
		for(int j = 0; j < cur; j++) {
			if(!s[j] && cost > d[i][j] && !otherAgentsTargets.contains(rr.get(j))) {
				cost = d[i][j];
				cand = j;
			}
		}
		
		return !s[cand] ? rr.get(cand) : null;
	}
	
	// equivalent of getUnsurveyed method, but it doesn't require that aps is run before.
	private String getUnsurveyedBFS(String vertex, final Collection<String> otherAgentsTargets) {
		// register target nodes if they are not known by the agent
		for(String target : otherAgentsTargets) {
			if(!isKnown(target)) {
				register(target);
			}
		}
		final int i = r.get(vertex);
		LinkedList<Integer> path = BFS.run(g, cur, i, new BFSPredicate() {

			@Override
			public boolean isTrue(int t) {
				return i != t && !s[t] && !otherAgentsTargets.contains(rr.get(t));
			}
			
		});
		
		return path.isEmpty() ? null : rr.get(path.getLast());
	}
	
	public String getClosest(String vertex, Collection<String> from, Collection<String> excluded) {
		// register target nodes if they are not known by the agent
		for(String target : from) {
			if(!isKnown(target)) {
				register(target);
			}
		}
		int i 	 = r.get(vertex);
		int cand = 0;
		int cost = Integer.MAX_VALUE;
		for(String otherVertex : from) {
			if(excluded.contains(otherVertex)) continue;
			int j = r.get(otherVertex);
			cand = d[i][j] < cost ? j 		: cand;
			cost = d[i][j] < cost ? d[i][j] : cost;
		}
		return rr.get(cand);
	}
	
	private String getClosestBFS(String vertex, final Collection<String> otherVertices) {
		// register target nodes if they are not known by the agent
		for(String target : otherVertices) {
			if(!isKnown(target)) {
				register(target);
			}
		}
		
		int j = BFS.run(g, cur, r.get(vertex), new BFSPredicate() {
			public boolean isTrue(int t) {
				return otherVertices.contains(rr.get(t));
			}
		}).getLast();
		
		return rr.get(j);
	}
	
	// distance between pos1 and pos2 should be one or zero, 
	// finds best pos3 such that dist(pos3, pos2) > dist(pos1, pos2) and dist(pos3, pos1) == 1 and cost(pos3,pos1) == minimum possible.
	public String findRunawayNode(String pos1, String pos2) {
		if(!isKnown(pos1)) register(pos1);
		if(!isKnown(pos2)) register(pos2);
		
		int i = r.get(pos1);
		int j = r.get(pos2);
		int m = Integer.MAX_VALUE;
		int r = -1;
		for(int k = 0; k < cur; k++) {
			if(d[k][j] > d[i][j] && d[k][i] == 1) {
				m = Math.min(w[k][i], m);
				r = w[k][i] == m ? k : r;
			}
		}
		return r == -1 ? null : rr.get(r);
	}
	
	public String findRandomNode(String pos) {
		if(!isKnown(pos)) register(pos);
		
		int i = r.get(pos);
		
		int j = (int) (Math.random() * cur);
		while(i == j) {
			j = (int) (Math.random() * cur);
		}
		
		return rr.get(j);
	}
	
	private String findRunawayNodeBFS(String pos1, String pos2) {
		int i = r.get(pos1);
		int j = r.get(pos2);
		
		for(int k = 0; k < cur; k++) {
			if(g[i][k] == 1 && g[j][k] == 0) return rr.get(k);
		}
		return null;
	}
	
	// removes a node from unvisited queue
	public void setVisited(String vertex) {
		// register if not known
		if(!isKnown(vertex)) {
			register(vertex);
		}
		
		int i = r.get(vertex);
		if(!v[i]) { visited++; }
		System.out.println("Visited: " + visited);
		v[i] = true;
	}
	
	public void setSurveyed(String vertex) {
		int i = r.get(vertex);
		if(!s[i]) { surveyed++; }
		System.out.println("Surveyed: " + surveyed);
		s[i] = true;
	}
	
	public void setSurveyedEdge(String vertex1, String vertex2, Integer value) {
		if(!isKnown(vertex1)) register(vertex1);
		if(!isKnown(vertex2)) register(vertex2);
		
		int i = r.get(vertex1);
		int j = r.get(vertex2);
		
		w[i][j] = w[j][i] = value;
	}
	
	public boolean isProbed(String vertex) {
		return pr.containsKey(vertex);
	}
	
	public void setProbed(String vertex, Integer weight) {
		if(!pr.containsKey(vertex)) probed++;
		System.out.println("Probed: " + probed);
		pr.put(vertex, weight);
	}
	
	public int probeValue(String vertex) {
		return isProbed(vertex) ? pr.get(vertex) : 0;
	}
	
	private int probeValue(int i) {
		return probeValue(rr.get(i));
	}
	
	public LinkedList<String> findBaseNodes(int size) {
		
		// find best vertex to probe
		int m = 0;
		int b = -1;
		for(int i = 0; i < cur; i++) {
			int score = probeValue(i);
			for(int j = 0; j < cur; j++) if(d[i][j] == 1) score += probeValue(j);
			for(int j = 0; j < cur; j++) if(d[i][j] == 2) score += probeValue(j);
			
			if(score > m) {
				m = score;
				b = i;
			}
		}
		
		// find all points in 2-degree away from the best point
		TreeSet<String> set = new TreeSet<String>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				if(!pr.containsKey(o2)) return 1;
				if(!pr.containsKey(o1)) return -1;
				return pr.get(o1) < pr.get(o2) ? 1 : -1;
			}
		});
		
		set.add(rr.get(b));
		for(int i = 0; i < cur; i++) {
			if(d[b][i] == 2) {
				set.add(rr.get(i));
			}
		}
		
		// get only sized points
		Iterable<String> limited = Iterables.limit(set, size);
		
		return Lists.newLinkedList(limited);
	}
	
	// returns true if a vertex is seen before
	private boolean isKnown(String vertex) {
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
	
	/**
	 * This path finding method is dangerous, if you are in a situation where the path between s and d are not known yet
	 * and you ask for a path, you will end up with an infinite loop.
	 * @param s
	 * @param d
	 * @return
	 */
	public LinkedList<String> path(String s, String d) {
		int i = r.get(s);
		int j = r.get(d);
		
		List<String> path = new LinkedList<String>();
		
		int prx = -1;
		int pre = j;
		while(i != pre) {
			String node = rr.get(pre);
			path.add(node);
			prx = pre;
			pre = p[i][pre];
			if(prx == pre) break; // things didn't go well, break.
		}
		
		return prx != pre ? new LinkedList<String>(Lists.reverse(path)) : new LinkedList<String>();
	}
	
	/**
	 * Weight between two adjacent points
	 * @param s1
	 * @param s2
	 * @return
	 */
	public int weightCost(String s1, String s2) {
		if(!isKnown(s1) || !isKnown(s2)) return MAX_INT;
		
		int i = r.get(s1);
		int j = r.get(s2);
		return w[i][j];
	}
	
	/**
	 * Edge cost between two points
	 * @param s1
	 * @param s2
	 * @return
	 */
	public int edgeCost(String s1, String s2) {
		if(!isKnown(s1) || !isKnown(s2)) return MAX_INT;
		
		int i = r.get(s1);
		int j = r.get(s2);
		return d[i][j];
	}
	
	private int edgeCostBFS(final String s1, final String s2) {
		if(!isKnown(s1) || !isKnown(s2)) return MAX_INT;
		List<Integer> path = BFS.run(g, cur, r.get(s1), new BFSPredicate() {
			public boolean isTrue(int t) {
				return r.get(s2) == t;
			}
		});
		return path == null ? MAX_INT : path.size();
	}
}
