package org.lannister.brain;

import java.util.Map;

import org.lannister.action.ActionFactory;
import org.lannister.action.ActionResults;
import org.lannister.action.Actions;
import org.lannister.agents.AgentMode;
import org.lannister.agents.AgentTypes;
import org.lannister.brain.AgentPlan.PlanType;
import org.lannister.graph.GraphManager;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public class RepairerBrain extends AgentBrain {
	
	/**
	 * id of the enemy agent
	 * @param name
	 */
	private String enemy; 
	
	/**
	 * id of teammate waiting help (teammate should be next to our position)
	 * @param name
	 */
	private String friend;
	
	/**
	 * position of friend waiting help
	 */
	private String friendPos;
	
	private String enemyPos;
	
	public RepairerBrain(String name) {
		super(name);
	}
	
	@Override
	protected Action handleFailedAction() {
		switch (result) { 
			case ActionResults.FAILRANDOM:
				return ActionFactory.get().create(action, param);
			case ActionResults.FAILUNKNOWN:
			case ActionResults.FAILUNREACHABLE: // abort and skip this step
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

	private Action handle() {
		return plan.isActionDone() ? startNewPlan() : keepOnPlan();
	}
	
	private Action keepOnPlan() {
		Action action = null;
		if(plan.isCompleted()) {
			switch(plan.type) {
				case EXPLORING:
					action = ActionFactory.get().surveyOrRecharge(energy);
					if(Actions.isTypeOf(action, Actions.SURVEY)) plan.actionDone();
					break;
				case SURVEYING:
					action = ActionFactory.get().surveyOrRecharge(energy);
					if(Actions.isTypeOf(action, Actions.SURVEY)) plan.actionDone();
					break;
				case REPAIRING:
					action = ActionFactory.get().repairOrRecharge(energy, plan.getTargetAgent());
					if(Actions.isTypeOf(action, Actions.REPAIR)) { plan.actionDone(); 
																   AgentPlanner.removeRepairingTarget(plan.getTargetAgent()); }
 					break;
				case BESTSCORE:
					action = ActionFactory.get().parryOrRecharge(energy);
					if(Actions.isTypeOf(action, Actions.PARRY)) plan.actionDone();
					break;
			}
		}
		else {
			switch(plan.type) {
				case REPAIRING:
					boolean stillDisabled = getHealths().get(plan.getTargetAgent()) == 0;
					if(stillDisabled) {
						action = ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
					}
					else {
						abortPlan();
						action = ActionFactory.get().create(Actions.SKIP);
					}
					break;
				default:
					action = ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
					break;
			}
			
		}
		return action;
	}
	
	private Action startNewPlan() {
		plan = AgentPlanner.newRepairingPlan(position, getDisabledAgentsPositions());
		plan = plan.isCompleted() ? AgentPlanner.newExploringPlan(position)         : plan;
		plan = plan.isCompleted() ? AgentPlanner.newSurveyingPlan(position) 		: plan;
		plan = plan.isCompleted() ? AgentPlanner.newBestScoringPlan(position, name) : plan;
		
		return keepOnPlan();
	}
	
	@Override
	protected Action handleSucceededAction() {
		return handle();
//		Action action = null;
//		switch(mode) {
//			case EXPLORING:
//				action = plan.isCompleted() ? ActionFactory.get().surveyOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
//				if(!Actions.isTypeOf(action, Actions.RECHARGE)) {
//						plan   = plan.isCompleted() ? planExploreOrHelp() : plan;
//						switch(plan.type) {
//							case REPAIRING:
//								nmode  = mode;
//								mode   = AgentMode.HELPING;
//								break;
//							case EXPLORING:
//								mode   = plan.isCompleted() ? AgentMode.SURVEYING 						 : mode;
//								plan   = plan.isCompleted() ? AgentPlanner.newSurveyingPlan(position) 	 : plan;
//								break;
//						}
//				}
//				break;
//			case SURVEYING:
//				action = plan.isCompleted() ? ActionFactory.get().surveyOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
//				if(!Actions.isTypeOf(action, Actions.RECHARGE)) {
//					plan   = plan.isCompleted() ? planSurveyOrHelp() : plan;
//					switch(plan.type) {
//						case REPAIRING:
//							nmode  = mode;
//							mode   = AgentMode.HELPING;
//						case SURVEYING:
//							mode   = plan.isCompleted() ? AgentMode.BESTSCORE 						 	  : mode;
//							plan   = plan.isCompleted() ? AgentPlanner.newBestScoringPlan(position, name) : plan;
//					}
//				}
//				break;
//			case HELPING:
//				action = plan.isCompleted() ? ActionFactory.get().repairOrRecharge(energy, plan.getTargetAgent()) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
//				if(!Actions.isTypeOf(action, Actions.RECHARGE)) {
//					mode   = plan.isCompleted() ? nmode : mode;
//					plan   = plan.isCompleted() ? (mode == AgentMode.EXPLORING ? AgentPlanner.newExploringPlan(position) 		 : (
//												   mode == AgentMode.SURVEYING ? AgentPlanner.newSurveyingPlan(position) 		 : (
//												   mode == AgentMode.BESTSCORE ? AgentPlanner.newBestScoringPlan(position, name) : null 
//														   										 ))) : plan;
//				}
//				break;
//			case BESTSCORE:
//				action = plan.isCompleted() ? ActionFactory.get().parryOrRecharge(energy) : ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
//				plan   = plan.isCompleted() ? planStayOrHelp() : plan;
//				nmode  = plan.isCompleted() ? mode : mode;
//				mode   = plan.isCompleted() ? mode : AgentMode.HELPING;
//				if(plan.isCompleted()) System.out.println(name + " : staying in best score pos");
//				break;
//		}
//		System.out.println(mode);
//		return action;
	}

	@Override
	protected Action handleImmediateAction() {
		if(friend != null) {
			abortPlan();
			String ffriend = friend; friend = null;
			return ActionFactory.get().repairOrRecharge(energy, ffriend);
		}
		if(friendPos != null) {
			abortPlan();
			String ffriendPos = friendPos; friendPos = null;
			return ActionFactory.get().gotoOrRecharge(energy, getPosition(), ffriendPos);
		}
		if(enemy != null) {
			abortPlan();
			enemy = null;
			return ActionFactory.get().runawayOrRecharge(energy, getPosition(), enemyPos);
		}
		return null;
 	}
	
	@Override
	protected Action handleDisabledAction() {
		System.out.println("Checking for disabled agents..");
		Action action = null;
		plan   = plan.type != PlanType.REPAIRING ? AgentPlanner.newRepairingPlan(position, getDisabledAgentsPositions()) : plan;
		action = plan.isCompleted() ? ActionFactory.get().repairOrRecharge(energy, plan.getTargetAgent()) 
									: ActionFactory.get().gotoOrRecharge(energy, position, plan.next());
		if(!Actions.isTypeOf(action, Actions.RECHARGE)) {
			plan = plan.isCompleted() ? AgentPlanner.newRepairingPlan(position, getDisabledAgentsPositions()) : plan;
		}
		return action;
	}

	@Override
	public void handleWhenEnemySeen(String id, String position, String status) {
		boolean nearMe 		= GraphManager.get().edgeCost(getPosition(), position) <= 1;
		boolean isSaboteur  = !getRoles().containsKey(id) || getRoles().get(id).equals(AgentTypes.SABOTEUR);
		boolean notDisabled = !status.equals("disabled");
		
		if(nearMe && isSaboteur && notDisabled) {
			enemy 	 = id;
			enemyPos = position;
		}
	}

	@Override
	public void handleWhenFriendSeen(String id, String position, String status) {
		boolean sameNode	= GraphManager.get().edgeCost(getPosition(), position) == 0;
		boolean nearMe 		= GraphManager.get().edgeCost(getPosition(), position) == 1;
		boolean isDisabled 	= status.equals("disabled");
		
		if(sameNode && isDisabled) {
			friend = id;
		}
		if(nearMe && isDisabled) {
			friendPos = position;
		}
	}
	
//	@Override
//	protected void abortPlan() {
//		super.abortPlan();
//		
//		friend = enemy = null;
//		friendPos = enemyPos = null;
//	}
	
//	private AgentPlan planExploreOrHelp() {
//		Map<String, String> disabledAgentsPositions = getDisabledAgentsPositions();
//		return disabledAgentsPositions.isEmpty() ? AgentPlanner.newExploringPlan(position)
//												 : AgentPlanner.newRepairingPlan(position, disabledAgentsPositions);
//	}
//	
//	private AgentPlan planSurveyOrHelp() {
//		Map<String, String> disabledAgentsPositions = getDisabledAgentsPositions();
//		return disabledAgentsPositions.isEmpty() ? AgentPlanner.newSurveyingPlan(position)
//												 : AgentPlanner.newRepairingPlan(position, disabledAgentsPositions);
//	}
//	
//	private AgentPlan planStayOrHelp() {
//		Map<String, String> disabledAgentsPositions = getDisabledAgentsPositions();
//		return disabledAgentsPositions.isEmpty() ? AgentPlanner.emptyPlan(PlanType.REPAIRING)
//												 : AgentPlanner.newRepairingPlan(position, disabledAgentsPositions);
//	}

}
