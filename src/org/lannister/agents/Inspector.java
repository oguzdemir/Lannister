package org.lannister.agents;

import java.util.List;

import org.lannister.EIManager;
import org.lannister.brain.AgentBrain;
import org.lannister.brain.InspectorBrain;
import org.lannister.graph.GraphManager;
import org.lannister.messaging.Message;
import org.lannister.messaging.Messages;
import org.lannister.util.Percepts;

import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Inspector extends Agent {

	public Inspector(String name, String team, AgentBrain brain) {
		super(name, team, brain);
	}

	@Override
	protected void handlePercepts() {
		List<Percept> percepts = EIManager.getPercepts(name);
		handleCommonPercepts(percepts);
		
		for(Percept percept : percepts) {
			if(percept.getName().equals(Percepts.INSPECTEDENTITY)) {
				String id = percept.getParameters().get(0).toString();
				String team = percept.getParameters().get(1).toString();
				String role = percept.getParameters().get(2).toString();
				String pos  = percept.getParameters().get(3).toString();
				
				brain.getPositions().put(id, pos);
				brain.getRoles().put(id, role);
				
				brain.getCoordinator().broadcast(Messages.create(name, percept));
			}
		}
	}

	@Override
	protected void handleMessages() {
		List<Message> messages = brain.getCoordinator().popMessages(name);
		handleCommonMessages(messages);
	}

}
