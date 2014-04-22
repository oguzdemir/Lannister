package org.lannister.action;

import org.lannister.graph.GraphManager;
import org.lannister.util.Thresholds;

import eis.iilang.Action;
import eis.iilang.Identifier;

/**
author = 'Oguz Demir'
 */
public class ActionFactory {
	
	private static ActionFactory factory;
	
	/**
	 * Basic usage: ActionFactory.get().create(Actions.GOTO, "v15");
	 *              ActionFactory.get().create(Actions.SKIP);
	 * @return
	 */
	public static ActionFactory get() {
		if(factory == null) {
			factory = new ActionFactory();
		}
		return factory;
	}
	
	public Action create(String action, String param) {
		return param == null ? create(action) : createWithParam(action, param);
	}
	
	public Action create(String action) {
		return new Action(action);
	}
	
	private Action createWithParam(String action, String param) {
		return new Action(action, new Identifier(param));
	}
	
	public Action gotoOrRecharge(int energy, String position, String next) {
		int cost = GraphManager.get().weightCost(position, next);
		cost = cost == 0 ? Thresholds.ENERGY : cost; 
		return energy > cost ? ActionFactory.get().create(Actions.GOTO, next)
				  	: ActionFactory.get().create(Actions.RECHARGE);
	}
	
	public Action parryOrRecharge(int energy) {
		return energy > Thresholds.PARRY  ? ActionFactory.get().create(Actions.PARRY)
				  	: ActionFactory.get().create(Actions.RECHARGE);
	}
	
	public Action surveyOrRecharge(int energy) {
		return energy > Thresholds.SURVEY ? ActionFactory.get().create(Actions.SURVEY)
				  	: ActionFactory.get().create(Actions.RECHARGE);
	}
	
	public Action probeOrRecharge(int energy) {
		return energy > Thresholds.PROBE ? ActionFactory.get().create(Actions.PROBE)
				 	: ActionFactory.get().create(Actions.RECHARGE);
	}
	
	public Action repairOrRecharge(int energy, String agent) {
		return energy > Thresholds.REPAIR ? ActionFactory.get().create(Actions.REPAIR, agent)
				  	: ActionFactory.get().create(Actions.RECHARGE);
	}
	
	public Action attackOrRecharge(int energy, String agent) {
		return energy > Thresholds.ATTACK ? ActionFactory.get().create(Actions.ATTACK, agent)
				  	: ActionFactory.get().create(Actions.RECHARGE);
	}
}
