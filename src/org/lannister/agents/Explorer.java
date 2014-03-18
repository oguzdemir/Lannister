package org.lannister.agents;

import java.util.LinkedList;
import java.util.List;

import org.lannister.EIManager;
import org.lannister.graph.GraphManager;
import org.lannister.messaging.Message;
import org.lannister.util.ActionResults;
import org.lannister.util.Actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Explorer extends Agent {

	private boolean newStep = false;
	
	private LinkedList<String> path 		= new LinkedList<String>();
	private String next;
	
	public Explorer(String name) {
		super(name);
	}
	
	private void handlePercepts() {
		List<Percept> percepts = EIManager.getPercepts(getAgentName());
		
		for(Percept percept : percepts) {
			if(percept.getName().equals("step")) {
				int step = Integer.valueOf(percept.getParameters().get(0).toString());
				if(getCurrentStep() < step) {
					newStep = true;
					setCurrentStep(step);
				}
				else {
					newStep = false;
				}
			}
			else if(percept.getName().equals("position")) {
				String pos = percept.getParameters().get(0).toString();
				setPosition(pos);
				GraphManager.get(getAgentName()).setVisited(pos);
				messenger.broadcast(new Message(getAgentName(), percept));
			}
			else if(percept.getName().equals("visibleVertex")) {
				GraphManager.get(getAgentName()).addVertex(percept.getParameters().get(0).toString());
				messenger.broadcast(new Message(getAgentName(), percept));
			}
			else if(percept.getName().equals("visibleEdge")) {
				GraphManager.get(getAgentName()).addEdge(percept.getParameters().getFirst().toString(),
										   percept.getParameters().getLast().toString(), 1);
				messenger.broadcast(new Message(getAgentName(), percept));
			}
			else if(percept.getName().equals("energy")) {
				setEnergy(Integer.valueOf(percept.getParameters().get(0).toString()));
			}
			else if(percept.getName().equals("lastAction")) {
				setLastAction(percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals("lastActionResult")) {
				setLastActionResult(percept.getParameters().getFirst().toString());
			}
		}
	}
	
	private void handleMessages() {
		List<Message> messages = messenger.popMessages(getAgentName());
		
		for(Message message : messages) {
			String fromAgent = message.getFrom();
			Percept percept  = message.getPercept();
			
			if(percept.getName().equals("visibleVertex")) {
				GraphManager.get(getAgentName()).addVertex(percept.getParameters().get(0).toString());
			} 
			else if(percept.getName().equals("visibleEdge")) {
				GraphManager.get(getAgentName()).addEdge(percept.getParameters().getFirst().toString(),
						   percept.getParameters().getLast().toString(), 1);
			}
			else if(percept.getName().equals("position")) {
				GraphManager.get(getAgentName()).setVisited(percept.getParameters().get(0).toString());
			}
		}
	}
	
	@Override
	public Action perform() {
		handlePercepts();
		handleMessages();
		if(newStep) {
			info();
			
			// update distance matrix
			GraphManager.get(getAgentName()).aps();
			
			print("Total perceived vertex size: " + GraphManager.get(getAgentName()).size());
			print("Total visited vertex size: " + GraphManager.get(getAgentName()).visited());
			
			handleFailedGotoAction();
			
			Action action;
			
			action = planRecharge();
			if(action != null) {
				print("Recharging..");
				return action;
			}
			
			action = planGoto();
			if(action != null) {
				print("Goto..");
				return action;
			}
			
			return new Action("skip");
		}
		return null;
	}
	
	// returns a valid recharge action if recharging is necessary, otherwise returns null
	private Action planRecharge() {
		if(getLastActionResult().equals(ActionResults.NORESOURCE) || 
				getEnergy() <= THRESHOLD_ENERGY) {
			return new Action(Actions.RECHARGE);
		}
		return null;
	}
	
	// returns a valid goto action if planning to go to a new vertex, otherwise returns null
	private Action planGoto() {
		updatePath();
		
		if(!path.isEmpty()) {
			next = path.remove(0);
			return new Action("goto", new Identifier(next));
		}
		
		return null;
	}
	
	//keep path always updated
	private void updatePath() {
		if(path.isEmpty()) {
			print("Updating path..");
			 
			String target = GraphManager.get(getAgentName()).getUnvisited(getPosition());
			path = GraphManager.get(getAgentName()).path(getPosition(), target);
			print(path);
		}
	}
	
	// if last goto action failed, try again.
	private void handleFailedGotoAction() {
		if(getLastAction().equals(Actions.GOTO) && 
				!getLastActionResult().equals(ActionResults.SUCCESS)) {
			path.addFirst(next);
		}
	}
	
}
