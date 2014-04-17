package org.lannister;

import org.lannister.agents.AgentsController;
import org.lannister.agents.Explorer;
import org.lannister.agents.Repairer;

public class Main {

	private static AgentsController agentsController;
	
	private static String[] explorerAgents =  { "LannisterExplorer1", "LannisterExplorer2",
									   			"LannisterExplorer3", "LannisterExplorer4",
									   			"LannisterExplorer5", "LannisterExplorer6" };
	private static String[] repairerAgents =  { "LannisterRepairer1" , "LannisterRepairer2",
												"LannisterRepairer3", "LannisterRepairer4",
												"LannisterRepairer5", "LannisterRepairer6" };
	
	public static void main(String[] args) {
		
		agentsController = new AgentsController();
		
		for(String agent : explorerAgents)
			agentsController.registerAgent(agent, Explorer.class);
		for(String agent : repairerAgents)
			agentsController.registerAgent(agent, Repairer.class);
		
		// enable coordinating between agents
		agentsController.enableCoordinating();
		
		// start agents
		agentsController.start();
		
	}

}
