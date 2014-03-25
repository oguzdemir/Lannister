package org.lannister.agents;

import org.lannister.EIManager;
import org.lannister.messaging.AgentsCoordinator;
import org.lannister.util.ActionResults;
import org.lannister.util.Actions;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public abstract class Agent extends Thread {

	protected String name;
	protected int step = -1;
	protected boolean newStep = false;
	
	private String position;
	private String lastAction       = Actions.SKIP;
	private String lastActionResult = ActionResults.SUCCESS;
	private int energy;
	
	protected AgentMode mode;
	protected AgentsCoordinator coordinator;
	
	public Agent(String name) {
		this.name = name;
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
	
	protected String getPosition() {
		return position;
	}
	
	protected void setPosition(String position) {
		this.position = position;
	}
	
	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	public String getLastActionResult() {
		return lastActionResult;
	}

	public void setLastActionResult(String lastActionResult) {
		this.lastActionResult = lastActionResult;
	}

	public AgentsCoordinator getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(AgentsCoordinator coordinator) {
		this.coordinator = coordinator;
	}

	protected void info() {
		print("Step: " + step);
		print("Position: " + position);
		print("Energy: " + energy);
		print("Last action: " + lastAction);
		print("Last Action Result: " + lastActionResult);
	}
	
	protected void print(Object o) {
		System.out.println("[Agent - " + getAgentName() + "]: " + o);
	}
	
	public void run() {
		boolean running = EIManager.isRunning();
		
		while(running) {
			handlePercepts();
			handleMessages();
			if(newStep) {
				updateMode();
				
				Action action = perform();
				
				if(action != null) {
					EIManager.act(getAgentName(), action);
				}
			}
			running = EIManager.isRunning();
		}
	}
	
	
	
	/**
	 * Plans an action according to current beliefs.
	 * @return
	 */
	protected abstract Action perform();
	
	/**
	 * Handles percepts that agents sensors received
	 */
	protected abstract void handlePercepts();
	
	/**
	 * Handles messages received from other agents
	 */
	protected abstract void handleMessages();
	
	/**
	 * Updates mode if necessary
	 */
	protected abstract void updateMode();
}
