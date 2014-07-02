package org.thepaffy.kugellabyrinth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Wall implements Baulk {

	@SuppressWarnings("unused")
	private static final String TAG = "Wall";

	private static final double EPS = 0.05;

	/** wall width */
	private int mWidth;
	/** wall height */
	private int mHeight;

	/** wall middle line x */
	private int mX;
	/** wall middle line y */
	private int mY;

	private RectF mWallRect;

	private Paint mBlack;

	public Wall(int left, int right, int top, int bottom) {
		mWidth = right - left;
		mHeight = bottom - top;
		mX = left + mWidth / 2;
		mY = top + mHeight / 2;
		mWallRect = new RectF(left, top, right, bottom);
		mBlack = new Paint();
		mBlack.setAntiAlias(true);
		mBlack.setARGB(255, 0, 0, 0);
	}

	@Override
	public void calcDistance(Kugel kugel) {
		if (Math.abs(kugel.y() - mY) <= kugel.height() / 2 + mHeight / 2
				&& Math.abs(kugel.x() - mX) > kugel.width() / 2 + (1 - EPS)
						* mWidth / 2) {
			if (Math.abs(kugel.x() - mX) <= kugel.width() / 2 + mWidth / 2) {
				kugel.reflectX();
				// Log.d(TAG, "reflectX()");
			}
		}

		if (Math.abs(kugel.x() - mX) <= kugel.width() / 2 + mWidth / 2
				&& Math.abs(kugel.y() - mY) > kugel.height() / 2 + (1 - EPS)
						* mHeight / 2) {
			if (Math.abs(kugel.y() - mY) <= kugel.height() / 2 + mHeight / 2) {
				kugel.reflectY();
				// Log.d(TAG, "reflectY()");
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(mWallRect, mBlack);
	}

}
