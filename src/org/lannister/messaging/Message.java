package org.lannister.messaging;

import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Message {

	private String from;
	private Percept percept;
	
	public Message(String from, Percept percept) {
		this.from 		= from;
		this.percept 	= percept;
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public Percept getPercept() {
		return percept;
	}
	public void setPercept(Percept percept) {
		this.percept = percept;
	}
	
	
	
}
