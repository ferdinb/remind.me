package com.remind.me.fninaber.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.remind.me.fninaber.R;
import com.remind.me.fninaber.model.LeftMenu;

public class LeftAdapter extends BaseAdapter{
	private List<LeftMenu> menus;
	private LayoutInflater inflater;
	
	public LeftAdapter(List<LeftMenu> menu, Context context){
		inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.menus = menu;
	}

	@Override
	public int getCount() {
		return menus == null ? 0 : menus.size();
	}

	@Override
	public Object getItem(int position) {
		return menus == null ? null : menus.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(null == v){
			v = inflater.inflate(R.layout.adapter_left_menu, null);
			LeftMenuHolder holder = new LeftMenuHolder();
			holder.icon = (ImageView) v.findViewById(R.id.left_menu_icon);
			holder.title = (TextView) v.findViewById(R.id.left_menu_text);	
			v.setTag(holder);
		}
		
		LeftMenuHolder holder = (LeftMenuHolder) v.getTag();
		LeftMenu leftMenu = menus.get(position);
		holder.icon.setImageResource(leftMenu.getResource());
		holder.title.setText(leftMenu.getTitle());
		
		return v;
	}

	
	private static class LeftMenuHolder{
		public ImageView icon;
		public TextView title;
	}
}
