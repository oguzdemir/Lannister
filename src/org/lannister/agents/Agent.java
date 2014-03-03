package org.lannister.agents;

import eis.iilang.Action;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public abstract class Agent {

	private String name;
	private int step = -1;
	
	private String position;
	
	public Agent(String name) {
		this.name = name;
	}
	
	public String getName() {
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
	
	protected void info() {
		print("Position: " + position);
	}
	
	protected void print(Object o) {
		System.out.println("[Agent - " + getName() + ": " + o);
	}
	
	public abstract void handlePercept(Percept percept);
	public abstract Action perform();
}
