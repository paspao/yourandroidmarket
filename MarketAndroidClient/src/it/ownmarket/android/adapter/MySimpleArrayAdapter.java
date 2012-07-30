package it.ownmarket.android.adapter;

import it.ownmarket.android.R;
import it.ownmarket.android.layout.bean.RowAppInfo;
import it.ownmarket.android.util.DownloadImageTask;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<RowAppInfo> {
	private final Activity context;
	private final RowAppInfo[] values;

	public MySimpleArrayAdapter(Activity context, RowAppInfo[] values) {
		super(context, R.layout.row_app_layout, values);
		this.context = context;
		this.values = values;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.row_app_layout, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.textAppTitle = (TextView) rowView.findViewById(R.id.apptitle);
			viewHolder.textAppVersion = (TextView) rowView.findViewById(R.id.appversion);
			viewHolder.textAppStatus = (TextView) rowView.findViewById(R.id.appstatus);
			viewHolder.imageAppIcon = (ImageView) rowView.findViewById(R.id.appicon);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		RowAppInfo s = values[position];
		if(s!=null)
		{
			holder.textAppTitle.setText(s.getTitle());
			holder.textAppVersion.setText(s.getVersion());
			holder.textAppStatus.setText(s.getStatus());
			new DownloadImageTask(holder.imageAppIcon)
            .execute(s.getIconUrl());
		}
		return rowView;
	}
	
	static class ViewHolder{
		private TextView textAppTitle;
		private TextView textAppVersion;
		private TextView textAppStatus;
		private ImageView imageAppIcon;
	}
}
