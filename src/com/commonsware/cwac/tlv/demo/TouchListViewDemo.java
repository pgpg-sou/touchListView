package com.commonsware.cwac.tlv.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
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

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		JSONArray json;
		tool = new DBTools();
		db = dbHelper.getWritableDatabase();
		List<String> tasks = new ArrayList<String>();
		
		String url = "http://153.121.40.25:8000/task.json";
		String jsonData = getData(url);
		if(jsonData  == null) {
			Log.d("return value", "null");
		} else {
			Log.d("return value", jsonData );			
		}
		try {
			json = new JSONArray(jsonData);
			
			Log.d("jsontest", json.toString(1));
			
			for(int i = 0;i < json.length();i++) {
				JSONObject obj = json.getJSONObject(i);
				tasks.add(i, obj.getString("title"));
				Log.d("json_name", obj.getString("title"));
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
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
	
	
	public String getData(String sUrl) {
		HttpClient objHttp = new DefaultHttpClient();
		HttpParams params = objHttp.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 1000); 
		HttpConnectionParams.setSoTimeout(params, 1000); 
		String sReturn = "";
		try {
			HttpGet objGet = new HttpGet(sUrl);
			HttpResponse objResponse = objHttp.execute(objGet);
			if (objResponse.getStatusLine().getStatusCode() < 400) {
				InputStream objStream = objResponse.getEntity().getContent();
				InputStreamReader objReader = new InputStreamReader(objStream);
				BufferedReader objBuf = new BufferedReader(objReader);
				StringBuilder objJson = new StringBuilder();
				String sLine;
				while ((sLine = objBuf.readLine()) != null) {
					objJson.append(sLine);
				}
				sReturn = objJson.toString();
				objStream.close();
			}
		} catch (IOException e) {
			return null;
		}
		
//		return sReturn.substring(1, sReturn.length() - 1);
		return sReturn;
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
			val.put("comments_count", 0);
			val.put("memo", "");
			val.put("scheduled_at", "");
			val.put("task_type", 0);
			val.put("approbal_flg", 0);
			val.put("updated_at", "");
			val.put("recommend_user_id", "");
			val.put("reccomend_user_name", "");
			val.put("created_at", "");
			val.put("public", "");
			val.put("seq", 0);
			val.put("completed_at", "");
			
			db.insert("task", null, val);
			array.add(newTask);
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
