package org.lannister.agents;

import java.util.LinkedList;
import java.util.List;

import org.lannister.EIManager;
import org.lannister.graph.GraphManager;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Explorer extends Agent {

	private boolean newStep = false;
	
	private String targetVertex;
	private List<String> path = new LinkedList<String>();
	
	public Explorer(String name) {
		super(name);
	}

	@Override
	public void handlePercept(Percept percept) {
		
	}

	private void handlePercepts() {
		List<Percept> percepts = EIManager.getPercepts(getAgentName());
		
		for(Percept percept : percepts) {
			if(percept.getName().equals("step")) {
				int step = Integer.valueOf(percept.getParameters().get(0).toString());
				if(getCurrentStep() < step) {
					newStep = true;
					setCurrentStep(step);
				}
				else {
					newStep = false;
				}
			}
			else if(percept.getName().equals("position")) {
				String pos = percept.getParameters().get(0).toString();
				setPosition(pos);
				GraphManager.setVisited(pos);
			}
			else if(percept.getName().equals("visibleVertex")) {
				GraphManager.get().addVertex(percept.getParameters().get(0).toString());
			}
			else if(percept.getName().equals("visibleEdge")) {
				GraphManager.get().addEdge(percept.getParameters().getFirst().toString(),
										   percept.getParameters().getLast().toString(), 1);
			}
		}
	}
	
	@Override
	public Action perform() {
		handlePercepts();
		if(newStep) {
			info();
			
			// update distance matrix
			GraphManager.get().allPairsShortestPath();
			
			// plan a mission, if not available
			if(path.isEmpty()) {
				print("Path is finished, finding a new one.");
				startMission();
			}
			
			// act
			String nextVertex = path.remove(0);
			return new Action("goto", new Identifier(nextVertex));
		}
		return null;
	}

	private void startMission() {
		print("Starting a mission!");
		
		// get unvisited vertex
		targetVertex = GraphManager.getUnvisited();
		
		print("Target vertex: " + targetVertex);
		
		// plan traversing path
		path = GraphManager.path(getPosition(), targetVertex);
		
		print("Path: " + path);
	}
	
}
