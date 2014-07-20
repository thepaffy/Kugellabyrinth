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

public class Kugel implements SensorEventListener, Runnable {

	/** Log tag constant */
	@SuppressWarnings("unused")
	private static final String TAG = "Kugel";

	/** Key constant for time */
	public static final String KEY_TIME = "TIME";

	/** Duration constant for vibration */
	private static final long VIB_DURATION = 200;

	/** x of the ball center */
	private double mX = 0;
	/** y of the ball center */
	private double mY = 0;

	/** x speed of the ball */
	private double mVx = 0;
	/** y speed of the ball */
	private double mVy = 0;

	/** x accel of the ball */
	private double mAx = 0;
	/** y accel of the ball */
	private double mAy = 0;

	/** last loop time */
	private long mLastTime;
	/** start time */
	private long mStartTime;

	/** Handle to the application context, used to e.g. fetch Drawables. */
	private Context mContext;
	/** Handle to the surface manager object we interact with */
	private SurfaceHolder mSurfaceHolder;

	/** The drawable of the Kugel */
	private Drawable mKugelImage;
	/** Pixel width of the Kugel */
	private int mKugelWidth;
	/** Pixel heigth of the Kugel */
	private int mKugelHeight;
	/** Radius of the Kugel as average of width and height */
	private int mKugelRadius;

	/** The drawable to use as the background of the animation canvas */
	private Bitmap mBackgroundImage;
	/** Current width of the surface/canvas */
	private int mCanvasWidth = 1;
	/** Current height of the surface/canvas */
	private int mCanvasHeight = 1;

	/** Run variable */
	private boolean mRun = false;
	/** Syncro lock for mRun */
	private Object mRunLock = new Object();
	/** Pause variable */
	private boolean mPause = false;

	/** Sensormanager for accelerometer */
	private SensorManager mSensorManager;
	/** Accelerometer */
	private Sensor mSensor;

	/** Vibrator */
	private Vibrator mVibrator;

	/**  */
	private ArrayList<Baulk> mBaulkList;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param surfaceHolder
	 */
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

		mBaulkList = new ArrayList<Baulk>(0);
	}

	@Override
	public void run() {
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

	public void setPaused(boolean b) {
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

	/** Restarts the game */
	public void restart() {
		synchronized (mSurfaceHolder) {
			mPause = true;
			mAx = 0;
			mAy = 0;
			mVx = 0;
			mVy = 0;
			mX = mKugelWidth / 2 + 5;
			mY = mKugelHeight / 2 + 5;
			mStartTime = System.currentTimeMillis();
			mLastTime = System.currentTimeMillis();
			mPause = false;
		}
	}

	/** Handle surfacesize change */
	public void setSurfaceSize(int width, int height) {
		synchronized (mSurfaceHolder) {
			mCanvasWidth = width;
			mCanvasHeight = height;

			mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage,
					width, height, true);
			fillBaulkList();
		}
	}

	/** Handle event from sensormanager */
	@Override
	public void onSensorChanged(SensorEvent event) {
		// Swap the axis because landscape
		double aY = event.values[0];
		double aX = event.values[1];

		// Prevent the Kugel from alone begins to roll
		if (Math.abs(aX) < 0.5 && Math.abs(mVx) == 0) {
			mAx = 0;
		} else {
			mAx = 4 * aX;
		}

		if (Math.abs(aY) < 0.5 && Math.abs(mVy) == 0) {
			mAy = 0;
		} else {
			mAy = 4 * aY;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
		// Reset the Kugel from the baulk
		mX = mX + (mVx / -Math.abs(mVx));
		mVx *= -0.25;
		mVibrator.vibrate(VIB_DURATION);
	}

	public void reflectY() {
		mY = mY + (mVy / -Math.abs(mVy));
		mVy *= -0.25;
		mVibrator.vibrate(VIB_DURATION);
	}

	public void inHole(boolean win) {
		long now = System.currentTimeMillis();
		long playTime = (now - mStartTime) / 1000;
		if (win) {
			Intent intent = new Intent(mContext, HighscoreActivity.class);
			intent.putExtra(KEY_TIME, playTime);
			mContext.startActivity(intent);
			mRun = false;
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

		// s = s_0 + v_0*t + 0.5*a*t^2
		mX = mX + mVx * elapsed + 0.5 * mAx * elapsed * elapsed;
		mY = mY + mVy * elapsed + 0.5 * mAy * elapsed * elapsed;

		// Save current speed for next round
		// v = v_0 + a*t
		mVx = mVx + mAx * elapsed;
		mVy = mVy + mAy * elapsed;

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

		left = mCanvasWidth / 3;
		right = mCanvasWidth / 3 + 10;
		top = 0;
		bottom = (int) (mCanvasHeight / 3 - 10 - mKugelHeight * 1.5);
		b = new Wall(left, right, top, bottom);
		mBaulkList.add(b);

		xCenter = (int) (mCanvasWidth - 1.75 * mKugelWidth);
		yCenter = mCanvasHeight / 3 - 15 - mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		xCenter = mKugelWidth / 2 + 5;
		yCenter = mCanvasHeight / 3 + 5 + mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		xCenter = (int) (mCanvasWidth - 2 * mKugelWidth);
		yCenter = mCanvasHeight / 3 + 15 + mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		left = mCanvasWidth * 2 / 3 - 10;
		right = mCanvasWidth * 2 / 3;
		top = (int) (mCanvasHeight / 3 + 1.5 * mKugelHeight);
		bottom = mCanvasHeight * 2 / 3;
		b = new Wall(left, right, top, bottom);
		mBaulkList.add(b);

		xCenter = mCanvasWidth * 2 / 3 + 5 + mKugelWidth / 2;
		yCenter = mCanvasHeight * 2 / 3 - 5 - mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		xCenter = mKugelWidth * 3;
		yCenter = mCanvasHeight * 2 / 3 - 5 - mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		xCenter = mKugelWidth / 2 + 5;
		yCenter = mCanvasHeight - 5 - mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		xCenter = mCanvasWidth / 2;
		yCenter = mCanvasHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		left = mCanvasWidth / 2 - 5;
		right = mCanvasWidth / 2 + 5;
		top = (int) (mCanvasHeight * 2 / 3 + 10 + 1.5 * mKugelHeight);
		bottom = mCanvasHeight;
		b = new Wall(left, right, top, bottom);
		mBaulkList.add(b);

		xCenter = mCanvasWidth / 2 - 10 - mKugelWidth / 2;
		yCenter = mCanvasHeight - 5 - mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		xCenter = mCanvasWidth / 2 + 10 + mKugelWidth / 2;
		yCenter = mCanvasHeight - 5 - mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		xCenter = mCanvasWidth / 4;
		yCenter = mCanvasHeight * 2 / 3 + 15 + mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);

		xCenter = mCanvasWidth * 3 / 4;
		yCenter = mCanvasHeight * 2 / 3 + 15 + mKugelHeight / 2;
		b = new Hole(xCenter, yCenter, mKugelRadius + 2);
		mBaulkList.add(b);
	}
}
