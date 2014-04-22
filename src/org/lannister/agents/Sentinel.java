package org.lannister.agents;

import java.util.List;

import org.lannister.EIManager;
import org.lannister.brain.AgentBrain;
import org.lannister.brain.SentinelBrain;
import org.lannister.graph.GraphManager;
import org.lannister.messaging.Message;
import org.lannister.util.Percepts;

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
		
		for(Percept percept : percepts) {
			if(percept.getName().equals(Percepts.VISIBLEENTITY)) {
				String id  		= percept.getParameters().get(0).toString();
				String pos 		= percept.getParameters().get(1).toString();
				String team		= percept.getParameters().get(2).toString();
				String status 	= percept.getParameters().get(3).toString();
			
				if(!team.equals(this.team) 
						&& status.equals("normal") 
						&& GraphManager.get().edgeCost(brain.getPosition(), pos) <= 1
						&& (brain.getRoles().get(id) == null							 
							|| brain.getRoles().get(id).equals(AgentTypes.SABOTEUR))) {
					//parry
					SentinelBrain sentinelBrain = (SentinelBrain) brain;
					sentinelBrain.setEnemy(id);
				}
			}
		}
	}

	@Override
	protected void handleMessages() {
		List<Message> messages = brain.getCoordinator().popMessages(name);
		handleCommonMessages(messages);
	}

}
