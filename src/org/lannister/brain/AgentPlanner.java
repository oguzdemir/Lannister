package org.lannister.brain;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lannister.brain.AgentPlan.PlanType;
import org.lannister.graph.GraphManager;

/**
author = 'Oguz Demir'
 */
public class AgentPlanner {
	
	private static List<String> exploringTargets = new LinkedList<String>();
	private static List<String> surveyingTargets = new LinkedList<String>();
	private static List<String> probingTargets	 = new LinkedList<String>();
	private static List<String> repairingTargets = new LinkedList<String>();
	
	private static Map<String, String> bases     = new HashMap<String, String>(); 
	
	private static AgentPlan plan(String current, String target, PlanType type) {
		return new AgentPlan(current, target, type);
	}
	
	private static AgentPlan plan(String current, String target, String targetAgent, PlanType type) {
		return new AgentPlan(current, target, targetAgent, type);
	}
	
	public static AgentPlan emptyPlan() {
		return new AgentPlan();
	}
	
	public static AgentPlan newExploringPlan(String current) {
		String target = findUnvisitedTarget(current);
		return target == null ? emptyPlan() : plan(current, target, PlanType.EXPLORING);
	}
	
	public static AgentPlan newProbingPlan(String current) {
		String target = findUnprobedTarget(current);
		return target == null ? emptyPlan() : plan(current, target, PlanType.PROBING);
	}
	
	public static AgentPlan newSurveyingPlan(String current) {
		String target = findUnsurveyedTarget(current);
		return target == null ? emptyPlan() : plan(current, target, PlanType.SURVEYING);
	}
	
	public static AgentPlan newBestScoringPlan(String current, String agent) {
		String target; 
		target = bases.containsKey(agent) ? bases.get(agent) : bases.put(agent, GraphManager.grabBaseNode());
		target = target == null ? bases.get(agent) : target;
		System.out.println("Base found for " + agent + ": " + target);
		return target == null ? emptyPlan() : plan(current, target, PlanType.BESTSCORE);
	}
	
	public static AgentPlan newCustomPlan(String current, String target) {
		return target == null ? emptyPlan() : plan(current, target, PlanType.CUSTOM);
	}
	
	public static AgentPlan newRepairingPlan(String current, Map<String, String> disabledAgentPositions) {
		String target = findUnrepairedTarget(current, disabledAgentPositions.values());
		String targetAgent = disabledAgentPositions.get(target);
		return target == null ? emptyPlan() : plan(current, target, targetAgent, PlanType.REPAIRING);
	}
	
	public static void abortPlan(AgentPlan plan) {
		switch(plan.type) {
			case EXPLORING:
				abortExploringPlan(plan.getTarget());
				break;
			case SURVEYING:
				abortSurveyingPlan(plan.getTarget());
				break;
			case REPAIRING:
				abortRepairingPlan(plan.getTarget());
				break;
			case PROBING:
				abortProbingPlan(plan.getTarget());
				break;
		}
	}
	
	private static void abortExploringPlan(String target) {
		exploringTargets.remove(target);
	}
	
	private static void abortProbingPlan(String target) {
		probingTargets.remove(target);
	}
	
	private static void abortSurveyingPlan(String target) {
		surveyingTargets.remove(target);
	}
	
	private static void abortRepairingPlan(String target) {
		repairingTargets.remove(target);
	}
	
	private static String findUnrepairedTarget(String current, Collection<String> disabledAgentPositions) {
		//disabledAgentPositions.removeAll(repairingTargets);
		String target = GraphManager.get().getClosest(current, disabledAgentPositions);
		if(target != null) repairingTargets.add(target);
		return target;
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
