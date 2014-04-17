package org.lannister.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lannister.agents.Agent;
import org.lannister.util.AgentUtil;

/**
author = 'Oguz Demir'
 */
public class AgentsCoordinator {

	private Map<String, Agent>		    agents;
	private Map<String, List<Message>> 	messages = new HashMap<String, List<Message>>();
	
	public AgentsCoordinator(Map<String, Agent> agents) {
		this.agents = agents;
		
		for(String agentName : agents.keySet()) {
			messages.put(agentName, new ArrayList<Message>());
		}
 	}
	
	/**
	 * Broadcasts a message to all other agents.
	 * @param from agent where message comes from
	 * @param message the percept information to be distributed to other agents
	 */
	public void broadcast(Message message) {
		for(Agent agent : agents.values())
			if(!agent.getAgentName().equals(message.getFrom())) {
				send(message, agent.getAgentName());
			}
	}
	
	/**
	 * Broadcasts a message to other agents who has type clazz. 
	 * @param message
	 * @param clazz
	 */
	public void broadcast(Message message, Class<? extends Agent> clazz) {
		for(Agent agent : AgentUtil.getOnly(agents.values(), clazz)) {
			send(message, agent.getAgentName());
		}
	}
	
	/**
	 * Sends a message to another agent
	 * @param message
	 * @param agentName
	 */
	public void send(Message message, String agentName) {
		if(messages.containsKey(agentName)) {
			messages.get(agentName).add(message);
		}
	}
	
	/**
	 * Returns messages for the agent, and clears the list for new messages.
	 * @param agentName
	 * @return
	 */
	public List<Message> popMessages(String agentName) {
		List<Message> m = new ArrayList<Message>(messages.get(agentName));
		messages.get(agentName).clear();
		
		return m;
	}
}
