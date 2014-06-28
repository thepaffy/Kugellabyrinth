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

	/** last loop time */
	private long mLastTime;
	/** start time */
	private long mStartTime;

	/** current loop handle x */
	private boolean mHandledX = false;

	/** current loop handle y */
	private boolean mHandledY = false;

	private Context mContext;
	private SurfaceHolder mSurfaceHolder;

	private Drawable mKugelImage;
	private int mKugelWidth;
	private int mKugelHeight;

	private int mHoleRadius;

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
		mKugelHeight = mKugelImage.getIntrinsicHeight() / 2;
		mKugelWidth = mKugelImage.getIntrinsicWidth() / 2;
		mHoleRadius = (mKugelWidth + mKugelHeight) / 4 + 6; // average
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
		// Swap the axis because landscape
		mAy = 4 * event.values[0];
		mAx = 4 * event.values[1];
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public int x() {
		return (int) mX;
	}

	public int y() {
		return (int) mY;
	}

	public int width() {
		return mCanvasWidth;
	}

	public int height() {
		return mCanvasHeight;
	}

	public int holeRadius() {
		return mHoleRadius;
	}

	public void reflectX() {
		mVx *= -1;
		mHandledX = true;
	}

	public void reflectY() {
		mVy *= -1;
		mHandledY = true;
	}

	public boolean isHandledX() {
		return mHandledX;
	}

	public boolean isHandledY() {
		return mHandledY;
	}

	public void inHole(boolean end) {
		long playTime = System.currentTimeMillis() - mStartTime;

	}

	private void doStart() {
		synchronized (mSurfaceHolder) {
			mX = mCanvasWidth / 2.0;
			mY = mCanvasHeight / 2.0;
			mStartTime = System.currentTimeMillis();
			mLastTime = mStartTime;
		}
	}

	private void updatePhysics() {
		long now = System.currentTimeMillis();

		if (mLastTime > now)
			return;

		double elapsed = (now - mLastTime) / 1000.0;

		// Canvas borders
		if (mX - mKugelWidth / 2 < 0 && mX + mKugelWidth / 2 > mCanvasWidth) {
			reflectX();
		}
		if (mY - mKugelHeight / 2 < 0 && mY + mKugelHeight / 2 > mCanvasHeight) {
			reflectY();
		}

		/*
		 * for (int y = 0; y < mCanvasHeight; y++) { ArrayList<Baulk> baulkList
		 * = mBaulkArray.get(y); if (baulkList != null) { for (int x = 0; x <
		 * mCanvasWidth; x++) { Baulk baulk = baulkList.get(x); if (baulk !=
		 * null) { baulk.handle(this); } } } }
		 */
		mX = mX + mVx * elapsed + 0.5 * mAx * elapsed * elapsed;
		mY = mY + mVy * elapsed + 0.5 * mAy * elapsed * elapsed;

		// Save current speed for next round
		mVx = mVx + mAx * elapsed;
		mVy = mVy + mAy * elapsed;

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
