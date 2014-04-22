package org.lannister.brain;

import org.lannister.action.ActionFactory;
import org.lannister.action.ActionResults;
import org.lannister.action.Actions;
import org.lannister.agents.AgentMode;
import org.lannister.brain.AgentPlan.PlanType;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public class InspectorBrain extends AgentBrain {

	private String enemy;
	
	public InspectorBrain(String name) {
		super(name);
	}

	@Override
	protected Action handleDisabledAction() {
		Action action = null;
		plan 	= plan.type != PlanType.BESTSCORE ? AgentPlanner.newBestScoringPlan(position, name) : plan;
		action 	= plan.isCompleted() ? ActionFactory.get().create(Actions.SKIP) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
		plan 	= plan.isCompleted() ? AgentPlanner.newBestScoringPlan(position, name) : plan;
		return action;
	}

	@Override
	protected Action handleImmediateAction() {
		if(enemy != null) {
			String eenemy = enemy; enemy = null;
			return ActionFactory.get().inspectOrRecharge(energy, eenemy);
		}
		return null;
	}

	@Override
	protected Action handleFailedAction() {
		switch (result) { 
			case ActionResults.FAILRANDOM:
				return ActionFactory.get().create(action, param);
			case ActionResults.FAILUNKNOWN:
			case ActionResults.FAILUNREACHABLE:
				abortPlan();
				return ActionFactory.get().create(Actions.SKIP);
			case ActionResults.FAILNORESOURCE: 	// recharge
				return ActionFactory.get().create(Actions.RECHARGE);
			case ActionResults.FAILATTACKED:   	// defend
				return ActionFactory.get().create(Actions.SKIP);
			case ActionResults.FAILSTATUS: 		// disabled
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
				action = plan.isCompleted() ? ActionFactory.get().create(Actions.SKIP) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				break;
		}
		return action;
	}
	
	public void setEnemy(String enemy) {
		this.enemy = enemy;
	}

}
