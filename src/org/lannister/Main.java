package org.lannister;

import org.lannister.agents.AgentsController;
import org.lannister.agents.Explorer;

public class Main {

	private static AgentsController agentsController;
	
	private static String[] agents = { "LannisterExplorer1", "LannisterExplorer2",
									   "LannisterExplorer3", "LannisterExplorer4",
									   "LannisterExplorer5", "LannisterExplorer6",
									   "LannisterExplorer7", "LannisterExplorer8"}; 
	
	public static void main(String[] args) {
		
		agentsController = new AgentsController();
		
		for(String agent : agents)
			agentsController.registerAgent(agent, Explorer.class);
		
		// enable coordinating between agents
		agentsController.enableCoordinating();
		
		// start agents
		agentsController.start();
		
	}

}
