package com.example.newbook4.adapter;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.newbook4.R;
import com.example.newbook4.bean.ClubBean;
import com.example.newbook4.wdiget.MyListView;

public class ClubListAdapter extends BaseAdapter {
	private static final String TAG = "SubjectListAdapter";
	private Context mContext;
	private List<ClubBean> mList;
	private String mAddress;
	private final LayoutInflater mInflater;

	public ClubListAdapter(Context context, List<ClubBean> list,
			MyListView listView, String address) {
		Log.d(TAG, "SubjectListAdapter");
		mContext = context;
		mList = list;
		mAddress = address;
		mInflater = LayoutInflater.from(mContext);

	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if (view == null) {
			view = mInflater.inflate(R.layout.club_listitem, null);
			holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(R.id.title);
			holder.msg = (TextView) view.findViewById(R.id.msg);
			holder.time = (TextView) view.findViewById(R.id.time);
			holder.num= (TextView) view.findViewById(R.id.num);
			
			
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		ClubBean clubBean = mList.get(position);
		holder.title.setText(clubBean.topic);
		holder.msg.setText(clubBean.getAddress1());
		holder.time.setText(clubBean.time);
		
		holder.num.setText("关注人数:"+clubBean.concern_num+",报名人数:"+clubBean.enroll_num);

		// TODO
		// ImageLoader.getInstance().displayImage(imgUrl, holder.icon);

		return view;

	}

	private class ViewHolder {
		TextView title;
		TextView time;
		TextView msg;
		TextView num;

	}

}
