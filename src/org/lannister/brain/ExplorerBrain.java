package org.lannister.brain;

import org.lannister.action.ActionFactory;
import org.lannister.action.ActionResults;
import org.lannister.action.Actions;
import org.lannister.agents.AgentMode;
import org.lannister.agents.AgentTypes;
import org.lannister.graph.GraphManager;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public class ExplorerBrain extends AgentBrain {

	private String positionToRunAway;
	
	public ExplorerBrain(String name) {
		super(name);
	}

	@Override
	protected Action handleFailedAction() {
		switch (result) { 
			case ActionResults.FAILRANDOM:		// retry
				return ActionFactory.get().create(action, param);
			case ActionResults.FAILUNREACHABLE: //
			case ActionResults.FAILUNKNOWN: 	// abort and skip this step
				abortPlan();
				return ActionFactory.get().create(Actions.SKIP);
			case ActionResults.FAILNORESOURCE: 	// recharge
				return ActionFactory.get().create(Actions.RECHARGE);
			case ActionResults.FAILATTACKED:   	// defend
				abortPlan();
				return ActionFactory.get().create(Actions.SKIP);
			case ActionResults.FAILSTATUS: 		// disabled
				abortPlan();
				return ActionFactory.get().create(Actions.SKIP);
			default: 
				return null;
		}
	}

	@Override
	protected Action handleSucceededAction() {
		Action action = null;
		switch(mode) {
			case EXPLORING:
				action = plan.isCompleted() ? ActionFactory.get().probeOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				if(!Actions.isTypeOf(action, Actions.RECHARGE)) {
					plan   = plan.isCompleted() ? AgentPlanner.newExploringPlan(position) 	: plan;
					mode   = plan.isCompleted() ? AgentMode.PROBING 						: mode;
					plan   = plan.isCompleted() ? AgentPlanner.newProbingPlan(position) 	: plan;
				}
				break;
			case PROBING:
				action = plan.isCompleted() ? ActionFactory.get().probeOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				if(!Actions.isTypeOf(action, Actions.RECHARGE)) {
					plan   = plan.isCompleted() ? AgentPlanner.newProbingPlan(position) 			: plan;
					mode   = plan.isCompleted() ? AgentMode.BESTSCORE 							  	: mode;
					mode   = teamInBestScore()  ? AgentMode.BESTSCORE								: mode;
					plan   = plan.isCompleted() ? AgentPlanner.newBestScoringPlan(position, name) 	: plan;
					plan   = teamInBestScore()  ? AgentPlanner.newBestScoringPlan(position, name)   : plan;
				}
				break;
			case BESTSCORE:
				plan   = plan.isCompleted() ? AgentPlanner.newBestScoringPlan(position, name) : plan;
				action = plan.isCompleted() ? ActionFactory.get().create(Actions.SKIP) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				break;
			default:
				action = ActionFactory.get().create(Actions.SKIP);
				break;
		}
		return action;
	}

	@Override
	protected Action handleImmediateAction() {
		
		if(positionToRunAway != null) abortPlan();
		
		try 	{ return positionToRunAway != null ? ActionFactory.get().gotoOrRecharge(energy, getPosition(), positionToRunAway) : null; } 
		finally { positionToRunAway = null; }
	}
	
	@Override
	protected Action handleDisabledAction() {
		abortPlan();
		
		return ActionFactory.get().create(Actions.SKIP);
	}

	@Override
	public void handleWhenEnemySeen(String id, String position, String status) {
		boolean nearMe		= GraphManager.get().edgeCost(getPosition(), position) <= 1;
		boolean isSaboteur  = !getRoles().containsKey(id) || getRoles().get(id).equals(AgentTypes.SABOTEUR);
		boolean notDisabled  = !status.equals("disabled");
		if(nearMe && isSaboteur && notDisabled) {
			positionToRunAway = GraphManager.get().findRunawayNode(getPosition(), position);
		}
		
	}

	@Override
	public void handleWhenFriendSeen(String id, String position, String status) {
		// do nothing
	}
	
	private boolean teamInBestScore() {
		return AgentPlanner.teamInBestScore();
	}
}
