package org.lannister.brain;

import org.lannister.agents.Agent;

/**
author = 'Oguz Demir'
 */
public class AgentBrainFactory {

	private static String prefix = "org.lannister.brain.";
	private static String suffix = "Brain";
	
	private static AgentBrainFactory factory;
	
	public static AgentBrainFactory get() {
		return factory == null ? factory = new AgentBrainFactory() : factory;
	}
	
	public AgentBrain createBrain(String agentName, Class<? extends Agent> clazz) {
		AgentBrain brain = null;
		try {
			brain = (AgentBrain) Class.forName(prefix + clazz.getSimpleName() + suffix).getDeclaredConstructor(String.class).newInstance(agentName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return brain;
	}
}
