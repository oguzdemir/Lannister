package org.lannister.brain;

import org.lannister.action.ActionFactory;
import org.lannister.action.ActionResults;
import org.lannister.action.Actions;
import org.lannister.agents.AgentMode;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public class ExplorerBrain extends AgentBrain {

	public ExplorerBrain(String name) {
		super(name);
	}

	@Override
	protected Action handleFailedAction() {
		switch (result) { 
			case ActionResults.FAILRANDOM:
				return ActionFactory.get().create(action, param);
			case ActionResults.FAILUNKNOWN:
				return ActionFactory.get().create(action, param);
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
					plan   = plan.isCompleted() ? AgentPlanner.newBestScoringPlan(position, name) 	: plan;
				}
				break;
			case DEFENDING:
				action = ActionFactory.get().create(Actions.SKIP);
				break;
			case BESTSCORE:
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
		return null;
	}
	
	@Override
	protected Action handleDisabledAction() {
		plan = AgentPlanner.newBestScoringPlan(position, name);
		return plan.isCompleted() ? ActionFactory.get().create(Actions.SKIP) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
	}
}
