package org.thepaffy.kugellabyrinth;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HighscoreActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = "HighscoreActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);

		final LinearLayout inputLayout = (LinearLayout) findViewById(R.id.inputLayout);
		final EditText nameEdit = (EditText) findViewById(R.id.nameEdit);
		final TextView timeView = (TextView) findViewById(R.id.timeView);
		final Button submitButton = (Button) findViewById(R.id.nameSubmit);
		final ListView listView = (ListView) findViewById(R.id.listView);

		final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(
				this);

		Intent intent = getIntent();
		if (intent.hasExtra(Kugel.TIME)) {
			long playTime = intent.getLongExtra(Kugel.TIME, 0);
			inputLayout.setVisibility(View.VISIBLE);
			submitButton.setVisibility(View.VISIBLE);
			long secs = playTime % 60;
			long mins = (playTime - secs) / 60;
			timeView.setText(mins + ":" + secs);

			nameEdit.setActivated(true);
			submitButton.setActivated(true);

			submitButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Highscore highscore = new Highscore(nameEdit.getText()
							.toString(), timeView.getText().toString());
					databaseOpenHelper.addHighscore(highscore);
				}
			});

			List<Highscore> highscoreList = databaseOpenHelper
					.getAllHighscores();
		}

	}
}
