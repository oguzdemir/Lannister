package org.lannister.agents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	// target nodes of other agents
	private Map<String, String> targets = new HashMap<String, String>();
	
	// path to the target node
	private LinkedList<String> path = new LinkedList<String>();
	
	// next node to visit in order to reach target node
	private String next;
	
	public Explorer(String name) {
		super(name);
	}
	
	// handles percepts that agents sensors received
	private void handlePercepts() {
		List<Percept> percepts = EIManager.getPercepts(getAgentName());
		
		for(Percept percept : percepts) {
			if(percept.getName().equals("step")) {
				int step = Integer.valueOf(percept.getParameters().getFirst().toString());
				if(getCurrentStep() < step) {
					newStep = true;
					setCurrentStep(step);
				}
				else {
					newStep = false;
				}
			}
			else if(percept.getName().equals("position")) {
				String pos = percept.getParameters().getFirst().toString();
				setPosition(pos);
				GraphManager.get(getAgentName()).setVisited(pos);
				messenger.broadcast(new Message(getAgentName(), percept));
			}
			else if(percept.getName().equals("visibleVertex")) {
				GraphManager.get(getAgentName()).addVertex(percept.getParameters().getFirst().toString());
				messenger.broadcast(new Message(getAgentName(), percept));
			}
			else if(percept.getName().equals("visibleEdge")) {
				GraphManager.get(getAgentName()).addEdge(percept.getParameters().getFirst().toString(),
										   percept.getParameters().getLast().toString(), 1);
				messenger.broadcast(new Message(getAgentName(), percept));
			}
			else if(percept.getName().equals("energy")) {
				setEnergy(Integer.valueOf(percept.getParameters().getFirst().toString()));
			}
			else if(percept.getName().equals("lastAction")) {
				setLastAction(percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals("lastActionResult")) {
				setLastActionResult(percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals("probedVertex")) {
				GraphManager.get(getAgentName()).setProbed(percept.getParameters().getFirst().toString(),
														Integer.valueOf(percept.getParameters().getLast().toString()));
				messenger.broadcast(new Message(getAgentName(), percept));
			}
		}
	}
	
	// handles messages received from other agents
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
			else if(percept.getName().equals("probedVertex")) {
				GraphManager.get(getAgentName()).setProbed(percept.getParameters().getFirst().toString(),
						Integer.valueOf(percept.getParameters().getLast().toString()));
			}
			else if(percept.getName().equals("goingTo")) {
				targets.put(fromAgent, percept.getParameters().getFirst().toString());
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
			
			// we are assuming that a previous goto action should succeed, 
			// but if somehow we hit an error in goto step, we need to repair the path
			handleFailedGotoAction();
			
			Action action;
			
			action = planRecharge();
			if(action != null) {
				print("Recharging..");
				return action;
			}
			
			action = planProbe();
			if(action != null) {
				print("Probing..");
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
	
	private Action planProbe() {
		if(shouldProbe()) {
			return new Action(Actions.PROBE);
		}
		return null;
	}
	
	// probe if:
	// 1 - On the target node which is not probed
	private boolean shouldProbe() {
		return path.isEmpty() && !GraphManager.get(getAgentName()).isProbed(getPosition());
	}
	
	
	private Action planRecharge() {
		if(shouldRecharge()) {
			return new Action(Actions.RECHARGE);
		}
		return null;
	}
	
	// recharge if:
	// 1 - Goto action failed
	// 2 - Probe action failed
	// 3 - Energy is below minimum allowed and needs to pass an edge in the next step
	private boolean shouldRecharge() {
		return (getLastAction().equals(Actions.GOTO) && getLastActionResult().equals(ActionResults.NORESOURCE)) ||
				(getLastAction().equals(Actions.PROBE) && getLastActionResult().equals(ActionResults.NORESOURCE)) ||
				(getEnergy() <= THRESHOLD_ENERGY && !path.isEmpty());
	}
	
	private Action planGoto() {
		updatePath();
		
		if(shouldGoto()) {
			next = path.remove(0);
			return new Action("goto", new Identifier(next));
		}
		
		return null;
	}
	
	// goto if:
	// 1 - There is a valid path to go to a target node
	private boolean shouldGoto() {
		return !path.isEmpty();
	}
	
	//keep path always updated
	private void updatePath() {
		if(path.isEmpty()) {
			print("Updating path..");
			
			// new target found
			String target = GraphManager.get(getAgentName()).getUnvisited(getPosition(), targets.values());
			
			// path updated
			path = GraphManager.get(getAgentName()).path(getPosition(), target);
			
			// target informed to other agents
			messenger.broadcast(new Message(getAgentName(), new Percept("goingTo", new Identifier(target))));
			
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
