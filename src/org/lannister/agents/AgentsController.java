package org.lannister.agents;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.lannister.EIManager;
import org.lannister.brain.AgentBrain;
import org.lannister.brain.AgentBrainFactory;
import org.lannister.graph.GraphManager;
import org.lannister.messaging.AgentsCoordinator;

import eis.iilang.Action;

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
		add("connectionA9");
		add("connectionA10");
		add("connectionA11");
		add("connectionA12");
	}};
	
	public AgentsController() {
		agents = new TreeMap<String, Agent>();
	}
	
	public void registerAgent(String name, Class<? extends Agent> clazz) {
		try {
			AgentBrain brain = AgentBrainFactory.get().createBrain(name, clazz); 
			Agent agent 	 = (Agent) clazz.getDeclaredConstructor(String.class, AgentBrain.class).newInstance(name, brain);
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
	
	// Start all agents to execute
	public void start() {
		EIManager.start();
		
		Action action   = null;
		boolean running = EIManager.isRunning();
		
		while(running) {
			for(Agent agent: agents.values()) {
				action = agent.perform();
				if(action != null) EIManager.act(agent.getAgentName(), action);
			}
			running = EIManager.isRunning();
		}
	}
	
	public void enableCoordinating() {
		registerCoordinator(new AgentsCoordinator(agents));
	}
}
