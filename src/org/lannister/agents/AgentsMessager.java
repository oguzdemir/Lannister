package org.lannister.agents;

import java.util.Map;
import java.util.Scanner;

/**
author = 'Oguz Demir'
 */
public class AgentsMessager extends Thread {

	private Map<String, Agent> 	agents;
	private Scanner 			scanner;
	
	protected AgentsMessager(Map<String, Agent> agents) {
		this.agents = agents;
		this.scanner = new Scanner(System.in);
	}
	
	public void run() {
		String goalVertex = null;
		while((goalVertex = scanner.next()) != null) {
			sendMessage(getCurrentAgent(), goalVertex);
		}
	}
	
	private void sendMessage(String agentName, String goalVertex) {
		agents.get(agentName).handleMessage(goalVertex);
	}
	
	private String getCurrentAgent() {
		return "LannisterExplorer1";
	}
	
}
