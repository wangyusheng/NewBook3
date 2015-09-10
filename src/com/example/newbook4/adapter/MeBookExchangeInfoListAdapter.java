package com.example.newbook4.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.newbook4.R;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.bean.BookExchangeInfoBean;
import com.example.newbook4.wdiget.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MeBookExchangeInfoListAdapter extends BaseAdapter {
	private static final String TAG = "SubjectListAdapter";
	private Context mContext;
	private List<BookExchangeInfoBean> mList;
	private String mAddress;
	private final LayoutInflater mInflater;
	private Typeface type = null;

	public MeBookExchangeInfoListAdapter(Context context,
			List<BookExchangeInfoBean> list, MyListView listView, String address) {
		Log.d(TAG, "SubjectListAdapter");
		mContext = context;
		mList = list;
		mAddress = address;
		type = Typeface.createFromAsset(context.getAssets(), "fonts/pop.ttf");
		mInflater = LayoutInflater.from(mContext);

	}

	@Override
	public int getCount() {
		Log.d(TAG, "getCount" + mList.size());
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		Log.d(TAG, "getItem" + position);
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		Log.d(TAG, "getItemId" + position);
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if (view == null) {
			view = mInflater.inflate(R.layout.listview_mebookexchangeinfo_item,
					null);
			holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(R.id.title);
			holder.msg = (TextView) view.findViewById(R.id.msg);
			holder.msg.setTypeface(type);
			holder.time = (TextView) view.findViewById(R.id.time);
			holder.icon = (ImageView) view.findViewById(R.id.icon);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		BookExchangeInfoBean bookExchangeInfoBean = mList.get(position);
		holder.title.setText(bookExchangeInfoBean.obtain_bookname);
		holder.msg.setText(getStr(bookExchangeInfoBean.obtain_msg));
		holder.time.setText(getTime(bookExchangeInfoBean.generate_time));
		String imgUrl = mAddress + bookExchangeInfoBean.obtain_picture;
		ImageLoader.getInstance().displayImage(imgUrl, holder.icon);

		return view;

	}

	/**
	 * 2015-08-16-16-19-28-1928
	 * 
	 * @param time
	 * @return
	 */
	private String getTime(String time) {
		String[] str_array = time.split("-");
		return str_array[1] + "月" + str_array[2] + "日" + " " + str_array[3]
				+ ":" + str_array[4] + ":" + str_array[5];
	}

	/**
	 * {"address1":"江西省萍乡市上栗县","address2":"金山镇张芳村","phone":"17722595175","name":
	 * "哩封信"}
	 * 
	 * @param json_Str
	 * @return
	 */
	private String getStr(String json_Str) {
		StringBuilder sb = new StringBuilder();
		try {
			JSONObject jsonObject = new JSONObject(json_Str);
			String address1 = jsonObject.getString("address1");
			String address2 = jsonObject.getString("address2");
			String phone = jsonObject.getString("phone");
			String name = jsonObject.getString("name");

			// sb.append(address1 + "(" + name + "收)\n");
			// sb.append(address2 + phone);
			sb.append(address1);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private class ViewHolder {
		ImageView icon;
		TextView title;
		TextView time;
		TextView msg;
	}

}
