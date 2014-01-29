package com.anywayanyday.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DBHelper mDbHelper;
	private Context mContext;
	private SQLiteDatabase mDb;
	
	public DBManager(Context context){
		mContext = context;
		mDbHelper = new DBHelper(mContext);
	}
	
	public void open(){
		mDb = mDbHelper.getWritableDatabase();
	}
	
	public void close(){
		mDbHelper.close();
	}
	
	public Cursor getCursor() {
		return mDb.query(DBHelper.TABLE_POINTS, null, null, null, null, null, null);
	}
	
	public Point getPoint(long id){
		Cursor cursor = null;
		try {
			cursor = mDb.query(DBHelper.TABLE_POINTS, null, DBHelper.COLUMN_ID + " = ?", new String[]{ String.valueOf(id) }, null, null, null);
			if (cursor.moveToFirst()) {
				return new Point(cursor);
			}
			
		} finally{
			if (cursor != null){
				cursor.close();
			}
		}
		
		return null;
	}
	
	public void deletePoint(long id){
		mDb.delete(DBHelper.TABLE_POINTS, DBHelper.COLUMN_ID + " = ?", new String[]{ String.valueOf(id) });
	}
	
	public void removePoint(long id){
		mDb.delete(DBHelper.TABLE_POINTS, DBHelper.COLUMN_ID + " = " + id, null);
	}
	
	public void addPoint(String name, String addr, double lat, double lon) {
		ContentValues valuse = new ContentValues();
		valuse.put(DBHelper.COLUMN_NAME, name);
		valuse.put(DBHelper.COLUMN_ADDR, addr);
		valuse.put(DBHelper.COLUMN_LAT, lat);
		valuse.put(DBHelper.COLUMN_LON, lon);
		mDb.insert(DBHelper.TABLE_POINTS, null, valuse);
	}
}
