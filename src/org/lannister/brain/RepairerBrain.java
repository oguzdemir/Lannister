package org.lannister.brain;

import org.lannister.action.ActionFactory;
import org.lannister.action.ActionResults;
import org.lannister.action.Actions;
import org.lannister.agents.AgentMode;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public class RepairerBrain extends AgentBrain {

	public RepairerBrain(String name) {
		super(name);
	}

	/**
	 * id of the caller agent for repair
	 */
	private String caller;
	
	@Override
	protected Action handleFailures() {
		switch (result) { 
			case ActionResults.FAILRANDOM:
				return ActionFactory.get().create(action, param);
			case ActionResults.FAILUNKNOWN:
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
	protected Action handleSuccess() {
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
			case HELPING:
				action = plan.isCompleted() ? ActionFactory.get().repairOrRecharge(energy, caller) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				if(!Actions.isTypeOf(action, Actions.RECHARGE)) {
					mode   = plan.isCompleted() ? nmode : mode;
					plan   = plan.isCompleted() ? (mode == AgentMode.EXPLORING ? AgentPlanner.newExploringPlan(position) 		 : (
												   mode == AgentMode.SURVEYING ? AgentPlanner.newSurveyingPlan(position) 		 : (
												   mode == AgentMode.BESTSCORE ? AgentPlanner.newBestScoringPlan(position, name) : null 
														   										 ))) : plan;
				}
				break;
			case DEFENDING:
				action = ActionFactory.get().parryOrRecharge(energy);
				break;
			case BESTSCORE:
				action = plan.isCompleted() ? ActionFactory.get().parryOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
				break;
			default:
				action = null;
		}
		System.out.println(action);
		return action;
	}
	
	public boolean handleHelpCall(String caller) {
		this.caller = caller;
		switch(mode) {
			case EXPLORING:
				AgentPlanner.abortExploringPlan(plan.getTarget());
				updateMode(AgentMode.HELPING);
				plan = AgentPlanner.newCustomPlan(position, positions.get(caller));
				return true;
			case SURVEYING:
				AgentPlanner.abortSurveyingPlan(plan.getTarget());
				updateMode(AgentMode.HELPING);
				plan = AgentPlanner.newCustomPlan(position, positions.get(caller));
				return true;
			case BESTSCORE:
				updateMode(AgentMode.HELPING);
				plan = AgentPlanner.newCustomPlan(position, positions.get(caller));
				return true;
			default:
				return false;
		}
	}
}
