package com.sarxos.gpwnotifier.gpw;

public class Session {

	private static Session instance = new Session(); 
	
	
	private Session() {
	}
	
	public static Session getInstance() {
		return instance;
	}
	
	public boolean getPhase() {
		return false;
	}
}
