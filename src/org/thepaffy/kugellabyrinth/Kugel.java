package org.thepaffy.kugellabyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;

public class Kugel implements SensorEventListener {

	/** x of the ball center */
	private double mX = 0;
	/** y of the ball center */
	private double mY = 0;

	/** x speed of the ball */
	private double mVx = 0;
	/** y speed of the ball */
	private double mVy = 0;

	/** current x accel */
	private double mAx = 0;
	/** current y accel */
	private double mAy = 0;

	private Object mAccelLock = new Object();

	private long mLastTime;

	private final Context mContext;

	private final SurfaceHolder mSurfaceHolder;

	private final Drawable mKugelImage;
	private final int mKugelWidth;
	private final int mKugelHeight;

	private SensorManager mSensorManager;
	private Sensor mSensor;

	public Kugel(Context context, SurfaceHolder surfaceHolder) {
		mContext = context;
		mSurfaceHolder = surfaceHolder;

		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_GAME);

		mKugelImage = mContext.getResources().getDrawable(R.drawable.kugel);
		mKugelHeight = mKugelImage.getIntrinsicHeight();
		mKugelWidth = mKugelImage.getIntrinsicWidth();
	}

	private void updatePhysics(Canvas canvas) {
		long now = System.currentTimeMillis();

		if (mLastTime > now)
			return;

		double elapsed = (mLastTime - now) / 1000.0;

		synchronized (mAccelLock) {
			mVx = mVx + mAx * elapsed;
			mVy = mVy + mAy * elapsed;
		}

		if (mX - mKugelWidth / 2 > 0
				&& mX + mKugelWidth / 2 < canvas.getWidth()) {
			mX = mX + mVx * elapsed;
		} else {
			mX = mX - mVx * elapsed;
		}

		if (mY - mKugelHeight / 2 > 0
				&& mY + mKugelHeight / 2 < canvas.getHeight()) {
			mY = mY + mVy * elapsed;
		} else {
			mY = mY - mVy * elapsed;
		}

		mLastTime = now;
	}

	private void doDraw(Canvas canvas) {
		int xLeft = (int) mX - mKugelWidth / 2;
		int yTop = (int) mY - mKugelHeight / 2;
		canvas.save();
		mKugelImage.setBounds(xLeft, yTop, xLeft + mKugelWidth, yTop
				+ mKugelHeight);
		mKugelImage.draw(canvas);
		canvas.restore();
	}

	public void move() {
		Canvas canvas = null;
		try {
			synchronized (mSurfaceHolder) {
				canvas = mSurfaceHolder.lockCanvas(null);
				updatePhysics(canvas);
				doDraw(canvas);
			}
		} finally {
			if (canvas != null) {
				mSurfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public int kugelWidth() {
		return mKugelWidth;
	}

	public int kugelHeight() {
		return mKugelHeight;
	}

	public void setStartPos(double x, double y) {
		mX = x;
		mY = y;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (mAccelLock) {
			mAx = event.values[0];
			mAy = event.values[1];
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
