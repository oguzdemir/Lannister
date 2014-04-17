package org.lannister.agents;
/**
author = 'Oguz Demir'
 */
public class AgentTypes {

	public final static String EXPLORER  = "Explorer";
	public final static String REPAIRER  = "Repairer";
	public final static String SABOTEUR  = "Saboteur";
	public final static String SENTINEL  = "Sentinel";
	public final static String INSPECTOR = "Inspector";
	
	public static boolean isTypeOf(String agent, String type) {
		switch(type) {
			case EXPLORER:
				return agent.contains(EXPLORER);
			case REPAIRER:
				return agent.contains(REPAIRER);
			case SABOTEUR:
				return agent.contains(SABOTEUR);
			case SENTINEL:
				return agent.contains(SENTINEL);
			case INSPECTOR:
				return agent.contains(INSPECTOR);
			default:
				return false;
		}
	}
}
