package org.lannister.agents;

import java.util.LinkedList;
import java.util.List;

import org.lannister.EIManager;
import org.lannister.graph.GraphManager;
import org.lannister.util.ActionResults;
import org.lannister.util.Actions;
import org.omg.PortableServer.THREAD_POLICY_ID;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Explorer extends Agent {

	private boolean newStep = false;
	
	private LinkedList<String> path 		= new LinkedList<String>();
	private LinkedList<String> goalVertices = new LinkedList<String>();
	private String next;
	
	public Explorer(String name) {
		super(name);
	}

	@Override
	public void handlePercept(Percept percept) {
		
	}

	@Override
	public void handleMessage(String message) {
		String goalVertex = message;
		
		if(GraphManager.isKnown(goalVertex)) {
			print("I know where " + goalVertex + " is, will try to reach there in the next step!");
			goalVertices.add(goalVertex);
		}
		else {
			print("I have no idea where " + goalVertex + " is.");
		}
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
				GraphManager.setVisited(pos);
			}
			else if(percept.getName().equals("visibleVertex")) {
				GraphManager.get().addVertex(percept.getParameters().get(0).toString());
			}
			else if(percept.getName().equals("visibleEdge")) {
				GraphManager.get().addEdge(percept.getParameters().getFirst().toString(),
										   percept.getParameters().getLast().toString(), 1);
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
	
	@Override
	public Action perform() {
		handlePercepts();
		if(newStep) {
			info();
			
			// update distance matrix
			GraphManager.get().aps();
			
			print("Total perceived vertex size: " + GraphManager.get().size());
			print("Total visited vertex size: " + GraphManager.get().visited());
			
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
			
			// update path to goal 
			if(!goalVertices.isEmpty()) {
				path = GraphManager.path(getPosition(), goalVertices.remove(0));
				print(path);
			} 
			else {
				String target = GraphManager.getUnvisited(getPosition());
				path = GraphManager.path(getPosition(), target);
				print(path);
			}
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
