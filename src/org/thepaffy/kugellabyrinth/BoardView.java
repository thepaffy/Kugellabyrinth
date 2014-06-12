package org.thepaffy.kugellabyrinth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {

	private Kugel mKugel;

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		mKugel = new Kugel(context, surfaceHolder);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mKugel.setRunning(true);
		mKugel.start();
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
				mKugel.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
