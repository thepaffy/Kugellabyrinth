package org.thepaffy.kugellabyrinth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {

	private Kugel mKugel;
	private Runnable mRunnable;
	private Thread mThread;

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		mKugel = new Kugel(context, surfaceHolder);
		mRunnable = new Runnable() {

			@Override
			public void run() {
				mKugel.process();
			}
		};
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread = new Thread(mRunnable);
		mKugel.setRunning(true);
		mThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mKugel.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		mKugel.setRunning(false);
		while (retry) {
			try {
				mThread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void pause(boolean b) {
		mKugel.pause(b);
	}

	public boolean isPaused() {
		return mKugel.isPaused();
	}

	public void restart() {
		mKugel.restart();
	}
}
