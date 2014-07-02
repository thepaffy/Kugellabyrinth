package org.thepaffy.kugellabyrinth;

import android.graphics.Canvas;
import android.graphics.Paint;

public class EndHole extends Hole {

	private Paint mRed;

	public EndHole(int x, int y, int radius) {
		super(x, y, radius);
		mRed = new Paint();
		mRed.setAntiAlias(true);
		mRed.setARGB(255, 255, 0, 0);
	}

	@Override
	public void calcDistance(Kugel kugel) {
		double distance = Math.sqrt(Math.pow(mX - kugel.x(), 2)
				+ Math.pow(mY - kugel.y(), 2));
		if (distance < mRadius) {
			kugel.inHole(true);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(mX, mY, mRadius + 1, mRed);
		super.draw(canvas);
	}

}
