package org.lannister.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lannister.agents.Agent;

/**
author = 'Oguz Demir'
 */
public class AgentsMessenger {

	private Map<String, List<Message>> 	messages = new HashMap<String, List<Message>>();
	
	public AgentsMessenger(Map<String, Agent> agents) {
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
}
