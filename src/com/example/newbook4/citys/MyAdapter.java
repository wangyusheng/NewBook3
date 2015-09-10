package com.example.newbook4.citys;

import java.util.List;
import com.example.newbook4.R;
import com.example.newbook4.bean.BookBean;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.R.color;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private Context context;
	private List<MyListItem> myList;
	private final LayoutInflater mInflater;

	public MyAdapter(Context context, List<MyListItem> myList) {
		this.context = context;
		this.myList = myList;
		mInflater = LayoutInflater.from(this.context);
	}

	public int getCount() {
		return myList.size();
	}

	public Object getItem(int position) {
		return myList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// public View getView(int position, View convertView, ViewGroup parent) {
	// MyListItem myListItem = myList.get(position);
	// return new MyAdapterView(this.context, myListItem);
	// }
	//
	// class MyAdapterView extends LinearLayout {
	// public static final String LOG_TAG = "MyAdapterView";
	//
	// public MyAdapterView(Context context, MyListItem myListItem) {
	// super(context);
	// this.setOrientation(HORIZONTAL);
	//
	// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	// 200, LayoutParams.WRAP_CONTENT);
	// params.setMargins(1, 1, 1, 1);
	//
	// TextView name = new TextView(context);
	// name.setText(myListItem.getName());
	// //name.setTextColor(color.black);
	// addView(name, params);
	//
	// LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
	// 200, LayoutParams.WRAP_CONTENT);
	// params2.setMargins(1, 1, 1, 1);
	//
	// TextView pcode = new TextView(context);
	// pcode.setText(myListItem.getPcode());
	// addView(pcode, params2);
	// pcode.setVisibility(GONE);
	//
	// }
	//
	// }
	//

	public View getView(final int position, View view, ViewGroup parent) {
		MyListItem myListItem = myList.get(position);
		ViewHolder holder = null;
		if (view == null) {
			view = mInflater.inflate(R.layout.city_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(R.id.tv);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.title.setText(myListItem.getName());
		return view;

	}

	private class ViewHolder {
		TextView title;

	}

}