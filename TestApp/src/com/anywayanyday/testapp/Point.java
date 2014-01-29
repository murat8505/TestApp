package com.anywayanyday.testapp;

import android.database.Cursor;

public class Point {
	public long id;
	public String name;
	public String addr;
	public double lat;
	public double lon;
	
	public Point(Cursor cursor){
		id = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID));
		name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
		addr = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ADDR));
		lat = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_LAT));
		lon = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_LON));
	}
}
