package org.lannister.agents;

import org.lannister.EIManager;

import eis.iilang.Action;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public abstract class Agent extends Thread {

	private String name;
	private int step = -1;
	
	private String position;
	private String lastAction;
	private String lastActionResult;
	private int energy;
	
	protected int THRESHOLD_ENERGY = 10;
	
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
			
			Action action = perform();
			
			if(action != null) {
				EIManager.act(getAgentName(), action);
			}
			
			running = EIManager.isRunning();
		}
	}
	
	public abstract void handlePercept(Percept percept);
	public abstract void handleMessage(String message);
	public abstract Action perform();
}
