package org.thepaffy.kugellabyrinth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}
	
	public void play(View view) {
		Intent intent = new Intent(this, PlayActivity.class);
		startActivity(intent);
	}
	
	public void settings(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void highscore(View view) {
		Intent intent = new Intent(this, HighscoreActivity.class);
		startActivity(intent);
	}
}
