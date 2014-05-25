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
public class SentinelBrain extends AgentBrain {

	private String enemy;
	private String enemyPos;
	
	public SentinelBrain(String name) {
		super(name);
	}

	@Override
	protected Action handleDisabledAction() {
		abortPlan();
		return ActionFactory.get().create(Actions.SKIP);
	}

	@Override
	protected Action handleImmediateAction() {
		if(enemy != null) {
			abortPlan();
			enemy = null;
			return ActionFactory.get().runawayOrRecharge(energy, getPosition(), enemyPos);
		}
		return null;
	}

	@Override
	protected Action handleFailedAction() {
		switch (result) { 
			case ActionResults.FAILRANDOM:
				return ActionFactory.get().create(action, param);
			case ActionResults.FAILUNKNOWN:
			case ActionResults.FAILUNREACHABLE: // abort plan
				abortPlan();
				return ActionFactory.get().create(Actions.SKIP);
			case ActionResults.FAILNORESOURCE: 	// recharge
				return ActionFactory.get().create(Actions.RECHARGE);
			case ActionResults.FAILATTACKED:   	// defend
				abortPlan();
				return ActionFactory.get().create(Actions.PARRY);
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
				action = plan.isCompleted() ? ActionFactory.get().parryOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				if(plan.isCompleted()) System.out.println(name + " : staying in best score pos");
				break;
		}
		return action;
	}

	@Override
	public void handleWhenEnemySeen(String id, String position, String status) {
		boolean nearMe 		= GraphManager.get().edgeCost(getPosition(), position) <= 1;
		boolean notDisabled = !status.equals("disabled");
		boolean isSaboteur 	= !getRoles().containsKey(id) || getRoles().get(id).equals(AgentTypes.SABOTEUR);
		
		if(nearMe && notDisabled && isSaboteur) {
			enemy 	 = id;
			enemyPos = position;
		}
	}

	@Override
	public void handleWhenFriendSeen(String id, String position, String status) {
		// no action
	}
}
