package org.lannister.brain;

import org.lannister.action.ActionFactory;
import org.lannister.action.ActionResults;
import org.lannister.action.Actions;
import org.lannister.agents.AgentMode;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public class SaboteurBrain extends AgentBrain {

	private String target = null;
	
	public SaboteurBrain(String name) {
		super(name);
	}

	@Override
	protected Action handleFailedAction() {
		switch (result) { 
			case ActionResults.FAILRANDOM:
				return ActionFactory.get().create(action, param);
			case ActionResults.FAILUNKNOWN:     // no action in the last step
				return ActionFactory.get().create(action, param);
			case ActionResults.FAILNORESOURCE: 	// recharge
				return ActionFactory.get().create(Actions.RECHARGE);
			case ActionResults.FAILATTACKED:   	// defend
				return ActionFactory.get().create(Actions.PARRY);
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
				action = plan.isCompleted() ? ActionFactory.get().parryOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				break;
			default:
				action = null;
		}
		
		return action;
	}

	@Override
	protected Action handleImmediateAction() {
		Action action = null;
		if(target != null && !disabled) {
			action = ActionFactory.get().attackOrRecharge(energy, target);
			target = null;
		}
		return action;
	}

	@Override
	protected Action handleDisabledAction() {
		
		switch(mode) {
			case BESTSCORE:
				System.out.println("I am disabled, let me find way to BS plan.");
				plan = AgentPlanner.newBestScoringPlan(position, name);
				break;
			default:
				plan = AgentPlanner.emptyPlan();
				break;
		}
		return plan.isCompleted() ? ActionFactory.get().create(Actions.SKIP) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
}
