package org.thepaffy.kugellabyrinth;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.view.SurfaceHolder;

public class Kugel implements SensorEventListener {

	@SuppressWarnings("unused")
	private static final String TAG = "Kugel";

	public static final String TIME = "TIME";

	private static final long VIB_DURATION = 200;

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

	private Context mContext;
	private SurfaceHolder mSurfaceHolder;

	private Drawable mKugelImage;
	private int mKugelWidth;
	private int mKugelHeight;
	private int mKugelRadius;

	private Bitmap mBackgroundImage;
	private int mCanvasWidth = 1;
	private int mCanvasHeight = 1;

	private boolean mRun = false;
	private Object mRunLock = new Object();

	private boolean mPause = false;

	private boolean mEnded = false;

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private Vibrator mVibrator;

	private ArrayList<Baulk> mBaulkList;

	public Kugel(Context context, SurfaceHolder surfaceHolder) {
		mContext = context;
		mSurfaceHolder = surfaceHolder;

		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_GAME);

		mVibrator = (Vibrator) mContext
				.getSystemService(Context.VIBRATOR_SERVICE);

		Resources res = mContext.getResources();
		mKugelImage = res.getDrawable(R.drawable.kugel);
		mKugelHeight = mKugelImage.getIntrinsicHeight() / 2;
		mKugelWidth = mKugelImage.getIntrinsicWidth() / 2;
		mKugelRadius = (mKugelHeight + mKugelWidth) / 4; // average
		mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.brett);

		mBaulkList = new ArrayList<Baulk>();
	}

	public void process() {
		doStart();
		while (mRun) {
			while (mPause && mRun) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {

				}
			}
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

	public void pause(boolean b) {
		synchronized (mSurfaceHolder) {
			if (!b) {
				mLastTime = System.currentTimeMillis();
			}
			mPause = b;
		}
	}

	public boolean isPaused() {
		return mPause;
	}

	public boolean isEnded() {
		return mEnded;
	}

	public void restart() {
		synchronized (mSurfaceHolder) {
			mPause = true;
			mAx = 0;
			mAy = 0;
			mVx = 0;
			mVy = 0;
			mX = mKugelWidth / 2 + 5;
			mY = mKugelHeight / 2 + 5;
			mEnded = false;
			mPause = false;
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
		double aY = event.values[0];
		double aX = event.values[1];

		if (Math.abs(aX) < 0.5 && Math.abs(mAx) < 0.5) {
			mAx = 0;
		} else {
			mAx = 4 * aX;
		}

		if (Math.abs(aY) < 0.5 && Math.abs(mVy) < 0.5) {
			mAy = 0;
		} else {
			mAy = 4 * aY;
		}
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
		return mKugelWidth;
	}

	public int height() {
		return mKugelHeight;
	}

	public void reflectX() {
		mX = mX + (mVx / -Math.abs(mVx)) * 2;
		mVx *= -0.25;
		mVibrator.vibrate(VIB_DURATION);
	}

	public void reflectY() {
		mY = mY + (mVy / -Math.abs(mVy)) * 2;
		mVy *= -0.25;
		mVibrator.vibrate(VIB_DURATION);
	}

	public void inHole(boolean end) {
		long now = System.currentTimeMillis();
		long playTime = (now - mStartTime) / 1000;
		if (end) {
			mPause = true;
			Intent intent = new Intent(mContext, HighscoreActivity.class);
			intent.putExtra(TIME, playTime);
			mContext.startActivity(intent);
		} else {
			restart();
		}
	}

	private void doStart() {
		synchronized (mSurfaceHolder) {
			mX = mKugelWidth / 2 + 5;
			mY = mKugelHeight / 2 + 5;
			fillBaulkList();
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
		if (mX - mKugelWidth / 2 <= 0 || mX + mKugelWidth / 2 >= mCanvasWidth) {
			reflectX();
		}
		if (mY - mKugelHeight / 2 <= 0
				|| mY + mKugelHeight / 2 >= mCanvasHeight) {
			reflectY();
		}

		for (int i = 0; i < mBaulkList.size(); i++) {
			mBaulkList.get(i).calcDistance(this);
		}

		mX = mX + mVx * elapsed + 0.5 * mAx * elapsed * elapsed;
		mY = mY + mVy * elapsed + 0.5 * mAy * elapsed * elapsed;

		// Save current speed for next round
		mVx = mVx + mAx * elapsed;
		mVy = mVy + mAy * elapsed;

		// Log.d(TAG, "Ax: " + mAx + ", Ay: " + mAy + "\nVx: " + mVx + ", Vy: "
		// + mVy);

		mLastTime = now;
	}

	private void doDraw(Canvas canvas) {
		canvas.drawBitmap(mBackgroundImage, 0, 0, null);

		for (int i = 0; i < mBaulkList.size(); i++) {
			mBaulkList.get(i).draw(canvas);
		}

		int xLeft = (int) mX - mKugelWidth / 2;
		int yTop = (int) mY - mKugelHeight / 2;
		canvas.save();
		mKugelImage.setBounds(xLeft, yTop, xLeft + mKugelWidth, yTop
				+ mKugelHeight);
		mKugelImage.draw(canvas);
		canvas.restore();
	}

	private void fillBaulkList() {
		mBaulkList.clear();
		int left = 0;
		int right = mCanvasWidth - 2 * mKugelWidth;
		int top = mCanvasHeight / 3 - 10;
		int bottom = mCanvasHeight / 3;
		Baulk b = new Wall(left, right, top, bottom);
		mBaulkList.add(b);
		left = 2 * mKugelWidth;
		right = mCanvasWidth;
		top = mCanvasHeight * 2 / 3;
		bottom = mCanvasHeight * 2 / 3 + 10;
		b = new Wall(left, right, top, bottom);
		mBaulkList.add(b);
		int xCenter = mCanvasWidth - mKugelWidth / 2 - 6;
		int yCenter = mCanvasHeight - mKugelHeight / 2 - 6;
		b = new EndHole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);
	}
}
