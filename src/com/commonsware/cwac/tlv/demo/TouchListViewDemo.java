package com.commonsware.cwac.tlv.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.commonsware.cwac.tlv.TouchListView;
import com.db.DBTools;
import com.db.DatabaseHelper;

public class TouchListViewDemo extends ListActivity {
	private static String[] items = { "lorem", "ipsum", "dolor", "sit", "amet", "consectetuer"};
	private IconicAdapter adapter = null;
	SQLiteDatabase db;
	Context mContext = this;
	EditText task;
	DBTools tool;
	private ArrayList<String> array = new ArrayList<String>(Arrays.asList(items));

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		tool = new DBTools();
		db = dbHelper.getWritableDatabase();
		List<String> tasks =  tool.getAnything(db, "task", "title");

		array = (ArrayList<String>) tasks;
		
		Button submit = (Button) findViewById(R.id.submit);
		task = (EditText) findViewById(R.id.task);
		TouchListView tlv = (TouchListView) getListView();
		adapter = new IconicAdapter();
		setListAdapter(adapter);

		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
		tlv.setOnItemClickListener(new updateMemo());
		submit.setOnClickListener(new addTask());
	}

	class updateMemo implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			final int locate = arg2;
			LayoutInflater inflater = LayoutInflater.from(TouchListViewDemo.this);
			View view = inflater.inflate(R.layout.create_topic_alert, null);
			final EditText memo = (EditText) view
					.findViewById(R.id.inputMemo);

			new AlertDialog.Builder(TouchListViewDemo.this)
					.setView(view)
					.setTitle("Create Topic")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ContentValues val = new ContentValues();
									DBTools tool = new DBTools();
									String name = memo.getText().toString();
									val.put("memo", name);
									
									db.update("task", val, "turn = " + String.valueOf(locate) ,null);
									tool.searchData(db, "task");
								}
							}).show();
		}
	}
	
	class addTask implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			DBTools tool = new DBTools();
			ContentValues val = new ContentValues();
			String newTask = task.getText().toString();
			
			val.put("title", newTask);
			val.put("turn", array.size());
			db.insert("task", null, val);
			array.add(newTask);
			tool.searchData(db, "task");
			adapter = new IconicAdapter();
			setListAdapter(adapter);
		}
	}

	private TouchListView.DropListener onDrop = new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			String item = adapter.getItem(from);

			adapter.remove(item);
			adapter.insert(item, to);
		}
	};

	private TouchListView.RemoveListener onRemove = new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
			// when add turn column, search by two 
			Log.d("delete item", adapter.getItem(which));
			tool.searchData(db, "task");

			String[] t = { adapter.getItem(which) };
			db.delete("task", "title=?", t);
			tool.searchData(db, "task");
			adapter.remove(adapter.getItem(which));
		}
	};

	class IconicAdapter extends ArrayAdapter<String> {
		IconicAdapter() {
			super(TouchListViewDemo.this, R.layout.row, array);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row, parent, false);
			}
			TextView label = (TextView) row.findViewById(R.id.label);
			label.setText(array.get(position));
			
			return (row);
		}
	}
}
