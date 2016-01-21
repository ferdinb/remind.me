package com.remind.me.fninaber.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.remind.me.fninaber.R;
import com.remind.me.fninaber.util.ScreenUtil;

public class PINAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private int[] num = new int[9];
	private int numWidth;

	public PINAdapter(Activity activity) {
		numWidth = ScreenUtil.width(activity) / 5;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < num.length; i++) {
			num[i] = i + 1;
		}
	}

	@Override
	public int getCount() {
		return num.length;
	}

	@Override
	public Object getItem(int position) {
		return position < num.length ? num[position] : num[0];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (null == v) {
			v = inflater.inflate(R.layout.adapter_pin, null);
			PINHolder holder = new PINHolder();
			holder.number = (TextView) v.findViewById(R.id.adapter_pin_text);
			v.setTag(holder);

		}

		PINHolder holder = (PINHolder) v.getTag();
		int value = num[position];
		holder.number.setText(String.valueOf(value));
		return v;
	}

	private static class PINHolder {
		public TextView number;
	}
}
