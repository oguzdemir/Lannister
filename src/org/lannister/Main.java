package org.lannister;

import org.lannister.agents.AgentsController;
import org.lannister.agents.Explorer;
import org.lannister.agents.Repairer;

public class Main {

	private static String CONFIGFILE = "javaagentsconfig.xml";
	
	public static void main(String[] args) {
		
		// initialize controller
		AgentsController agentsController = new AgentsController(CONFIGFILE);
		
		// enable coordinating between agents
		agentsController.enableCoordinating();
		
		// start agents
		agentsController.start();
		
	}

}
