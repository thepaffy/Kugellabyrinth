package org.thepaffy.kugellabyrinth;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "kugellabyrinth";
	private static final String HIGHSCORE_TABLE_NAME = "highscore";
	private static final String KEY_ID = "_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_TIME = "time";
	private static final String HIGHSCORE_TABLE_CREATE = "CREATE TABLE "
			+ HIGHSCORE_TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
			+ KEY_NAME + " TEXT, " + KEY_TIME + " TEXT);";

	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(HIGHSCORE_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + HIGHSCORE_TABLE_NAME);
		onCreate(db);
	}

	public void addHighscore(Highscore highscore) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, highscore.name());
		contentValues.put(KEY_TIME, highscore.time());

		db.insert(HIGHSCORE_TABLE_NAME, null, contentValues);
		db.close();
	}

	public Highscore getHighscoreById(int id) {
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.query(HIGHSCORE_TABLE_NAME, new String[] { KEY_ID,
				KEY_NAME, KEY_TIME }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		Highscore highscore = new Highscore(cursor.getString(1),
				cursor.getString(2));
		return highscore;
	}

	public List<Highscore> getAllHighscores() {
		List<Highscore> highscoreList = new ArrayList<Highscore>();
		String selectQuery = "SELECT  * FROM " + HIGHSCORE_TABLE_NAME;

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				highscoreList.add(new Highscore(cursor.getString(1), cursor
						.getString(2)));
			} while (cursor.moveToNext());
		}

		return highscoreList;
	}

	public int getHighscoreCount() {
		String countQuery = "SELCET * FROM " + HIGHSCORE_TABLE_NAME;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		return cursor.getCount();
	}

}
