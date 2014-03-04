package org.lannister.agents;

import java.util.HashMap;
import java.util.Map;

import org.lannister.EIManager;

import eis.AgentListener;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class AgentsController implements AgentListener {

	private Map<String, Agent> agents;
	
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
			EIManager.attachListener(name, this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handlePercept(String agent, Percept percept) {
		agents.get(agent).handlePercept(percept);
	}
	
	// TODO: Implement, parse from xml.
	private String getConnection() {
		return "connectionA1";
	}
	
	// Start all Agents to execute
	public void start() {
		EIManager.start();
		
		for(Agent agent : agents.values()) {
			agent.start();
		}
	}
	
	public AgentsMessager createMessager() {
		return new AgentsMessager(agents);
	}
}
