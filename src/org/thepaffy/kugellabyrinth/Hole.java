package org.thepaffy.kugellabyrinth;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Hole implements Baulk {

	protected int mX;
	protected int mY;
	protected int mRadius;

	private Paint mBlack;

	public Hole(int x, int y, int radius) {
		mX = x;
		mY = y;
		mRadius = radius;
		mBlack = new Paint();
		mBlack.setAntiAlias(true);
		mBlack.setARGB(255, 0, 0, 0);
	}

	@Override
	public void calcDistance(Kugel kugel) {
		double distance = Math.sqrt(Math.pow(mX - kugel.x(), 2)
				+ Math.pow(mY - kugel.y(), 2));
		if (distance < mRadius) {
			kugel.inHole(false);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(mX, mY, mRadius, mBlack);
	}
}
