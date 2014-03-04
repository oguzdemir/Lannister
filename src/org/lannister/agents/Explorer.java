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
	
	private List<String> path 			= new LinkedList<String>();
	private List<String> goalVertices 	= new LinkedList<String>();
	
	private boolean onGoalMission = false;
	
	public Explorer(String name) {
		super(name);
	}

	@Override
	public void handlePercept(Percept percept) {
		
	}

	@Override
	public void handleMessage(String message) {
		String goalVertex = message;
		
		if(GraphManager.isKnown(goalVertex)) {
			print("I know where " + goalVertex + " is, will try to reach there in the next step!");
			goalVertices.add(goalVertex);
		}
		else {
			print("I have no idea where " + goalVertex + " is.");
		}
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
			
			// if there is a goal vertex from messager, plan to go there
			if(!goalVertices.isEmpty() && !onGoalMission) {
				startMission(goalVertices.remove(0));
				onGoalMission = true; 
			}
			
			// if there is no longer vertices to visit, plan a new mission to an unvisited vertex
			else if(path.isEmpty()) {
				print("Path is finished, finding a new one.");
				
				// no more on a goal mission
				if(onGoalMission) {
					onGoalMission = !onGoalMission;
				}
				
				startMission();
			}
			
			// act
			String nextVertex = path.remove(0);
			print("Going to " + nextVertex);
			return new Action("goto", new Identifier(nextVertex));
		}
		return null;
	}
	
	private void startMission() {
		print("Starting a mission!");
		
		// get closest unvisited vertex
		String targetVertex = GraphManager.getUnvisited(getPosition());
		
		if(targetVertex != null) {
			startMission(targetVertex);
		}
	}
	
	private void startMission(String targetVertex) {
		print("Target vertex: " + targetVertex);
		
		// plan traversing path
		path = GraphManager.path(getPosition(), targetVertex);
		
		print("Path: " + path);
	}
}
