package com.commonsware.cwac.tlv.demo;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.commonsware.cwac.tlv.TouchListView;

public class TouchListViewDemo extends ListActivity {
	private static String[] items = { "lorem", "ipsum", "dolor", "sit", "amet",
			"consectetuer"};
	private IconicAdapter adapter = null;
	EditText task;
	private ArrayList<String> array = new ArrayList<String>(
			Arrays.asList(items));

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		Button submit = (Button) findViewById(R.id.submit);
		task = (EditText) findViewById(R.id.task);
		TouchListView tlv = (TouchListView) getListView();
		adapter = new IconicAdapter();
		setListAdapter(adapter);

		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
		submit.setOnClickListener(new addTask());
	}

	class addTask implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			array.add(task.getText().toString());
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
			adapter.remove(adapter.getItem(which));
		}
	};

	class IconicAdapter extends ArrayAdapter<String> {
		IconicAdapter() {
			super(TouchListViewDemo.this, R.layout.row2, array);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row2, parent, false);
			}
			TextView label = (TextView) row.findViewById(R.id.label);
			label.setText(array.get(position));
			return (row);
		}
	}
}
