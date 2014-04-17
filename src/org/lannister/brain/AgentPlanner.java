package org.lannister.brain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lannister.graph.GraphManager;

/**
author = 'Oguz Demir'
 */
public class AgentPlanner {
	
	private static List<String> exploringTargets = new LinkedList<String>();
	private static List<String> surveyingTargets = new LinkedList<String>();
	private static List<String> probingTargets	 = new LinkedList<String>();
	
	private static Map<String, String> bases     = new HashMap<String, String>(); 
	
	private static AgentPlan plan(String current, String target) {
		return new AgentPlan(current, target);
	}
	
	public static AgentPlan emptyPlan() {
		return new AgentPlan();
	}
	
	public static AgentPlan newExploringPlan(String current) {
		String target = findUnvisitedTarget(current);
		return target == null ? emptyPlan() : plan(current, target);
	}
	
	public static AgentPlan newProbingPlan(String current) {
		String target = findUnprobedTarget(current);
		return target == null ? emptyPlan() : plan(current, target);
	}
	
	public static AgentPlan newSurveyingPlan(String current) {
		String target = findUnsurveyedTarget(current);
		return target == null ? emptyPlan() : plan(current, target);
	}
	
	public static AgentPlan newBestScoringPlan(String current, String agent) {
		String target; 
		target = bases.containsKey(agent) ? bases.get(agent) : bases.put(agent, GraphManager.grabBaseNode());
		target = target == null ? bases.get(agent) : target;
		return target == null ? emptyPlan() : plan(current, target);
	}
	
	public static AgentPlan newCustomPlan(String current, String target) {
		return target == null ? emptyPlan() : plan(current, target);
	}
	
	public static void abortExploringPlan(String target) {
		exploringTargets.remove(target);
	}
	
	public static void abortProbingPlan(String target) {
		probingTargets.remove(target);
	}
	
	public static void abortSurveyingPlan(String target) {
		surveyingTargets.remove(target);
	}
	
	private static String findUnvisitedTarget(String current) {
		String target = GraphManager.get().getUnvisited(current, exploringTargets);
		if(target != null) exploringTargets.add(target);
		return target;
	}
	
	private static String findUnprobedTarget(String current) {
		String target = GraphManager.get().getUnprobed(current, probingTargets);
		if(target != null) probingTargets.add(target);
		return target;
	}
	
	private static String findUnsurveyedTarget(String current) {
		String target = GraphManager.get().getUnsurveyed(current, surveyingTargets);
		if(target != null) surveyingTargets.add(target);
		return target;
	}
}
