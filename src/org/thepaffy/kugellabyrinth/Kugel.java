package org.thepaffy.kugellabyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Kugel {

	/** x of the ball center */
	private double mX;
	/** y of the ball center */
	private double mY;

	/** x speed of the ball */
	private double mVx = 0;
	/** y speed of the ball */
	private double mVy = 0;

	/** current x accel */
	private double mAx = 0;
	/** current y accel */
	private double mAy = 0;

	private long mLastTime;

	private final Context mContext;

	private final Canvas mCanvas;

	private final Drawable mKugelImage;
	private final int mKugelWidth;
	private final int mKugelHeight;

	private final Sensor mSensor;
	private final SensorEventListener mSensorEventListener;

	public Kugel(double x, double y, Context context, Canvas canvas) {
		mX = x;
		mY = y;
		mContext = context;
		mCanvas = canvas;
		SensorManager sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mKugelImage = mContext.getResources().getDrawable(R.drawable.kugel);
		mKugelHeight = mKugelImage.getIntrinsicHeight();
		mKugelWidth = mKugelImage.getIntrinsicWidth();

		mSensorEventListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					mAx = event.values[0];
					mAy = event.values[1];
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
			}
		};
	}

	private void updatePhysics() {
		long now = System.currentTimeMillis();

		if (mLastTime > now)
			return;

		double elapsed = (mLastTime - now) / 1000.0;

		mVx = mVx + mAx * elapsed;
		mVy = mVy + mAy * elapsed;

		if (mX >= 0 && mX < mCanvas.getWidth()) {
			mX = mX + mVx * elapsed;
		}
		if (mY >= 0 && mY < mCanvas.getHeight()) {
			mY = mY + mVy * elapsed;
		}

		mLastTime = now;
	}

	private void doDraw() {

	}

}
