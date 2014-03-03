package org.lannister;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import eis.AgentListener;
import eis.EILoader;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.exceptions.RelationException;
import eis.iilang.Action;
import eis.iilang.EnvironmentState;
import eis.iilang.Percept;

public class EIManager {
	
	private static EnvironmentInterfaceStandard ei;
	
	static {
		try {
			String cn = "massim.eismassim.EnvironmentInterface";
			ei = EILoader.fromClassName(cn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void register(String agentName) {
		try {
			ei.registerAgent(agentName);
		} catch (AgentException e) {
			e.printStackTrace();
		}
	}
	
	public static void attachListener(String agentName, AgentListener listener) {
		ei.attachAgentListener(agentName, listener);
	}
	
	public static void associate(String agentName, String entityName) {
		try {
			ei.associateEntity(agentName,entityName);
		} catch (RelationException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Percept> getPercepts(String agentName) {
		List<Percept> ret = new ArrayList<Percept>();
		try {
			Map<String, Collection<Percept>> percepts = ei.getAllPercepts(agentName);
			for(Collection<Percept> ps : percepts.values())
				ret.addAll(ps);
		} catch (PerceiveException e) {
			// NOT PERCIEVED YET!
			//e.printStackTrace();
		} catch (NoEnvironmentException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void start() {
		try {
			ei.start();
		} catch (ManagementException e) {
			e.printStackTrace();
		}
	}
	
	public static void act(String agentName, Action action) {
		try {
			ei.performAction(agentName, action);
		} catch (ActException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isRunning() {
		return ei.getState() == EnvironmentState.RUNNING;
	}
}
