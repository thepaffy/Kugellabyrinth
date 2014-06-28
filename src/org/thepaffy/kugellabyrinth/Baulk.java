package org.thepaffy.kugellabyrinth;

import android.util.Log;

public class Baulk {

	private static final String TAG = "Baulk";

	public enum BaulkType {
		WALL, HOLE, ENDHOLE
	}

	public static void handle(Kugel kugel, BaulkType baulkType, int xBaulk,
			int yBaulk) {
		switch (baulkType) {
		case WALL:
			handleWall(kugel, xBaulk, yBaulk);
			break;
		case HOLE:
			handleHole(kugel, xBaulk, yBaulk, false);
			break;
		case ENDHOLE:
			handleHole(kugel, xBaulk, xBaulk, true);
			break;
		default:
			Log.e(TAG, "Unkown BaulkType");
		}
	}

	public static void handleWall(Kugel kugel, int xWall, int yWall) {
		/*
		 * Kugel with border on wall +----------+ | | | | | o x | | | |
		 * +----------+ x: wall point o: Kugel midpoint
		 * 
		 * Wall in the Kugel +----------+ | | | | | o x | | | | | +----------+
		 * x: wall point o: Kugel midpoint
		 */
		// Log.d(TAG, "X: " + mX + ", Y: " + mY);
		// Translate wall coords to kugel-based coords: mX - kugel.x()
		if (Math.abs(xWall - kugel.x()) <= kugel.width() / 2) {
			// Only reflect one time
			if (!kugel.isHandledX()) {
				kugel.reflectX();
			}
		}

		if (Math.abs(yWall - kugel.y()) <= kugel.height() / 2) {
			if (!kugel.isHandledY()) {
				kugel.reflectY();
			}
		}
	}

	public static void handleHole(Kugel kugel, int xHole, int yHole, boolean end) {
		double distance = Math.sqrt(Math.pow(xHole - kugel.x(), 2)
				+ Math.pow(yHole - kugel.y(), 2));
		if (distance < kugel.holeRadius()) {
			kugel.inHole(end);
		}
	}
}
