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
		taskTable += ",comments_count integer";
		taskTable += ",completed_at text";
		taskTable += ",memo text";
		taskTable += ",scheduled_at text";
		taskTable += ",task_type integer";
		taskTable += ",approval_flg integer";
		taskTable += ",title text not null";
		taskTable += ",updated_at text";
		taskTable += ",recommend_user_id text";
		taskTable += ",created_at text";
		taskTable += ",public text";
		taskTable += ",recommend_user_name text";
		taskTable += ",seq integer";
		taskTable += ")";
		
		String comments = "";
		comments += "create table comments (";
		comments += "id integer primary key AUTOINCREMENT";
		comments += ", messeage text";
		comments += ", user_id text";
		comments += ", user_name text";
		comments += ", user_image text";
		
		db.execSQL(taskTable);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}