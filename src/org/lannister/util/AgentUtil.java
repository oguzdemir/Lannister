package org.lannister.util;

import java.util.List;

import org.lannister.agents.Agent;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
author = 'Oguz Demir'
 */
public class AgentUtil {

	/**
	 * Returns size of the specific type of agents in the agent set.
	 * @param agents
	 * @param clazz
	 * @return
	 */
	public static int sizeOf(Iterable<Agent> agents, Class clazz) {
		int size = 0;
		for(Agent agent : agents) {
			if(agent.getClass() == clazz) {
				size++;
			}
		}
		
		return size;
	}
	
	/**
	 * Returns only the specific type of agents in the agent set.
	 * @param agents
	 * @param clazz
	 * @return
	 */
	public static List<Agent> getOnly(Iterable<Agent> agents, Class clazz) {
		return Lists.newLinkedList(Iterables.filter(agents, clazz));
	}
}
