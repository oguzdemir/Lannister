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
		
		for(Percept percept : percepts) {
			if(percept.getName().equals(Percepts.VISIBLEENTITY)) {
				String id  		= percept.getParameters().get(0).toString();
				String pos 		= percept.getParameters().get(1).toString();
				String team		= percept.getParameters().get(2).toString();
				String status 	= percept.getParameters().get(3).toString();
				
				if(team.equals(this.team) 													// in my team
						&& status.equals("disabled") 										// and disabled
						&& GraphManager.get().edgeCost(brain.getPosition(), pos) <= 1) {	// and closer than 1 edge, then help
					RepairerBrain repairerBrain = (RepairerBrain) brain;
					repairerBrain.setFriend(id);
				}
				
				if(!team.equals(this.team)													// in opponent team 
						&& status.equals("normal") 											// and has normal status
						&& GraphManager.get().edgeCost(brain.getPosition(), pos) <= 1 		// and closer than 1 edge
						&& (brain.getRoles().get(id) == null								// and type is either not known or saboteur, then parry 
							|| brain.getRoles().get(id).equals(AgentTypes.SABOTEUR))) {	
					//parry
					RepairerBrain repairerBrain = (RepairerBrain) brain;
					repairerBrain.setEnemy(id);
				}
			}
		}
	}

	@Override
	protected void handleMessages() {
		List<Message> messages = brain.getCoordinator().popMessages(getAgentName());
		handleCommonMessages(messages);
		
		for(Message message : messages) {
			String from 	= message.getFrom();
			Percept percept = message.getPercept();
			if(percept.getName().equals(Percepts.HELP)) {
				RepairerBrain repairerBrain = (RepairerBrain) brain;
				repairerBrain.handleHelpCall(from);
			}
		}
	}

}
