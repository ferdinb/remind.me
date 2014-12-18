package com.f.ninaber.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.f.ninaber.android.R;
import com.f.ninaber.android.model.Calendar;

public class CalendarAdapter extends BaseAdapter{
	private List<Calendar> calendar;
	private LayoutInflater inflater;
	private Context context;
	
	public CalendarAdapter(List<Calendar> calendar, Context context){
		inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.calendar = calendar;
		this.context = context;
	}

	@Override
	public int getCount() {
		return calendar == null ? 0 : calendar.size();
	}

	@Override
	public Object getItem(int position) {
		return calendar == null ? null : calendar.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void resetData(List<Calendar> calendar){
		this.calendar = calendar;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(null == v){
			v = inflater.inflate(R.layout.adapter_calendar, null);
			CalendarHolder holder = new CalendarHolder();
			holder.day = (TextView) v.findViewById(R.id.adapter_calendar_date);
			holder.dateMonthYear = (TextView) v.findViewById(R.id.adapter_calendar_month_year);	
			holder.numTask = (TextView) v.findViewById(R.id.adapter_calendar_task);
			v.setTag(holder);
		}
		
		CalendarHolder holder = (CalendarHolder) v.getTag();
		Calendar cal = calendar.get(position);
		holder.day.setText(cal.getDay());
		holder.dateMonthYear.setText(cal.getDateMonthYear());
		holder.numTask.setText(context.getResources().getString(R.string.num_task, cal.getNumTask()));
		return v;
	}

	
	private static class CalendarHolder{
		public TextView day;
		public TextView dateMonthYear;
		public TextView numTask;
	}
}
