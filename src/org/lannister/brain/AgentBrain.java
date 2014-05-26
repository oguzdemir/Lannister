package org.lannister.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lannister.action.Actions;
import org.lannister.agents.AgentMode;
import org.lannister.agents.AgentTypes;
import org.lannister.brain.AgentPlan.PlanType;
import org.lannister.graph.GraphManager;
import org.lannister.messaging.AgentsCoordinator;
import org.lannister.messaging.Messages;
import org.lannister.util.Pair;
import org.lannister.util.Percepts;

import com.google.common.collect.Ordering;

import eis.iilang.Action;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public abstract class AgentBrain {
	
	/**
	 * Last action memories
	 */
	protected String action;
	protected String param;
	protected String result;
	
	/**
	 * Current mode of the agent (current goal) 
	 */
	protected AgentMode mode;
	
	/**
	 * Normal mode of the agent (EXPLORING, PROBING, SURVEYING, BESTSCORE)
	 */
	protected AgentMode nmode;
	
	/**
	 * Current plan
	 */
	protected AgentPlan plan;
	
	/**
	 * Current position
	 */
	protected String position;
	
	/**
	 * Current role
	 */
	protected String role;
	
	/**
	 * Current energy
	 */
	protected int energy;
	
	/**
	 * Disabled or not
	 */
	protected boolean disabled;
	
	/**
	 * Current health
	 */
	protected int health;
	
	/**
	 * Name of the agent
	 */
	protected String name;
	
	/**
	 * Positions of each agent
	 */
	protected Map<String, String> positions = new HashMap<String, String>();
	
	/**
	 * Role of each agent
	 */
	protected Map<String, String> roles = new HashMap<String, String>();
	
	/**
	 * Health of each agent
	 */
	protected Map<String, Integer> healths = new HashMap<String, Integer>();
	
	private AgentsCoordinator coordinator;
	
	public AgentBrain(String name) {
		this.name = name;
		this.mode = AgentMode.EXPLORING;
		this.plan = AgentPlanner.emptyPlan(PlanType.EXPLORING);
	}
	
	/**
	 * This is where brain ticks and tocks.
	 * @return
	 */
	public Action perform() {
		Action action = null;
		
		// return an action if disabled
		if(disabled) {
			action = handleDisabledAction();
		}
		
		// return an action if there is an immediate call
		if(action == null) {
			action = handleImmediateAction();
		}
		
		// return an action if last action failed
		if(action == null) {
			action = handleFailedAction();
		}
		
		// successful GOTO action
		if(action == null && this.action.equals(Actions.GOTO)) {
			plan.update();
		}
		
		// small hack for "GOTO current position" errors
		if(!plan.isCompleted() && plan.next().equals(position)) {
			plan.update();
		}
		
		// successful SURVEY action
		if(action == null && this.action.equals(Actions.SURVEY)) {
			GraphManager.get().setSurveyed(position);
		}
		// handle succeeded actions
		if(action == null) {
			action = handleSucceededAction();
		}
		System.out.println(name + ": " + action);
		return action;
	}
	
	protected abstract Action handleDisabledAction();
	
	/**
	 * Handle perceptual warnings that need an action
	 * @return
	 */
	protected abstract Action handleImmediateAction();
	
	/**
	 * Handle failures that might have happened from last step, 
	 * such as random failures, attack failures, etc.. 
	 * @return
	 */
	protected abstract Action handleFailedAction();
	
	/**
	 * Do your next action according to the current plan
	 * @return
	 */
	protected abstract Action handleSucceededAction();
	
	/**
	 * Update internals when enemy is seen
	 */
	public abstract void handleWhenEnemySeen(String id, String position, String status);
	
	/**
	 * Update internals when friend is seen
	 */
	public abstract void handleWhenFriendSeen(String id, String position, String status);
	
	protected void updateMode(AgentMode newMode) {
		nmode = mode;
		mode  = newMode;
	}
	
	protected void revertMode() {
		mode = nmode;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public Map<String, String> getPositions() {
		return positions;
	}

	public void setPositions(Map<String, String> positions) {
		this.positions = positions;
	}

	public Map<String, Integer> getHealths() {
		return healths;
	}
	
	public Map<String, String> getRoles() {
		return roles;
	}
	
	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private boolean sentHelp;
	
	public void setHealth(int health) {
		this.health = health;
	}

	public int getHealth() {
		return this.health;
	}
	
	public AgentsCoordinator getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(AgentsCoordinator coordinator) {
		this.coordinator = coordinator;
	}
	
	public AgentMode getMode() {
		return mode;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public Map<String, String> getDisabledAgentsPositions() {
		Map<String, String> map = new HashMap<String, String>();
		for(Entry<String, Integer> entry : healths.entrySet()) {
			String agentName = entry.getKey();
			Integer health   = entry.getValue();
			String position  = positions.get(agentName);
			if(health == 0) {
				map.put(agentName, position);
			}
		}
		return map;
	}
	
	protected void abortPlan() {
		AgentPlanner.abortPlan(plan);
		switch(plan.type) {
			case BESTSCORE: plan = AgentPlanner.emptyPlan(PlanType.BESTSCORE); break;
			case PROBING:   plan = AgentPlanner.emptyPlan(PlanType.PROBING);   break;
			case REPAIRING: plan = AgentPlanner.emptyPlan(PlanType.REPAIRING); break;
			case SURVEYING: plan = AgentPlanner.emptyPlan(PlanType.SURVEYING); break;
			case EXPLORING: plan = AgentPlanner.emptyPlan(PlanType.EXPLORING); break;
		}
	}
}
