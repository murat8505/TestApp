package com.anywayanyday.testapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "mydb";
	private static final int DB_VERSION = 1;
	
	public static final String TABLE_POINTS = "points";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ADDR = "address";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LON = "lon";
	
	private static final String DB_CREATE = 
			"create table " + TABLE_POINTS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text, "
			+ COLUMN_ADDR + " text, "
			+ COLUMN_LAT + " real, "
			+ COLUMN_LON + " real" + ");";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
