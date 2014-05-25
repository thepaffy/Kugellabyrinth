package org.thepaffy.kugellabyrinth;

public class Board {
	
	private int mWidth;
	private int mHeight;
	
	private Coord[][] mField;
	
	/**
	 * Constructs a new board with size (x * y) as a clone of the field.
	 */
	public Board(int width, int height, Coord[][] field) {
		mWidth = width;
		mHeight = height;
		mField = field.clone();
	}
	
	/**
	 * Returns the field on the coordinates (x ,y).
	 * 
	 * @return field on the coordinates (x, y).
	 */
	public Coord getField(int x, int y) {
		if (contains(x, y)) {
			return mField[y][x];
		}
		return null;
	}
	
	/**
	 * Returns the field on the coordinates of the parameter c.
	 * 
	 * @return field on the coordinates of the parameter c.
	 */
	public Coord getField(Coord c) {
		if (c == null) {
			return null;
		}
		return getField(c.x(), c.y());
	}
	
	/**
	 * Returns the width of the board.
	 * 
	 * @return width of the board.
	 */
	public int getWidth() {
		return this.mWidth;
	}
	
	/**
	 * Returns the height of the board.
	 * 
	 * @return height of the board.
	 */
	public int getHeight() {
		return this.mHeight;
	}
	
	/**
	 * Determines whether this Board "contains" the specified Coords.
	 * 
	 * @param x
	 *            the x coords.
	 * @param y
	 *            the y coords.
	 * @return <code>true</code> if the board contains the specified coords.
	 */
	public boolean contains(int x, int y) {
		return (x >= 0) && (y >= 0) && (x < mWidth) && (y < mHeight);
	}
	
	/**
	 * Determines whether this Board "contains" the specified Coords.
	 * 
	 * @param c
	 *            the coords.
	 * @return <code>true</code> if the board contains the specified coords.
	 */
	public boolean contains(Coord c) {
		if (c == null) {
			return false;
		}
		return contains(c.x(), c.y());
	}
	
	@Override
	public String toString() {
		return "Board breite: " + mWidth + "* höhe: " + mHeight;
	}

	public String toString(int x, int y) {
		return "Daten an der Koordinate x: " + x + ", y: " + y + ": "
				+ mField[y][x].toString();
	}

	public String toString(Coord c) {
		return toString(c.x(), c.y());
	}
}
