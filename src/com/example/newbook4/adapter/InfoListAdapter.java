package com.example.newbook4.adapter;

import java.util.List;

import com.example.newbook4.R;

import com.example.newbook4.bean.InfoBean;
import com.example.newbook4.bean.InfoBean;
import com.example.newbook4.wdiget.MyListView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InfoListAdapter extends BaseAdapter {
	private static final String TAG = "SubjectListAdapter";
	private Context mContext;
	private List<InfoBean> mList;
	private String mAddress;
	private final LayoutInflater mInflater;

	public InfoListAdapter(Context context, List<InfoBean> list,
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
			view = mInflater.inflate(R.layout.info_listitem, null);
			holder = new ViewHolder();
			holder.title=(TextView) view.findViewById(R.id.bookname);
			holder.msg=(TextView) view.findViewById(R.id.msg);
			holder.price=(TextView) view.findViewById(R.id.price);
			holder.num= (TextView) view.findViewById(R.id.num);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		InfoBean infoBean = mList.get(position);
		holder.title.setText(infoBean.bookname);
	
		holder.msg.setText(infoBean.getRealAddress());
		//holder.time.setText(infoBean.);
		
		holder.num.setText("¹Ø×¢ÈËÊý:"+infoBean.concern_num);

		// TODO
		// ImageLoader.getInstance().displayImage(imgUrl, holder.icon);

		return view;

	}

	private class ViewHolder {
		TextView title;
		TextView price;
		TextView msg;
		TextView num;

	}

}
