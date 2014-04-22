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
			if(percept.getName().equals(Percepts.VISIBLEENTITY)) {
				String id  		= percept.getParameters().get(0).toString();
				String pos 		= percept.getParameters().get(1).toString();
				String team		= percept.getParameters().get(2).toString();
				String status 	= percept.getParameters().get(3).toString();
				
				if(!team.equals(this.team) 												// not in my team
						&& GraphManager.get().edgeCost(brain.getPosition(), pos) <= 1 	// and max 1 edge distance
						&& !brain.getRoles().containsKey(id)) {							// and not inspected before
					InspectorBrain inspectorBrain = (InspectorBrain) brain;
					inspectorBrain.setEnemy(id);
				}
			}
			else if(percept.getName().equals(Percepts.INSPECTEDENTITY)) {
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
