package org.lannister.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lannister.agents.Agent;
import org.lannister.agents.Explorer;
import org.lannister.graph.GraphManager;
import org.lannister.util.AgentUtil;
import org.lannister.util.Percepts;

/**
author = 'Oguz Demir'
 */
public class AgentsCoordinator {

	private Map<String, Agent>		    agents;
	private Map<String, String> 		targets  = new HashMap<String, String>(); 
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
	public synchronized void broadcast(Message message) {
		for(Map.Entry<String, List<Message>> entry : messages.entrySet()) {
			String agentName 			= entry.getKey();
			
			if(!agentName.equals(message.getFrom())) {
				send(message, agentName);
			}
		}
	}
	
	/**
	 * Broadcasts a message to other agents who has type clazz. 
	 * @param message
	 * @param clazz
	 */
	public synchronized void broadcast(Message message, Class clazz) {
		for(Map.Entry<String, List<Message>> entry : messages.entrySet()) {
			String agentName			= entry.getKey();
			if(agents.get(agentName).getClass() == clazz && !agentName.equals(message.getFrom())) {
				send(message, agentName);
			}
		}
	}
	
	/**
	 * Sends a message to another agent
	 * @param message
	 * @param agentName
	 */
	public synchronized void send(Message message, String agentName) {
		if(messages.containsKey(agentName)) {
			messages.get(agentName).add(message);
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
	
	/**
	 * Finds base nodes for Explorer agents.
	 */
	public void findBase() {
		int size = AgentUtil.sizeOf(agents.values(), Explorer.class);
		
		distributeBaseNodes(GraphManager.getBaseNodes(size));
	}
	
	/**
	 * Sends base node info to all Explorer agents.
	 * @param nodes
	 */
	private void distributeBaseNodes(List<String> nodes) {
		List<Agent> explorers = AgentUtil.getOnly(agents.values(), Explorer.class);
		
		if(explorers.size() == nodes.size()) {
			for(int i = 0; i < explorers.size(); i++) {
				String agentName = explorers.get(i).getAgentName();
				String node		 = nodes.get(i);
				
				send(Messages.create(null, Percepts.BASE, node), agentName);
			}
		}
	}
}
