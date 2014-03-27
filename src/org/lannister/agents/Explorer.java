package org.lannister.agents;

import java.util.LinkedList;
import java.util.List;

import org.lannister.EIManager;
import org.lannister.graph.GraphManager;
import org.lannister.messaging.Message;
import org.lannister.messaging.Messages;
import org.lannister.util.ActionResults;
import org.lannister.util.Actions;
import org.lannister.util.Percepts;
import org.lannister.util.Thresholds;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Explorer extends Agent {
	
	// path to the target node
	private LinkedList<String> path = new LinkedList<String>();
	
	// next node to visit in order to reach target node
	private String next;
	
	// base position of the explorer agent to stay on during simulation
	private String base;
	
	public Explorer(String name) {
		super(name);
		this.mode = AgentMode.EXPLORING;
	}
	
	@Override
	protected void handlePercepts() {
		List<Percept> percepts = EIManager.getPercepts(getAgentName());
		
		for(Percept percept : percepts) {
			if(percept.getName().equals(Percepts.STEP)) {
				int step = Integer.valueOf(percept.getParameters().getFirst().toString());
				if(getCurrentStep() < step) {
					newStep = true;
					setCurrentStep(step);
				}
				else {
					newStep = false;
				}
			}
			else if(percept.getName().equals(Percepts.POSITION)) {
				String pos = percept.getParameters().getFirst().toString();
				setPosition(pos);
				GraphManager.get(getAgentName()).setVisited(pos);
				coordinator.broadcast(Messages.create(getAgentName(), percept));
			}
			else if(percept.getName().equals(Percepts.VISIBLEVERTEX)) {
				GraphManager.get(getAgentName()).addVertex(percept.getParameters().getFirst().toString());
				coordinator.broadcast(Messages.create(getAgentName(), percept));
			}
			else if(percept.getName().equals(Percepts.VISIBLEEDGE)) {
				GraphManager.get(getAgentName()).addEdge(percept.getParameters().getFirst().toString(),
										   percept.getParameters().getLast().toString(), 1);
				coordinator.broadcast(Messages.create(getAgentName(), percept));
			}
			else if(percept.getName().equals(Percepts.ENERGY)) {
				setEnergy(Integer.valueOf(percept.getParameters().getFirst().toString()));
			}
			else if(percept.getName().equals(Percepts.LASTACTION)) {
				setLastAction(percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals(Percepts.LASTACTIONRESULT)) {
				setLastActionResult(percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals(Percepts.PROBEDVERTEX)) {
				GraphManager.get(getAgentName()).setProbed(percept.getParameters().getFirst().toString(),
														Integer.valueOf(percept.getParameters().getLast().toString()));
				coordinator.broadcast(Messages.create(getAgentName(), percept));
			}
		}
	}
	
	@Override
	protected void handleMessages() {
		List<Message> messages = coordinator.popMessages(getAgentName());
		
		for(Message message : messages) {
			String fromAgent = message.getFrom();
			Percept percept  = message.getPercept();
			
			if(percept.getName().equals(Percepts.VISIBLEVERTEX)) {
				GraphManager.get(getAgentName()).addVertex(percept.getParameters().get(0).toString());
			} 
			else if(percept.getName().equals(Percepts.VISIBLEEDGE)) {
				GraphManager.get(getAgentName()).addEdge(percept.getParameters().getFirst().toString(),
						   percept.getParameters().getLast().toString(), 1);
			}
			else if(percept.getName().equals(Percepts.POSITION)) {
				GraphManager.get(getAgentName()).setVisited(percept.getParameters().get(0).toString());
			}
			else if(percept.getName().equals(Percepts.PROBEDVERTEX)) {
				GraphManager.get(getAgentName()).setProbed(percept.getParameters().getFirst().toString(),
						Integer.valueOf(percept.getParameters().getLast().toString()));
			}
			else if(percept.getName().equals(Percepts.BASE)) {
				this.base = percept.getParameters().getFirst().toString();
			}
		}
	}
	
	@Override
	protected Action perform() {
		info();
		
		// update distance matrix
		GraphManager.get(getAgentName()).aps();
		
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
		
		print("Skip..");
		return new Action(Actions.SKIP);
	}
	
	@Override
	protected void updateMode() {
		// update mode to defensive if someone is attacking
		if(getLastActionResult().equals(ActionResults.ATTACKED)) {
			this.mode = AgentMode.DEFENSIVE;
		}
		// update mode to probing if there are no more unvisited nodes
		else if(this.mode == AgentMode.EXPLORING && path.isEmpty() && GraphManager.get(getAgentName()).getUnvisited(getPosition(), coordinator.getTargets()) == null) {
			this.mode = AgentMode.PROBING;
		}
		// update mode to bestscore if there are no more unprobed nodes
		// signal coordinator to find the base for team 
		else if(this.mode == AgentMode.PROBING && path.isEmpty() && GraphManager.get(getAgentName()).getUnprobed(getPosition(), coordinator.getTargets()) == null) {
			this.mode = AgentMode.BESTSCORE;
			coordinator.findBase();
		}
 	}
	
	private Action planProbe() {
		if(shouldProbe()) {
			return new Action(Actions.PROBE);
		}
		return null;
	}
	
	// probe if:
	// 1 - agent is on the target node which is not probed
	private boolean shouldProbe() {
		return (this.mode == AgentMode.EXPLORING && path.isEmpty() && !GraphManager.get(getAgentName()).isProbed(getPosition()))
				|| (this.mode == AgentMode.PROBING && path.isEmpty() && !GraphManager.get(getAgentName()).isProbed(getPosition()));
				
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
	// 3 - Energy is below minimum allowed
	private boolean shouldRecharge() {
		return (getLastAction().equals(Actions.GOTO) && getLastActionResult().equals(ActionResults.NORESOURCE)) ||
				(getLastAction().equals(Actions.PROBE) && getLastActionResult().equals(ActionResults.NORESOURCE)) ||
				(getEnergy() <= Thresholds.ENERGY);
	}
	
	private Action planGoto() {
		updatePath();
		
		if(shouldGoto()) {
			next = path.remove(0);
			return new Action(Actions.GOTO, new Identifier(next));
		}
		
		return null;
	}
	
	// goto if:
	// 1 - If in Exploring mode, there is a valid path to go to a target node
	// 2 - If in Probing mode, there is a valid path to go to a target node to probe
	private boolean shouldGoto() {
		return !path.isEmpty();
	}
	
	//keep path always updated
	private void updatePath() {
		if(path.isEmpty()) {

			String target = null;
			if(this.mode == AgentMode.EXPLORING) {
				target = GraphManager.get(getAgentName()).getUnvisited(getPosition(), coordinator.getTargets());
			}
			else if(this.mode == AgentMode.PROBING) {
				target = GraphManager.get(getAgentName()).getUnprobed(getPosition(), coordinator.getTargets());
			}
			else if(this.mode == AgentMode.BESTSCORE) {
				target = base;
			}
			
			if(target != null && !target.equals(getPosition())) {
				print("Updating path..");
				// path updated
				path = GraphManager.get(getAgentName()).path(getPosition(), target);
				
				// register target to coordinator
				boolean ret = coordinator.registerTarget(getAgentName(), target);
				
				// check for target collision, no two agent aims the same node
				if(this.mode == AgentMode.EXPLORING || this.mode == AgentMode.PROBING) {
					if(ret == false) {
						print("Target is collided with another agent..");
						updatePath();
					}
				}
				
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
