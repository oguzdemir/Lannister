package org.lannister.action;
/**
author = 'Oguz Demir'
 */
public class ActionResults {

	/**
	 * Returned when the action was successful
	 */
	public final static String SUCCESS 			= "successful";
	
	/**
	 * The agent attempted to move (goto) to a node that is not connected to its current one.
	 */
	public final static String FAILUNREACHABLE 	= "failed_ureachable";
	
	/**
	 * The agent does not have enough resources to execute the
  	 * action. In most cases, resources mean energy points, although for the buy
     * action it can also mean money (i.e., achievement points).
	 */
	public final static String FAILNORESOURCE 	= "failed_resources";
	
	/**
	 * Besides each action's intrinsic failure possibilities, any action can fail ran-
	 * domly with a 1 percent probability. In this case the action is considered as the
	 * skip action.
	 */
	public final static String FAILRANDOM 		= "failed_random";
	
	/**
	 * The attempted action was interrupted because the agent was
	 * successfully attacked. Not that only some actions can be prevented by an
     * attack
	 */
	public final static String FAILATTACKED 		= "failed_attacked";
	
	/**
	 * The attempted attack was parried by the target.
	 */
	public final static String FAILPARRIED		= "failed_parried";
	
	/**
	 * The target of the attempted ranged action was outside
 	 * the visibility range of the agent.
	 */
	public final static String FAILOUTOFRANGE		= "failed_out_of_range";
	
	/**
	 * The ranged action was missed because of the distance, even
	 * though the target of the attempted ranged action was within the visibility
	 * range of the agent
	 */
	public final static String FAILINRANGE		= "failed_in_range";
	
	/**
	 * The parameter given was not recognized as a valid iden-
	 * timer.
	 */
	public final static String FAILWRONGPARAM		= "failed_wrong_param";
	
	/**
	 * Agent belongs to a role that is not capable of executing the at-
	 * tempted action.
	 */
	public final static String FAILROLE		    = "failed_role";
	
	/**
	 * The agent is currently disabled, and the action can only be
	 * executed when enabled
	 */
	public final static String FAILSTATUS		    = "failed_status";
	
	/**
	 * The agent attempted to buy an extension pack to improve an
	 * attribute for which it has already reached the maximum value allowed
	 */
	public final static String FAILLIMIT			= "failed_limit";
	
	/**
	 * This code is used when the agent did not send an action on time, or
	 * when the action sent was not recognized by the server.
	 */
	public final static String FAILUNKNOWN		= "failed";
}
