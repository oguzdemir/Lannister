package org.lannister.brain;

import java.util.LinkedList;

import org.lannister.graph.GraphManager;

/**
author = 'Oguz Demir'
 */
public class AgentPlan {
	
	/**
	 * final target in the path
	 */
	private String target;
	
	/**
	 * Path to target node (includes target also)
	 */
	private LinkedList<String> path;
	
	public AgentPlan(String current, String target) {
		this.target = target;
		path = GraphManager.get().path(current, target);
	}
	
	public AgentPlan() {
		path = new LinkedList<String>();
	}
	
	public String next() {
		return !path.isEmpty() ? path.getFirst() : null;
	}
	
	/**
	 * update path after a succesful GOTO action
	 */
	public void update() {
		path.removeFirst();
	}
	
	public boolean isCompleted() {
		return path.isEmpty();
	}
	
	public String getTarget() {
		return target;
	}
}
