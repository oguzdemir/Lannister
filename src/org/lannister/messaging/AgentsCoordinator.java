package org.lannister.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lannister.agents.Agent;

/**
author = 'Oguz Demir'
 */
public class AgentsCoordinator {

	private Map<String, String> 		targets  = new HashMap<String, String>(); 
	private Map<String, List<Message>> 	messages = new HashMap<String, List<Message>>();
	
	public AgentsCoordinator(Map<String, Agent> agents) {
		for(String agentName : agents.keySet()) {
			messages.put(agentName, new ArrayList<Message>());
		}
	}
	
	/**
	 * Broadcasts a message from fromAgent to other agents.
	 * @param from agent where message comes from
	 * @param message the percept information to be distributed to other agents
	 */
	public synchronized void broadcast(Message message) {
		for(Map.Entry<String, List<Message>> entry : messages.entrySet()) {
			String agentName 			= entry.getKey();
			List<Message> agentMessages = entry.getValue();
			
			if(!agentName.equals(message.getFrom())) {
				agentMessages.add(message);
			}
		}
	}
	
	/**
	 * Returns messages for the agent, and clears the list for new messages.
	 * @param agentName
	 * @return
	 */
	public synchronized List<Message> popMessages(String agentName) {
		List<Message> m = new ArrayList<Message>(messages.get(agentName));
		messages.get(agentName).clear();
		
		return m;
	}
	
	/**
	 * Updates target list for the agent.
	 * @param agentName
	 * @param target
	 */
	public synchronized boolean registerTarget(String agentName, String target) {
		if(!targets.containsValue(target)) {
			targets.put(agentName, target);
			return true;
		}
		return false;
	}
	
	/**
	 * Returns current targets.
	 * @return
	 */
	public Collection<String> getTargets() {
		return targets.values();
	}
	
}
