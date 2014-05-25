package org.lannister.agents;

import java.util.List;

import org.lannister.EIManager;
import org.lannister.brain.AgentBrain;
import org.lannister.brain.RepairerBrain;
import org.lannister.graph.GraphManager;
import org.lannister.messaging.Message;
import org.lannister.util.Percepts;

import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Repairer extends Agent {

	public Repairer(String name, String team, AgentBrain brain) {
		super(name, team, brain);
	}

	@Override
	protected void handlePercepts() {
		List<Percept> percepts = EIManager.getPercepts(getAgentName());
		handleCommonPercepts(percepts);
	}

	@Override
	protected void handleMessages() {
		List<Message> messages = brain.getCoordinator().popMessages(getAgentName());
		handleCommonMessages(messages);
	}

}
