package org.thepaffy.kugellabyrinth;

public class Kugel {
	
	public static final int sWidth = 3;
	public static final int sHeight = 3;
    
	private int mTopLeftX;
	private int mTopLeftY;
	
	public Kugel() {
		this(0, 0);
	}
	
	public Kugel(int topLeftX, int topLeftY) {
		mTopLeftX = topLeftX;
		mTopLeftY = topLeftY;
	}
	
	public int topLeftX() {
		return mTopLeftX;
	}
	
	public int topLeftY() {
		return mTopLeftY;
	}
	
	public void setTopLeftX(int topLeftX) {
		mTopLeftX = topLeftX;
	}
	
	public void setTopLeftY(int topLeftY) {
		mTopLeftY = topLeftY;
	}
	
	public void setTopLeft(int topLeftX, int topLeftY) {
		mTopLeftX = topLeftX;
		mTopLeftY = topLeftY;
	}
}
