package jp.emitteeer.countdowntimetable;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimeLineListAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater = null;
//	private Context context = null;
	private List<TimeLine> timeLines = null;

	public TimeLineListAdapter(Context context, List<TimeLine> timeLines) {
		this.layoutInflater = LayoutInflater.from(context);
		this.timeLines = timeLines;
	}
	
	@Override
	public int getCount() {
		return timeLines.size();
	}

	@Override
	public Object getItem(int position) {
		return timeLines.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.timeline, null);
			
			viewHolder = new ViewHolder();
//			viewHolder.ivSelected = (ImageView)convertView.findViewById(R.id.selected_icon);
			viewHolder.tvTime = (TextView)convertView.findViewById(R.id.time);
			viewHolder.tvContent = (TextView)convertView.findViewById(R.id.content);
			
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)convertView.getTag();
		}

		viewHolder.tvTime.setText(timeLines.get(position).getHourString(2) + ":" + timeLines.get(position).getMinuteString(2));
		viewHolder.tvContent.setText(timeLines.get(position).getContent());

		if (timeLines.get(position).getSelected() == true) {
			convertView.setBackgroundColor(Color.BLUE);
//			viewHolder.ivSelected.setImageResource(android.R.drawable.presence_online);
		}
		else {
			convertView.setBackgroundColor(Color.BLACK);
//			viewHolder.ivSelected.setImageResource(android.R.drawable.presence_invisible);
		}
		
		return convertView;
	}
	
	static class ViewHolder {
//		ImageView ivSelected;
		TextView tvTime;
		TextView tvContent;
	}
}