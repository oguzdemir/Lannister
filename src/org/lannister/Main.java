package org.lannister;

import org.lannister.agents.AgentsController;
import org.lannister.agents.Explorer;

public class Main {

	private static AgentsController agentsController;
	
	private static String[] agents = { "LannisterExplorer1", "LannisterExplorer2" }; 
	
	public static void main(String[] args) {
		
		agentsController = new AgentsController();
		
		for(String agent : agents)
			agentsController.registerAgent(agent, Explorer.class);
		
		// enable messaging between agents
		agentsController.enableMessaging();
		
		// start agents
		agentsController.start();
		
	}

}
