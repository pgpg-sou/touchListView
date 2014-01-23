package com.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBTools {
	
    Cursor mCursor;
    List<String> anything;

    public DBTools() {
    	anything = new ArrayList<String>();
    	mCursor = null;
    }
    
	public static String searchData( SQLiteDatabase db, String Runner ){
        Cursor cursor = null;
        try{
            cursor = db.query( Runner, null, null, null, null, null, null );
            return readCursor(cursor);
        }
        finally{
            if( cursor != null ){
                cursor.close();
            }
        }
    }

    public static String readCursor( Cursor cursor ){
        String result = "";

        int title = cursor.getColumnIndex("title");
        int memo = cursor.getColumnIndex("memo");
        int turn = cursor.getColumnIndex("turn");

        while( cursor.moveToNext()){
        	String na = cursor.getString(title);
        	String me = cursor.getString(memo);
        	String tu = cursor.getString(turn);
            
            result = "title : " + na 
            		+ "memo : " + me 
            		+ "turn : " + tu ;
            Log.d("test",result);
        }
        Log.d("check", "ok");
        return result;
    }
    public int getColumn(SQLiteDatabase db, String table) {
    	int maxColumn = 0;
        Cursor cursor = null;
        try{
        	cursor = db.query( table , null, null, null, null, null, null );
            while( cursor.moveToNext() )
            	maxColumn++;
        }
        finally{
            if( cursor != null )
                cursor.close();
        }
    	return maxColumn;
    }
    public List<String> getAnything(SQLiteDatabase db, String table, String element) {
    	mCursor = db.query(table, new String[]{ element }, null, null, null, null, null);
    	int index = mCursor.getColumnIndex(element);
    	
    	while(mCursor.moveToNext())
    		anything.add(mCursor.getString(index));
    	mCursor.close();
    	
    	return anything;
    }
    
    public List<String> search(SQLiteDatabase db, String table, String element,String keyTitle, String key) {
    	anything = new ArrayList<String>();
    	mCursor = db.query(table, new String[] {element}, keyTitle + " = ?", new String[]{key}, null, null, null);
    	
    	int any = mCursor.getColumnIndex(element);
    	Log.d("colu", String.valueOf(any));
    	while(mCursor.moveToNext()) {
    		anything.add(mCursor.getString(any));
    	}
    	mCursor.close();
    	
    	return anything;
    }
}