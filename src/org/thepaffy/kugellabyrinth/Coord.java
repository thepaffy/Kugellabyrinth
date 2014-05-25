package org.thepaffy.kugellabyrinth;

public class Coord {
	
	private int mX;
	private int mY;
	
	/**
	 * Constructs a new coordinate at (0, 0).
	 */
	public Coord() {
		this(0,0);
	}
	
	/**
	 * Constructs a new coordinate at (x, y).
	 */
	public Coord(int x, int y) {
		mX = x;
		mY = y;
	}
	
	/**
	 * Constructs a new coordinate as copy of c.
	 */
	public Coord(Coord c) {
		mX = c.mX;
		mY = c.mY;
	}
	
	/**
	 * Returns the x-part of the Coordinate.
	 * 
	 * @return x-part of the Coordinate.
	 */
	public int x() {
		return mX;
	}
	
	/**
	 * Returns the y-part of the Coordinate.
	 * 
	 * @return y-part of the Coordinate.
	 */
	public int y() {
		return mY;
	}
	
	/**
	 * Set the x-part of the Coordinate.
	 * 
	 * @param x The x-part of the Coordinate
	 */
	public void setX(int x) {
		mX = x;
	}
	
	/**
	 * Set the y-part of the Coordinate.
	 * 
	 * @param y The y-part of the Coordinate
	 */
	public void setY(int y) {
		mY = y;
	}
	
	/**
	 * Set the x and y-part of the Coordinate.
	 * 
	 * @param x The x-part of the Coordinate
	 * @param y The y-part of the Coordinate
	 */
	public void setXY(int x, int y) {
		mX = x;
		mY = y;
	}
	
	@Override
	public String toString() {
		return "X-Achse: " + mX + ", Y-Achse: " + mY;
	}

}
