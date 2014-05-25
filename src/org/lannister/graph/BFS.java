package org.lannister.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Lists;

/**
author = 'Oguz Demir'
 */
public class BFS {

	private static int dad[];
	
	
	/**
	 * Returns a path to an end point using BFS method. The end point is the closest one who holds the predicate.
	 * @param g
	 * @param size
	 * @param start
	 * @param predicate
	 * @return
	 */
	public static LinkedList<Integer> run(int[][] g, int size, int start, BFSPredicate predicate) {
		dad = new int[size];
		
		int            end = start;
		boolean		   hld = false;
		Queue<Integer>   q = new LinkedList<Integer>();
		Set<Integer>     b = new HashSet<Integer>();	
		q.add(start);
		
		while(!q.isEmpty()) {
			int t = q.remove();
			b.add(t);
			if(predicate.isTrue(t)) {
				end = t;
				hld = true;
				break;
			}
			
			for(int j = 0; j < size; j++) {
				if(g[t][j] == 1 && !b.contains(j)) {
					q.add(j);
					dad[j] = t;
				}
			}
		}
		LinkedList<Integer> p = new LinkedList<Integer>();
		while(end != start) {
			p.add(end);
			end = dad[end];
		}
		
		return hld ? new LinkedList<Integer>(Lists.reverse(p)) : null;
	}
	
	protected interface BFSPredicate {
		
		boolean isTrue(int t);
	}
}
