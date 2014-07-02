package org.thepaffy.kugellabyrinth;

public class Highscore {
	private String mName;
	private String mTime;

	public Highscore(String name, String time) {
		mName = name;
		mTime = time;
	}

	public String name() {
		return mName;
	}

	public String time() {
		return mTime;
	}

	public void setName(String name) {
		mName = name;
	}

	public void setTime(String time) {
		mTime = time;
	}
}
