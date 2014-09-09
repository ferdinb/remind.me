package com.f.ninaber.android.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.f.ninaber.android.R;
import com.f.ninaber.android.db.TableTask;
import com.f.ninaber.android.db.TaskHelper;
import com.f.ninaber.android.model.Task;
import com.f.ninaber.android.util.DateUtil;
import com.f.ninaber.android.widget.ArialText;

public class TaskAdapter extends CursorAdapter {

	public TaskAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		long thisDate = cursor.getLong(cursor.getColumnIndex(TableTask.Column.TIMESTAMP));
		long prevDate = 0;
		if (cursor.getPosition() > 0 && cursor.moveToPrevious()) {
			prevDate = cursor.getLong(cursor.getColumnIndex(TableTask.Column.TIMESTAMP));
			cursor.moveToNext();
		}
		
		if (!DateUtil.getDate(thisDate).equalsIgnoreCase(DateUtil.getDate(prevDate))) {
			view.findViewById(R.id.adapter_date_group).setVisibility(View.VISIBLE);
			TextView dayView = (TextView) view.findViewById(R.id.adapter_task_day);
			if(DateUtil.getDate(System.currentTimeMillis()).contentEquals(DateUtil.getDate(thisDate))){
				dayView.setVisibility(View.VISIBLE);
				dayView.setText(context.getResources().getString(R.string.today));
			}else{
				dayView.setVisibility(View.GONE);
			}
			
		}else{
			view.findViewById(R.id.adapter_date_group).setVisibility(View.GONE);
		}
				
		if(thisDate < System.currentTimeMillis()){
			view.findViewById(R.id.adapter_task_master).setBackgroundColor(context.getResources().getColor(R.color.red_light));
		}else{
			view.findViewById(R.id.adapter_task_master).setBackgroundColor(context.getResources().getColor(R.color.blue_light));


		}
		
		ArialText timeView = (ArialText) view.findViewById(R.id.adapter_task_time);
		ArialText titleView = (ArialText) view.findViewById(R.id.adapter_task_title);
		ArialText notesView = (ArialText) view.findViewById(R.id.adapter_task_notes);
		ArialText dateView = (ArialText) view.findViewById(R.id.adapter_task_date);

		Task task = TaskHelper.getInstance().cursorToTask(cursor);
		long timestamp = task.getTimestamp();
		
		dateView.setText(DateUtil.dateTimestamp(timestamp));
		timeView.setText(DateUtil.timeTimestamp(timestamp));
		titleView.setText(task.getTitle());
		
		String sNotes = task.getNotes();
		if(!TextUtils.isEmpty(sNotes)){
			notesView.setVisibility(View.VISIBLE);
			
			String text = String.format(context.getResources().getString(R.string.notes_desc), sNotes);
			SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(text);
			ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.blue));
			StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD);			
			spannableBuilder.setSpan(styleSpan, 0, text.indexOf(":"), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			spannableBuilder.setSpan(colorSpan, 0, text.indexOf(":"), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			
			notesView.setText(spannableBuilder);			
		}else{
			notesView.setVisibility(View.GONE);
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.adapter_task_item, parent, false);
	}

}
