package org.lannister.agents;

import java.util.List;

import org.lannister.brain.AgentBrain;
import org.lannister.graph.GraphManager;
import org.lannister.messaging.AgentsCoordinator;
import org.lannister.messaging.Message;
import org.lannister.messaging.Messages;
import org.lannister.util.Percepts;

import eis.iilang.Action;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public abstract class Agent {

	protected String name;
	protected String team;
	protected int step = -1;
	protected boolean newStep = false;

	protected AgentBrain brain;
	
	public Agent(String name, String team, AgentBrain brain) {
		this.name 	= name;
		this.team	= team;
		this.brain 	= brain;
	}

	protected void printInfo() {
		print("Step: " + step);
		print("Position: " + brain.getPosition());
		print("Mode: " + brain.getMode());
		print("Energy: " + brain.getEnergy());
		print("Last action: " + brain.getAction());
		print("Last action param: " + brain.getParam());
		print("Last Action result: " + brain.getResult());
	}
	
	protected void print(Object o) {
		System.out.println("[Agent - " + getAgentName() + "]: " + o);
	}
	
	/**
	 * Performs an action if in a new step
	 * @return
	 */
	public Action perform() {
		handlePercepts();
		handleMessages();
		
		if(newStep) {
			if(brain.getMode() == AgentMode.EXPLORING) GraphManager.get().aps();
			//printInfo();
			return brain.perform();
		}
		return null;
	}
	
	/**
	 * Handles percepts that agents sensors received,
	 * Returns true if it is a new step.
	 */
	protected abstract void handlePercepts();
	
	/**
	 * Handles messages received from other agents
	 */
	protected abstract void handleMessages();
	
	public boolean handleCommonPercepts(List<Percept> percepts) {
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
				brain.setPosition(pos);
				GraphManager.get().setVisited(pos);
				brain.getCoordinator().broadcast(Messages.create(getAgentName(), percept));
			}
			else if(percept.getName().equals(Percepts.VISIBLEVERTEX)) {
				GraphManager.get().addVertex(percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals(Percepts.VISIBLEEDGE)) {
				GraphManager.get().addEdge(percept.getParameters().getFirst().toString(),
										   percept.getParameters().getLast().toString(), 1);
			}
			else if(percept.getName().equals(Percepts.SURVEYEDEDGE)) {
				String vertex1 = percept.getParameters().get(0).toString();
				String vertex2 = percept.getParameters().get(1).toString();
				Integer weight = Integer.valueOf(percept.getParameters().get(2).toString());
				GraphManager.get().setSurveyedEdge(vertex1, vertex2, weight);
			}
			else if(percept.getName().equals(Percepts.ENERGY)) {
				brain.setEnergy(Integer.valueOf(percept.getParameters().getFirst().toString()));
			}
			else if(percept.getName().equals(Percepts.HEALTH)) {
				brain.setHealth(Integer.valueOf(percept.getParameters().getFirst().toString()));
				brain.setDisabled(brain.getHealth() == 0);
				brain.getCoordinator().broadcast(Messages.create(getAgentName(), percept));
			}
			else if(percept.getName().equals(Percepts.LASTACTION)) {
				brain.setAction(percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals(Percepts.LASTACTIONPARAM)) {
				brain.setParam(percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals(Percepts.LASTACTIONRESULT)) {
				brain.setResult(percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals(Percepts.ROLE)) {
				brain.setRole(percept.getParameters().getFirst().toString());
				brain.getCoordinator().broadcast(Messages.create(getAgentName(), percept));
			}
		}
		return newStep;
	}
	
	public void handleCommonMessages(List<Message> messages) {
		for(Message message : messages) {
			String from 	= message.getFrom();
			Percept percept = message.getPercept();
			
			if(percept.getName().equals(Percepts.POSITION)) {
				brain.getPositions().put(from, percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals(Percepts.HEALTH)) {
				brain.getHealths().put(from, Integer.valueOf(percept.getParameters().getFirst().toString()));
			}
			else if(percept.getName().equals(Percepts.ROLE)) {
				brain.getRoles().put(from, percept.getParameters().getFirst().toString());
			}
			else if(percept.getName().equals(Percepts.INSPECTEDENTITY)) {
				String id = percept.getParameters().get(0).toString();
				String team = percept.getParameters().get(1).toString();
				String role = percept.getParameters().get(2).toString();
				String pos  = percept.getParameters().get(3).toString();
				
				brain.getPositions().put(id, pos);
				brain.getRoles().put(id, role);
			}
			
		}
	}
	
	public String getAgentName() {
		return name;
	}
	
	protected int getCurrentStep() {
		return step;
	}
	
	protected void setCurrentStep(int step) {
		this.step = step;
	}

	public void setCoordinator(AgentsCoordinator coordinator) {
		this.brain.setCoordinator(coordinator);
	}
}
