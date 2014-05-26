package org.lannister.brain;

import org.lannister.action.ActionFactory;
import org.lannister.action.ActionResults;
import org.lannister.action.Actions;
import org.lannister.agents.AgentMode;
import org.lannister.brain.AgentPlan.PlanType;
import org.lannister.graph.GraphManager;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public class InspectorBrain extends AgentBrain {

	private String enemyToBeInspected;
	
	public InspectorBrain(String name) {
		super(name);
	}

	@Override
	protected Action handleDisabledAction() {
		abortPlan();
		return ActionFactory.get().create(Actions.SKIP);
	}

	@Override
	protected Action handleImmediateAction() {
		
		if(enemyToBeInspected != null) abortPlan();
		
		try 	{ return enemyToBeInspected != null ? ActionFactory.get().inspectOrRecharge(energy, enemyToBeInspected) : null; }
		finally { enemyToBeInspected = null; }
	}

	@Override
	protected Action handleFailedAction() {
		switch (result) { 
			case ActionResults.FAILRANDOM:		// retry
				return ActionFactory.get().create(action, param);
			case ActionResults.FAILUNKNOWN:
			case ActionResults.FAILUNREACHABLE:	// abort and skip this step
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
				action = plan.isCompleted() ? ActionFactory.get().surveyOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				if(!Actions.isTypeOf(action, Actions.RECHARGE)) {
					plan   = plan.isCompleted() ? AgentPlanner.newExploringPlan(position) 	 : plan;
					mode   = plan.isCompleted() ? AgentMode.SURVEYING 						 : mode;
					plan   = plan.isCompleted() ? AgentPlanner.newSurveyingPlan(position) 	 : plan;
				}
				break;
			case SURVEYING:
				action = plan.isCompleted() ? ActionFactory.get().surveyOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				if(!Actions.isTypeOf(action, Actions.RECHARGE)) {
					plan   = plan.isCompleted() ? AgentPlanner.newSurveyingPlan(position) 	 	  : plan;
					mode   = plan.isCompleted() ? AgentMode.BESTSCORE 						 	  : mode;
					plan   = plan.isCompleted() ? AgentPlanner.newBestScoringPlan(position, name) : plan;
				}
				break;
			case BESTSCORE:
				plan   = plan.isCompleted() ? AgentPlanner.newBestScoringPlan(position, name) : plan;
				action = plan.isCompleted() ? ActionFactory.get().create(Actions.SKIP) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				break;
		}
		return action;
	}

	@Override
	public void handleWhenEnemySeen(String id, String position, String status) {
		boolean nearMe 			= GraphManager.get().edgeCost(getPosition(), position) <= 1;
		boolean notSeenBefore 	= !getRoles().containsKey(id);
		
		if(nearMe && notSeenBefore) {
			enemyToBeInspected = id;
		}
	}

	@Override
	public void handleWhenFriendSeen(String id, String position, String status) {
		// no action necessary
	}
	
//	@Override
//	protected void abortPlan() {
//		super.abortPlan();
//		
//		enemyToBeInspected = null;
//	}
}
