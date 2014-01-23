package com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DB_NAME = "board.db";
	private final static int DB_VER = 1;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String  taskTable = "";
		taskTable += "create table task (";
		taskTable += "id integer primary key AUTOINCREMENT";
		taskTable += ",title text not null";
		taskTable += ",memo text";
		taskTable += ",turn integer";
		taskTable += ")";
		
		db.execSQL(taskTable);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}