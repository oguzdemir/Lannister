package org.lannister.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lannister.action.Actions;
import org.lannister.agents.AgentMode;
import org.lannister.agents.AgentTypes;
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
	 * Team
	 */
	protected String team;
	
	/**
	 * Name of the agent
	 */
	protected String name;
	
	/**
	 * Positions of each agent
	 */
	protected Map<String, String> positions = new HashMap<String, String>();
	
	private AgentsCoordinator coordinator;
	
	public AgentBrain(String name) {
		this.name = name;
		this.mode = AgentMode.EXPLORING;
		this.plan = AgentPlanner.emptyPlan();
	}
	
	/**
	 * This is where brain ticks and tacks.
	 * @return
	 */
	public Action perform() {
		Action action = handleFailures();
		
		// successful GOTO action
		if(action == null && this.action.equals(Actions.GOTO)) {
			plan.update();
		}
		
		// successful SURVEY action
		if(action == null && this.action.equals(Actions.SURVEY)) {
			GraphManager.get().setSurveyed(position);
		}
		
		return action == null ? handleSuccess() : action;
	}
	
	/**
	 * Handle failures that might have happened from last step, 
	 * such as random failures, attack failures, etc.. 
	 * @return
	 */
	protected abstract Action handleFailures();
	
	/**
	 * Do your next action according to the current plan
	 * @return
	 */
	protected abstract Action handleSuccess();
	
	protected void updateMode(AgentMode newMode) {
		nmode = mode;
		mode  = newMode;
	}
	
	protected void revertMode() {
		mode = nmode;
	}
	
	/**
	 * Wait until help is received
	 */
	private void initHelp() {
		switch(mode) {
			case EXPLORING:	
				AgentPlanner.abortExploringPlan(plan.getTarget());
				plan = AgentPlanner.emptyPlan();
				break;
			case PROBING:
				AgentPlanner.abortProbingPlan(plan.getTarget());
				plan = AgentPlanner.emptyPlan();
				break;
			case SURVEYING:
				AgentPlanner.abortSurveyingPlan(plan.getTarget());
				plan = AgentPlanner.emptyPlan();
				break;
		}
		
		updateMode(AgentMode.DEFENDING);
		
		
		// TODO: SEARCH ONLY REPAIRERS IN (SURVEYING, EXPLORING, BESTSCORE) MODE
		
		List<Pair<Integer, String>> costs = new ArrayList<Pair<Integer, String>>();
		
		for(String agentName : positions.keySet()) {
			if(AgentTypes.isTypeOf(agentName, AgentTypes.REPAIRER)) {
				Integer cost = GraphManager.get().edgeCost(position, positions.get(agentName));
				costs.add(new Pair<Integer, String>(cost, agentName));
			}
		}
		
		// find closest
		Pair<Integer, String> cost = Ordering.natural().min(costs);
		
		// send message
		String repairerName = cost.second();
		coordinator.send(Messages.create(name, new Percept(Percepts.HELP)), repairerName);
	}
	
	private void doneHelp() {
		revertMode();
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

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Map<String, String> getPositions() {
		return positions;
	}

	public void setPositions(Map<String, String> positions) {
		this.positions = positions;
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
		if(this.health > health && !sentHelp) {
			sentHelp = true;
			initHelp();
		}
		
		if(this.health < health && sentHelp) {
			sentHelp = false;
			doneHelp();
		}
		this.health = health;
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
}
