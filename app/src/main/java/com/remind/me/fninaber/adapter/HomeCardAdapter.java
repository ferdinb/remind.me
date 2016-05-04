package com.remind.me.fninaber.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.remind.me.fninaber.R;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.model.Type;
import com.remind.me.fninaber.util.DateUtil;
import com.remind.me.fninaber.util.RoundedSelectedTransform;
import com.remind.me.fninaber.widget.FninaberText;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by f.ninaber on 29/04/2016.
 */
public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.ViewHolder> {
    private List<Task> tasks;
    private Context context;

    public HomeCardAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = tasks.get(position);

        long timestamp = task.getTimestamp();
        String day = DateUtil.calendarDayAdapter(timestamp);
        holder.mDate.setText(day.substring(0, day.indexOf(" ")));
        holder.mMonth.setText(day.substring(day.indexOf(" "), day.lastIndexOf(" ")));
        holder.mYear.setText(day.substring(day.lastIndexOf(" "), day.length()));
        holder.mDay.setText(DateUtil.dayTimestamp(timestamp));
        holder.mTime.setText(DateUtil.timeTimestamp(timestamp));
        holder.mTitle.setText(task.getTitle());

        if (timestamp < System.currentTimeMillis()) {
            holder.mExpired.setVisibility(View.VISIBLE);
            holder.mRemaining.setVisibility(View.INVISIBLE);
        } else {
            holder.mExpired.setVisibility(View.GONE);
            holder.mRemaining.setVisibility(View.VISIBLE);
            holder.mRemaining.setText(DateUtil.remainingTime(context, timestamp));
        }

        if (task.getType().equalsIgnoreCase(Type.AUDIO.toString())) {
            holder.mImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(R.drawable.img_coffee).transform(new RoundedSelectedTransform((int) context.getResources().getDimension(R.dimen.padding_size_5dp))).into(holder.mImage);
        } else if (task.getType().equalsIgnoreCase(Type.PHOTO.toString())) {
            holder.mImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(task.getPath()).centerCrop().fit().transform(new RoundedSelectedTransform((int) context.getResources().getDimension(R.dimen.padding_size_5dp))).into(holder.mImage);
        } else {
            holder.mImage.setVisibility(View.GONE);
        }

        String sNotes = task.getNotes();
        if (!TextUtils.isEmpty(sNotes)) {
            holder.mNotes.setVisibility(View.VISIBLE);
            String text = String.format(context.getResources().getString(R.string.notes_desc), sNotes);
            SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.blue));
            StyleSpan styleSpan = new StyleSpan(Typeface.NORMAL);
            spannableBuilder.setSpan(styleSpan, 0, text.indexOf(":"), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            spannableBuilder.setSpan(colorSpan, 0, text.indexOf(":"), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            holder.mNotes.setText(spannableBuilder);
        } else {
            holder.mNotes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FninaberText mTime;
        public FninaberText mTitle;
        public FninaberText mNotes;
        public FninaberText mDate;
        public FninaberText mMonth;
        public FninaberText mYear;
        public FninaberText mDay;
        public FninaberText mRemaining;
        public ImageView mImage;
        public ImageView mExpired;

        public ViewHolder(View v) {
            super(v);
            mTime = (FninaberText) v.findViewById(R.id.adapter_home_time);
            mTitle = (FninaberText) v.findViewById(R.id.adapter_home_title);
            mNotes = (FninaberText) v.findViewById(R.id.adapter_home_notes);
            mDate = (FninaberText) v.findViewById(R.id.adapter_home_date);
            mMonth = (FninaberText) v.findViewById(R.id.adapter_home_month);
            mYear = (FninaberText) v.findViewById(R.id.adapter_home_year);
            mDay = (FninaberText) v.findViewById(R.id.adapter_home_day);
            mRemaining = (FninaberText) v.findViewById(R.id.adapter_home_day_remaining);
            mImage = (ImageView) v.findViewById(R.id.adapter_home_image);
            mExpired = (ImageView) v.findViewById(R.id.adapter_home_expired);
        }
    }
}
