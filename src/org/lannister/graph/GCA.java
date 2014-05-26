package org.lannister.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Ordering;

/**
author = 'Oguz Demir'
 */
public class GCA {

	private static boolean colored(int[][] g, int size, int i, List<Integer> positions) {
		boolean colored = false;
		
		colored = colored == false ? dominatesNode(g, size, i, positions) 	   : colored;
		colored = colored == false ? dominatesNeighbors(g, size, i, positions) : colored;
		
		return colored;
	}
	
	private static boolean dominatesNode(int[][] g, int size, int i, List<Integer> positions) {
		return positions.contains(i);
	}
	
	private static boolean dominatesNeighbors(int[][] g, int size, int i, List<Integer> positions) {
		int nc = 0;
		int nd = 0;
		for(int j = 0; j < size; j++) { 
			nc += g[i][j];
			nd += positions.contains(j) ? 1 : 0;
		}
		return nd * 1.0 >= nc / 2.0;
	}
	
	public static int score(int[][] g, int size, List<Integer> positions, Map<Integer, Integer> probeValues) {
		int score = 0;
		for(int i = 0; i < size; i++) {
			score += colored(g, size, i, positions) ? probeValues.get(i) : 0;
		}
		return score;
	}
	
	private static List<Integer> uncoloredNeighbors(int[][] g, int size, int i, List<Integer> positions) {
		List<Integer> list = new ArrayList<Integer>();
		for(int j = 0; j < size; j++) {
			if(g[i][j] == 1 && !colored(g, size, j, positions)) list.add(j);
		}
		return list;
	}
	
	public static int bestUncoloredNeighbor(int[][] g, int size, int i, List<Integer> positions, final Map<Integer, Integer> probeValues) {
		return new Ordering<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
				Integer pa = !probeValues.containsKey(a) ? 0 : probeValues.get(a);
				Integer pb = !probeValues.containsKey(b) ? 0 : probeValues.get(b);
				return pa.compareTo(pb);
			}	
		}.max(uncoloredNeighbors(g, size, i, positions));
	}
}
