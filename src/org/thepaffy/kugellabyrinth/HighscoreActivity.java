package org.thepaffy.kugellabyrinth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class HighscoreActivity extends Activity {

	/** Log tag constant */
	@SuppressWarnings("unused")
	private static final String TAG = "HighscoreActivity";

	/** Key constant for last name */
	private static final String KEY_LAST_NAME = "lastname";

	private EditText mNameEdit;
	private TextView mTimeView;
	private Button mSubmitButton;
	private ListView mListView;

	private SharedPreferences mSharedPreferences;

	private DatabaseOpenHelper mDatabaseOpenHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);

		mNameEdit = (EditText) findViewById(R.id.nameEdit);
		mTimeView = (TextView) findViewById(R.id.timeView);
		mSubmitButton = (Button) findViewById(R.id.nameSubmit);
		mListView = (ListView) findViewById(R.id.listView);

		mSharedPreferences = getPreferences(0);

		mDatabaseOpenHelper = new DatabaseOpenHelper(this);

		Intent intent = getIntent();
		if (intent.hasExtra(Kugel.KEY_TIME)) {
			long playTime = intent.getLongExtra(Kugel.KEY_TIME, 0);
			long secs = playTime % 60;
			long mins = (playTime - secs) / 60;
			mTimeView.setText(mins + ":" + secs);
			mNameEdit.setEnabled(true);
			if (mSharedPreferences.contains(KEY_LAST_NAME)) {
				mNameEdit.setText(mSharedPreferences.getString(KEY_LAST_NAME,
						""));
				mSubmitButton.setEnabled(true);
			}

			mSubmitButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Map<String, String> highscore = new HashMap<String, String>(
							2);
					highscore.put(DatabaseOpenHelper.KEY_NAME, mNameEdit
							.getText().toString());
					highscore.put(DatabaseOpenHelper.KEY_TIME, mTimeView
							.getText().toString());
					mDatabaseOpenHelper.addHighscore(highscore);
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putString(KEY_LAST_NAME, mNameEdit.getText()
							.toString());
					editor.commit();
					mNameEdit.setText("");
					mTimeView.setText("");
					mSubmitButton.setEnabled(false);
					mNameEdit.setEnabled(false);
					fillListview();
				}
			});

			mNameEdit.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					if (s.length() > 0) {
						mSubmitButton.setEnabled(true);
					} else {
						mSubmitButton.setEnabled(false);
					}
				}
			});
		}

		fillListview();
	}

	private void fillListview() {
		List<Map<String, String>> highscoreList = mDatabaseOpenHelper
				.getAllHighscores();

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, highscoreList,
				android.R.layout.simple_list_item_2, new String[] {
						DatabaseOpenHelper.KEY_NAME,
						DatabaseOpenHelper.KEY_TIME }, new int[] {
						android.R.id.text2, android.R.id.text1 });

		mListView.setAdapter(simpleAdapter);
	}
}
