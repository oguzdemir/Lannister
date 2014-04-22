package org.lannister.action;

import eis.iilang.Action;

/**
author = 'Oguz Demir'
 */
public class Actions {

	public final static String GOTO 		= "goto";
	public final static String RECHARGE 	= "recharge";
	public final static String PROBE		= "probe";
	public final static String SKIP       	= "skip";
	public final static String PARRY		= "parry";
	public final static String SURVEY		= "survey";
	public final static String REPAIR     	= "repair";
	public final static String ATTACK		= "attack";
	public final static String INSPECT		= "inspect";
	public final static String NOACTION		= "noAction";
	
	public static boolean isTypeOf(Action action, String type) {
		switch(type) {
			case GOTO:
				return action.getName().equals(GOTO);
			case RECHARGE:
				return action.getName().equals(RECHARGE);
			case PROBE:
				return action.getName().equals(PROBE);
			case SKIP:
				return action.getName().equals(SKIP);
			case PARRY:
				return action.getName().equals(PARRY);
			case SURVEY:
				return action.getName().equals(SURVEY);
			case REPAIR:
				return action.getName().equals(REPAIR);
			default:
				return false;
		}
	}
}
