package com.example.newbook4.me;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.newbook4.BaseInfo;
import com.example.newbook4.BookFragment;
import com.example.newbook4.MyApplication;
import com.example.newbook4.R;
import com.example.newbook4.adapter.SubjectListAdapter;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.book.BookDetailActivity;
import com.example.newbook4.book.ReleaseBookActivity;
import com.example.newbook4.book.ReleaseBookSale;
import com.example.newbook4.tools.Configuration;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.wdiget.MyListView;
import com.example.newbook4.wdiget.MyListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 收藏的书籍 交易的书籍 自己的书籍
 * 
 * @author Administrator
 *
 */
public class MeBookActivity extends Activity {

	private static final String TAG = MeBookActivity.class.getName();

	private Context ctx;

	private RequestQueue requestQueue = null;

	private MyListView listview;// listView对象
	private LinearLayout ll_loading;// 控制显示正在加载的progress
	private ArrayList<BookBean> list;// 要显示的列表
	private ArrayList<BookBean> temp_List;
	private SubjectListAdapter adapter;// 数据适配器

	private OnRefreshListener headRefreshListener;
	private OnRefreshListener footrefreshListener;

	private final int SHOW_TOAST = 0;
	private final int DIALOG_DIMSS = SHOW_TOAST + 1;
	private final int DATA_COMPONENT = DIALOG_DIMSS + 1;
	private final int REFRESH_COMPLETE = DATA_COMPONENT + 1;
	private final int START_RETRIEVAL = REFRESH_COMPLETE + 1;
	private final int END_RETRIEVAL = START_RETRIEVAL + 1;
	private final int UPDATE_DATA = END_RETRIEVAL + 1;

	private int clickedPostion = -1;
	private String action;

	private String name;

	private ArrayList<String> actionCommands;
	private BaseInfo baseInfo;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_TOAST:
				Toast.makeText(ctx, (String) msg.obj, Toast.LENGTH_SHORT)
						.show();
				break;
			case START_RETRIEVAL:
				ll_loading.setVisibility(View.VISIBLE);
				listview.setLoading(true);
				break;
			case END_RETRIEVAL:
				listview.onRefreshComplete();
				// Log.e(TAG, "END_RETRIEVAL");
				ll_loading.setVisibility(View.GONE);
				Log.d(TAG, "更新adapter");
				adapter.notifyDataSetChanged();
				Log.d(TAG, "一共有" + list.size() + "本书");
				listview.setLoading(false);
				break;

			case UPDATE_DATA:
				// 显示 加载更多
				listview.dispalyFooterView();
				int type = (Integer) msg.obj;
				if (type == 0 || type == 1) {
					list.addAll(temp_List);
					Log.d(TAG, "list addLast");
				} else if (type == 2) {
					Log.d(TAG, "list addFirst");
					list.addAll(temp_List);
					// 排序
					Collections.sort(list, BookBean.Comparator);
				}
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me_book);
		ctx = this;
		Intent intent = getIntent();
		action = intent.getStringExtra("action");
		name = intent.getStringExtra("name");
		if (TextUtils.isEmpty(action)) {
			showToast("参数错误");
			finish();
			return;
		}
		actionCommands = new ArrayList<String>();
		actionCommands.add("retrievalAllMyBook");
		actionCommands.add("retrievalMyBeforeTrade");
		actionCommands.add("retrievalMyTrading");
		actionCommands.add("retrievalMyTraded");
		if (!actionCommands.contains(action)) {
			showToast("参数错误");
			finish();
			return;
		}

		initView();
		baseInfo = ((MyApplication) getApplication()).baseInfo;
		adapter = new SubjectListAdapter(ctx, list, listview,
				NetUtil.getBookUpLoadAddress(ctx));
		requestQueue = Volley.newRequestQueue(ctx);
		// 第一次加载数据
		initListView();
	}

	private void initView() {

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});

		((TextView) findViewById(R.id.actionbar_tv)).setText(name);

		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);

		listview = (MyListView) findViewById(R.id.listView);

		ll_loading = (LinearLayout) findViewById(R.id.ll_main_progress);

		listview.setOnItemClickListener(onItemClickListener);

		list = new ArrayList<BookBean>();
		temp_List = new ArrayList<BookBean>();

	}

	/**
	 * ListView点击事件
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			clickedPostion = position;
			// 详细信息
			BookBean bookBean = (BookBean) parent.getAdapter()
					.getItem(position);
			// showToast("点击了" + bookBean.book_Id);
			Intent intent = new Intent();
			intent.putExtra("BookBean", bookBean);
			if ("retrievalAllMyBook".equals(action)) {
				intent.setClass(ctx, MeBookDetail.class);
				startActivity(intent);
			} else if ("retrievalMyBeforeTrade".equals(action)) {
				intent.setClass(ctx, MeExBookInfosActivity.class);
				intent.putExtra("book_id", bookBean.book_Id);
				intent.putExtra("book_name", bookBean.book_Name);
				startActivityForResult(intent, 1);

			} else if ("retrievalMyTrading".equals(action)) {
				// 确认收货之前
				showToast("显示订单记录");

			} else if ("retrievalMyTraded".equals(action)) {
				showToast("显示完成的订单");
			}

		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "clickedPostion=" + clickedPostion);
		clickedPostion=clickedPostion-1;
		switch (requestCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				int size = list.size();
				if (clickedPostion >=0 && clickedPostion < size) {
					list.remove(clickedPostion);
					adapter.notifyDataSetChanged();
				}
			}
			break;

		}
	}

	/**
	 * 初始化listView 设置两个监听器 设置Adapter
	 */
	public void initListView() {
		/**
		 * 下拉
		 */
		headRefreshListener = new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// downLoad_More(true);
				if (list.isEmpty()) {
					retrievalBook(true, 0, 0);
				} else {
					// 加载更多的
					// 获得最大的id 第一个
					retrievalBook(true, list.get(0).book_Id, 2);
				}
			}
		};
		listview.setonRefreshListener(headRefreshListener);

		footrefreshListener = new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (list.isEmpty()) {
					retrievalBook(false, 0, 0);
				} else {
					// 加载更多的
					// 获得最大的id 第一个
					retrievalBook(false, list.get(list.size() - 1).book_Id, 1);
				}
			}

		};
		listview.setonFootRefreshListener(footrefreshListener);
		listview.setAdapter(adapter);

	}

	/**
	 * 查找书籍
	 * 
	 * @param sign
	 * @param bookId
	 * @param type
	 */
	private void retrievalBook(final boolean sign, final int bookId,
			final int type) {
		if (listview.getLoading()) {
			// 正在加载
			return;
		}

		
		handler.sendEmptyMessage(START_RETRIEVAL);
		
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", action);
		map.put("bookId", bookId + "");
		map.put("userId", baseInfo.userId + "");
		map.put("type", type + "");

		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
				Method.POST, NetUtil.getBookBusinessSpecial(ctx), jsonObject,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(final JSONObject response) {
						retrievalCompleted(sign, true, response.toString(),
								type);
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError error) {

						// message.what = LOGIN_FAILED;
						// message.obj = VolleyErrorStr.getErrorStr(error);
						retrievalCompleted(sign, false,
								VolleyErrorHelper.getMessage(error, ctx), type);
					}

				}

		) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/json;charset=UTF-8");
				return headers;
			}

		};
		requestQueue.add(jsonRequest);
		Log.d(TAG, "jsonRequest=" + jsonRequest);
	}

	/**
	 * 加载完成
	 * 
	 * @param sign
	 * @param response
	 */
	protected void retrievalCompleted(boolean sign, boolean isFinish,
			String response, int type) {

		if (isFinish) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				int error_Code = jsonObject.getInt("errorcode");
				if (error_Code == 0) {
					// 返回UserId
					JSONArray bookArray = jsonObject.getJSONArray("bookArray");
					temp_List.clear();
					Log.d(TAG, "bookArray=" + bookArray);
					int len = bookArray.length();
					for (int i = 0; i < len; i++) {

						JSONObject bookObject = bookArray.getJSONObject(i);
						BookBean bookBean = new BookBean();
						bookBean.book_Id = bookObject.getInt("book_id");
						bookBean.user_Id = bookObject.getInt("user_id");
						bookBean.book_Name = bookObject.getString("book_name");
						bookBean.abstract_content = bookObject
								.getString("abstract");
						bookBean.time_Release = bookObject
								.getString("time_release");
						bookBean.transcation = bookObject
								.getString("transaction");
						bookBean.new_Old = bookObject.getString("new_old");
						bookBean.picture_Path = bookObject.getString("picture");
						bookBean.priority = bookObject.getInt("priority");
						bookBean.rating = bookObject.getInt("rating");
						String generation_time = bookObject
								.getString("generation_time");
						String[] temp = generation_time.split("-");
						bookBean.generation_time = temp[3] + ":" + temp[4]
								+ ":" + temp[5];
						bookBean.author_name = bookObject
								.getString("author_name");
						bookBean.interest = bookObject.getString("interest");
						temp_List.add(bookBean);
					}
					if (!temp_List.isEmpty()) {
						Message msg = handler.obtainMessage();
						msg.what = UPDATE_DATA;
						msg.obj = type;
						handler.sendMessage(msg);

					} else {
						showToast("没有更多内容，请稍候刷新");
					}
				} else {
					showToast("error_Code：" + error_Code);
				}

			} catch (JSONException e) {
				showToast("服务器返回值出现错误");
				e.printStackTrace();
			}
		} else {
			showToast(response);
		}
		handler.sendEmptyMessage(END_RETRIEVAL);
	}

	private void showToast(String str) {
		Message msg = handler.obtainMessage();
		msg.what = SHOW_TOAST;
		msg.obj = str;
		handler.sendMessage(msg);
	}

}
