package org.lannister.agents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.lannister.EIManager;
import org.lannister.messaging.AgentsCoordinator;

/**
author = 'Oguz Demir'
 */
public class AgentsController {

	private Map<String, Agent> agents;
	
	private LinkedList<String> connections = new LinkedList<String>() {{
		add("connectionA1");
		add("connectionA2");
		add("connectionA3");
		add("connectionA4");
		add("connectionA5");
		add("connectionA6");
		add("connectionA7");
		add("connectionA8");
	}};
	
	public AgentsController() {
		agents = new HashMap<String, Agent>();
	}
	
	public void registerAgent(String name, Class clazz) {
		try {
			Agent agent = (Agent) clazz.getDeclaredConstructor(String.class).newInstance(name);
			agents.put(name, agent);
			
			// add it to Environment interface
			EIManager.register(name);
			EIManager.associate(name, getConnection());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void registerCoordinator(AgentsCoordinator coordinator) {
		for(Agent agent : agents.values()) {
			agent.setCoordinator(coordinator);
		}
	}
	
	// TODO: Implement, parse from xml.
	private String getConnection() {
		return connections.removeFirst();
	}
	
	// Start all Agents to execute
	public void start() {
		EIManager.start();
		
		for(Agent agent : agents.values()) {
			agent.start();
		}
	}
	
	public void enableCoordinating() {
		registerCoordinator(new AgentsCoordinator(agents));
	}
}
