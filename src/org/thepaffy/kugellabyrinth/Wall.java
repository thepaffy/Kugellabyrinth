package org.thepaffy.kugellabyrinth;

import android.util.Log;

public class Wall implements Baulk {

	private static final String TAG = "Wall";

	private int mX;

	private int mY;

	public Wall(int x, int y) {
		mX = x;
		mY = y;
	}

	@Override
	public void handle(Kugel kugel) {
		/* Kugel with border on wall
		 * +----------+
		 * |          |
		 * |          |
		 * |     o    x
		 * |          |
		 * |          |
		 * +----------+
		 * x: wall point
		 * o: Kugel midpoint
		 *
		 * Wall in the Kugel
		 * +----------+
		 * |          |
		 * |          |
		 * |     o  x |
		 * |          |
		 * |          |
		 * +----------+
		 * x: wall point
		 * o: Kugel midpoint
		 */
		Log.d(TAG, "X: " + mX + ", Y: " + mY);
		// Translate wall coords to kugel-based coords: mX - kugel.x()
		if (Math.abs(mX - kugel.x()) <= kugel.width() / 2) {
			//Only reflect one time
			if(!kugel.isHandledX()) {
				kugel.reflectX();
			}
		}

		if (Math.abs(mY - kugel.y()) <= kugel.height() / 2) {
			if(!kugel.isHandledY()) {
				kugel.reflectY();
			}
		}
	}

	@Override
	public int x() {
		return mX;
	}

	@Override
	public int y() {
		return mY;
	}

}
