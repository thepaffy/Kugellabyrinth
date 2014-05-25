package org.thepaffy.kugellabyrinth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HighscoreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);
	}
	
	public void play() {
		Intent intent = new Intent(this, PlayActivity.class);
		startActivity(intent);
	}
	
	public void start() {
		Intent intent = new Intent(this, StartActivity.class);
		startActivity(intent);
	}
}
