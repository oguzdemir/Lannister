package org.lannister.agents;

import java.util.List;

import org.lannister.EIManager;
import org.lannister.brain.AgentBrain;
import org.lannister.graph.GraphManager;
import org.lannister.messaging.Message;
import org.lannister.util.Percepts;

import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Explorer extends Agent {

	public Explorer(String name, String team, AgentBrain brain) {
		super(name, team, brain);
	}
	
	@Override
	protected void handlePercepts() {
		List<Percept> percepts = EIManager.getPercepts(getAgentName());
		handleCommonPercepts(percepts);
		
		for(Percept percept : percepts) {
			if(percept.getName().equals(Percepts.PROBEDVERTEX)) {
				GraphManager.get().setProbed(percept.getParameters().getFirst().toString(),
														Integer.valueOf(percept.getParameters().getLast().toString()));
			}
			//TODO: if opponent saboteur is seen, run away!
		}
	}
	
	@Override
	protected void handleMessages() {
		List<Message> messages = brain.getCoordinator().popMessages(getAgentName());
	}
}
