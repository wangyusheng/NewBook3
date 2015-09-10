package com.example.newbook4.adapter;

import java.util.List;

import com.example.newbook4.R;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.wdiget.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class SubjectListAdapter extends BaseAdapter {
	private static final String TAG = "SubjectListAdapter";
	private Context mContext;
	private List<BookBean> mList;
	private String mAddress;
	private final LayoutInflater mInflater;
	private Typeface type = null;

	public SubjectListAdapter(Context context, List<BookBean> list,
			MyListView listView, String address) {
		Log.d(TAG, "SubjectListAdapter");
		mContext = context;
		mList = list;
		mAddress = address;
		type = Typeface.createFromAsset(context.getAssets(), "fonts/pop.ttf");
		mInflater = LayoutInflater.from(mContext);

	}

	@Override
	public int getCount() {
		//Log.d(TAG, "getCount" + mList.size());
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		//Log.d(TAG, "getItem" + position);
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		//Log.d(TAG, "getItemId" + position);
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if (view == null) {
			view = mInflater.inflate(R.layout.listview_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(R.id.title);
			holder.msg = (TextView) view.findViewById(R.id.msg);
			holder.msg.setTypeface(type);
			holder.time = (TextView) view.findViewById(R.id.time);
			holder.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
			holder.icon = (ImageView) view.findViewById(R.id.icon);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		BookBean newBook = mList.get(position);
		holder.title.setText(newBook.book_Name);
		holder.msg.setText(newBook.interest + ":" + newBook.transcation);
		holder.time.setText(newBook.generation_time);
		holder.ratingBar.setRating(newBook.rating);
		String imgUrl = mAddress + newBook.picture_Path;
		// 使用高手写的东西加载图片
		ImageLoader.getInstance().displayImage(imgUrl, holder.icon);

		return view;

	}

	private class ViewHolder {
		ImageView icon;
		TextView title;
		TextView time;
		TextView msg;
		RatingBar ratingBar;

	}

}
