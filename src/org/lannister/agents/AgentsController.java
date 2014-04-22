package org.lannister.agents;

import java.util.HashMap;
import java.util.Map;

import org.lannister.EIManager;
import org.lannister.brain.AgentBrain;
import org.lannister.brain.AgentBrainFactory;
import org.lannister.messaging.AgentsCoordinator;
import org.lannister.util.AgentConfig;
import org.lannister.util.AgentsConfig;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public class AgentsController {

	private Map<String, Agent> agents = new HashMap<String, Agent>();
	
	public static int TEAMSIZE = 0;
	
	public AgentsController(String configFile) {
		AgentsConfig config = new AgentsConfig(configFile);
		
		for(AgentConfig conf : config.getAgentConfigs()) {
			registerAgent(conf.getName(), 
						  conf.getTeam(), 
						  conf.getEntity(),
						  conf.getClazz());
		}
		
		TEAMSIZE = config.getTeamSize();
	}
	
	public void registerAgent(String name, String team, String entity, String className) {
		try {
			Class<? extends Agent> clazz = Class.forName(className).asSubclass(Agent.class);
			AgentBrain brain 			 = AgentBrainFactory.get().createBrain(name, clazz); 
			Agent agent 	 			 = (Agent) clazz.getDeclaredConstructor(String.class, String.class, AgentBrain.class).newInstance(name, team, brain);
			agents.put(name, agent);
			
			// add it to Environment interface
			EIManager.register(name);
			EIManager.associate(name, entity);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void registerCoordinator(AgentsCoordinator coordinator) {
		for(Agent agent : agents.values()) {
			agent.setCoordinator(coordinator);
		}
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
