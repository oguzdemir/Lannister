package org.lannister.brain;

import java.util.LinkedList;

import org.lannister.graph.GraphManager;

/**
author = 'Oguz Demir'
 */
public class AgentPlan {
	
	protected enum PlanType {
		BESTSCORE, EXPLORING, SURVEYING, REPAIRING, PROBING
	}
	
	/**
	 * Type of the plan
	 */
	protected PlanType type;
	
	/**
	 * final target in the path
	 */
	private String target;
	
	/**
	 * target agent at the end of the path (optional)
	 */
	private String targetAgent;
	
	/**
	 * Path to target node (includes target also)
	 */
	private LinkedList<String> path;
	
	private boolean actionDone = false;
	
	public AgentPlan(String current, String target, PlanType type) {
		this.target = target;
		this.type   = type;
		this.path = GraphManager.get().path(current, target);
	}
	
	public AgentPlan(String current, String target, String targetAgent, PlanType type) {
		this(current, target, type);
		this.targetAgent = targetAgent;
	}
	
	public AgentPlan(PlanType type) {
		this.path 		= new LinkedList<String>();
		this.type 		= type;
		this.actionDone = true;
	}
	
	public String next() {
		return !path.isEmpty() ? path.getFirst() : null;
	}
	
	/**
	 * update path after a succesful GOTO action
	 */
	public void update() {
		if(!isCompleted())
			path.removeFirst();
	}
	
	public boolean isCompleted() {
		return path.isEmpty();
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getTargetAgent() {
		return targetAgent;
	}
	
	public PlanType getType() {
		return type;
	}
	
	public boolean isActionDone() {
		return actionDone;
	}
	
	public void actionDone() {
		actionDone = true;
	}
}
