package org.thepaffy.kugellabyrinth;

public class DataModel {
	
	private Kugel mKugel;
	private Board mBoard;
	
	private static DataModel mInstance = null;
	
	private DataModel() {
	}
	
	public static DataModel getInstance() {
		if(mInstance == null){
			mInstance = new DataModel();
		}
		return mInstance;
	}
}
