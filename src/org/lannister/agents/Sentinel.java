package org.lannister.agents;

import java.util.List;

import org.lannister.EIManager;
import org.lannister.brain.AgentBrain;
import org.lannister.messaging.Message;

import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Sentinel extends Agent {

	public Sentinel(String name, String team, AgentBrain brain) {
		super(name, team, brain);
	}

	@Override
	protected void handlePercepts() {
		List<Percept> percepts = EIManager.getPercepts(name);
		handleCommonPercepts(percepts);
	}

	@Override
	protected void handleMessages() {
		List<Message> messages = brain.getCoordinator().popMessages(name);
		handleCommonMessages(messages);
	}

}
