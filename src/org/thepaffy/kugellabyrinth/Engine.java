package org.thepaffy.kugellabyrinth;

public class Engine {
	
	private static Engine mInstance = null;
	
	private Engine() {
		
	}
	
	public static Engine getInstance() {
		if(mInstance == null) {
			mInstance = new Engine();
		}
		return mInstance;
	}

}
