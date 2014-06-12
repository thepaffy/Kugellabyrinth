package org.thepaffy.kugellabyrinth;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.SurfaceHolder;

public class Kugel extends Thread implements SensorEventListener {

	private static final String TAG = "Kugel";

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

	private long mLastTime;

	private Context mContext;
	private SurfaceHolder mSurfaceHolder;

	private Drawable mKugelImage;
	private int mKugelWidth;
	private int mKugelHeight;

	private Bitmap mBackgroundImage;
	private int mCanvasWidth = 1;
	private int mCanvasHeight = 1;

	private boolean mRun = false;
	private Object mRunLock = new Object();

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

		Resources res = mContext.getResources();
		mKugelImage = res.getDrawable(R.drawable.kugel);
		mKugelHeight = mKugelImage.getIntrinsicHeight();
		mKugelWidth = mKugelImage.getIntrinsicWidth();
		mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.brett);
	}

	@Override
	public void run() {
		doStart();
		while (mRun) {
			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					updatePhysics();
					synchronized (mRunLock) {
						if (mRun)
							doDraw(c);
					}
				}
			} finally {
				if (c != null) {
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}

	public void setRunning(boolean b) {
		synchronized (mRunLock) {
			mRun = b;
		}
	}

	public void setSurfaceSize(int width, int height) {
		synchronized (mSurfaceHolder) {
			mCanvasWidth = width;
			mCanvasHeight = height;

			mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage,
					width, height, true);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mAy = event.values[0];
		mAx = event.values[1];
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	private void doStart() {
		synchronized (mSurfaceHolder) {
			mX = mCanvasWidth / 2.0;
			mY = mCanvasHeight / 2.0;
			mLastTime = System.currentTimeMillis();
		}
	}

	private void updatePhysics() {
		long now = System.currentTimeMillis();

		if (mLastTime > now)
			return;

		double elapsed = (now - mLastTime) / 1000.0;

		mVx = mVx + mAx * elapsed;
		mVy = mVy + mAy * elapsed;

		if (mX - mKugelWidth / 2 > 0 && mX + mKugelWidth / 2 < mCanvasWidth) {
			mX = mX + mVx * elapsed;
		} else {
			mX = mX - mVx * elapsed;
			mVx *= -1;
		}

		if (mY - mKugelHeight / 2 > 0 && mY + mKugelHeight / 2 < mCanvasHeight) {
			mY = mY + mVy * elapsed;
		} else {
			mY = mY - mVy * elapsed;
			mVy *= -1;
		}

		Log.d(TAG, "Ax: " + mAx + ", Ay: " + mAy + "\nVx: " + mVx + ", Vy: "
				+ mVy);

		mLastTime = now;
	}

	private void doDraw(Canvas canvas) {
		canvas.drawBitmap(mBackgroundImage, 0, 0, null);

		int xLeft = (int) mX - mKugelWidth / 2;
		int yTop = (int) mY - mKugelHeight / 2;
		canvas.save();
		mKugelImage.setBounds(xLeft, yTop, xLeft + mKugelWidth, yTop
				+ mKugelHeight);
		mKugelImage.draw(canvas);
		canvas.restore();
	}

}
