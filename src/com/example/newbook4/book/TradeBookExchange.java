package com.example.newbook4.book;

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
import com.example.newbook4.MyApplication;
import com.example.newbook4.R;
import com.example.newbook4.adapter.SubjectListAdapter;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.tools.Configuration;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.wdiget.MyListView;
import com.example.newbook4.wdiget.MyListView.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TradeBookExchange extends Activity {
	private static final String TAG = "TradeBookExchange";
	private Context ctx;
	private BookBean from_bookBean;
	private ImageView icon;
	private TextView title;
	private TextView time;
	private TextView msg;
	private RatingBar ratingBar;
	private Typeface typeace = null;
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
				Log.e(TAG, "END_RETRIEVAL");
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
		setContentView(R.layout.tradebookexchange);

		Intent intent = getIntent();
		Object object = intent.getParcelableExtra("BookBean");
		if (object instanceof BookBean) {
			from_bookBean = (BookBean) object;
			if (from_bookBean == null) {
				finish();
			}
		} else {
			finish();
		}

		typeace = Typeface.createFromAsset(getAssets(), "fonts/pop.ttf");
		ctx = this;
		initViewComponent();

		baseInfo = ((MyApplication) getApplication()).baseInfo;
		adapter = new SubjectListAdapter(ctx, list, listview,
				NetUtil.getBookUpLoadAddress(ctx));
		requestQueue = Volley.newRequestQueue(ctx);
		// 第一次加载数据
		initListView();
	}

	private void initViewComponent() {
		((TextView) findViewById(R.id.actionbar_tv)).setText("1/2：选择书籍");
		title = (TextView) findViewById(R.id.title);
		msg = (TextView) findViewById(R.id.msg);
		msg.setTypeface(typeace);
		time = (TextView) findViewById(R.id.time);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		icon = (ImageView) findViewById(R.id.icon);

		title.setText(from_bookBean.book_Name);
		msg.setText(from_bookBean.interest + ":" + from_bookBean.transcation);
		time.setText(from_bookBean.generation_time);
		ratingBar.setRating(from_bookBean.rating);
		String imgUrl = NetUtil.getBookUpLoadAddress(ctx)
				+ from_bookBean.picture_Path;
		ImageLoader.getInstance().displayImage(imgUrl, icon);

		listview = (MyListView) findViewById(R.id.listView);

		ll_loading = (LinearLayout) findViewById(R.id.ll_main_progress);

		listview.setOnItemClickListener(onItemClickListener);

		list = new ArrayList<BookBean>();
		temp_List = new ArrayList<BookBean>();
		((Button) this.findViewById(R.id.actionbar_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}

				});

		((Button) this.findViewById(R.id.actionbar_add))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						uploadImage2(TradeBookExchange.this);
					}

				});
	}

	/**
	 * ListView点击事件
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 详细信息
			BookBean bookBean = (BookBean) parent.getAdapter()
					.getItem(position);
			showToast("点击了" + bookBean.book_Id);
			Intent intent = new Intent();
			intent.setClass(ctx, TradeBookAddress.class);
			intent.putExtra("other_bookId", from_bookBean.book_Id);
			intent.putExtra("other_userId", from_bookBean.user_Id);
			intent.putExtra("own_bookname", bookBean.book_Name);
			intent.putExtra("own_bookId", bookBean.book_Id);
			intent.putExtra("own_picture", bookBean.picture_Path);
			intent.putExtra("other_bookname", from_bookBean.book_Name);
			intent.putExtra("style", "exchange");

			// TODO
			startActivityForResult(intent, 0);

		}

	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 0:
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();// 结束之后会将结果传回From后会将结果传回From
				break;

			case 1:

			case 2:
				// 刷新
				if (headRefreshListener != null) {
					headRefreshListener.onRefresh();
				}
				break;
			}
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
		map.put("action", "retrievalBook-user");
		map.put("bookId", bookId + "");
		map.put("type", type + "");
		map.put("userId", baseInfo.userId + "");

		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
				Method.POST, NetUtil.getBookBusincesAddress(ctx), jsonObject,
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

	private void showToast(String srt) {
		Toast.makeText(this, srt, Toast.LENGTH_SHORT).show();
	}

	private ActionBarAddPopupWindow actionBarPopupWindow;

	public void uploadImage2(final Activity context) {
		actionBarPopupWindow = new ActionBarAddPopupWindow(context,
				popWindowOnClick);
		actionBarPopupWindow.showAtLocation(findViewById(R.id.actionbar_add),
				Gravity.TOP | Gravity.RIGHT, 10, 230);
	}

	public class ActionBarAddPopupWindow extends PopupWindow {

		private View mMenuView;

		private LinearLayout exchange_ll, give_ll, sale_ll;

		private TextView exchange_tv, give_tv, sale_tv;

		private PopllOnClickListener popllOnClickListener;

		public ActionBarAddPopupWindow(final Activity context,
				OnClickListener itemsOnClick) {
			super(context);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mMenuView = inflater.inflate(R.layout.book_bar_add, null);

			mMenuView.setBackgroundColor(getResources().getColor(
					Configuration.BOOK_COLOR));
			exchange_ll = (LinearLayout) mMenuView
					.findViewById(R.id.poo_exchange);
			give_ll = (LinearLayout) mMenuView.findViewById(R.id.poo_give);
			sale_ll = (LinearLayout) mMenuView.findViewById(R.id.poo_sale);

			exchange_tv = (TextView) mMenuView
					.findViewById(R.id.popexchange_tv);
			give_tv = (TextView) mMenuView.findViewById(R.id.popgive_tv);
			sale_tv = (TextView) mMenuView.findViewById(R.id.popsale_tv);

			popllOnClickListener = new PopllOnClickListener();
			exchange_ll.setOnTouchListener(popllOnClickListener);
			give_ll.setOnTouchListener(popllOnClickListener);
			sale_ll.setOnTouchListener(popllOnClickListener);

			int h = context.getWindowManager().getDefaultDisplay().getHeight();
			int w = context.getWindowManager().getDefaultDisplay().getWidth();

			this.setContentView(mMenuView);
			this.setWidth(w / 2 + 50);
			this.setHeight(LayoutParams.WRAP_CONTENT);
			this.setFocusable(true);
			this.setAnimationStyle(R.style.popwindow);
			ColorDrawable dw = new ColorDrawable(0000000000);
			this.setBackgroundDrawable(dw);
			mMenuView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					int height = mMenuView.findViewById(R.id.pop_layout)
							.getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							dismiss();
						}
					}
					return true;
				}
			});

		}

		class PopllOnClickListener implements OnTouchListener {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int cId = -1;
				int action = event.getAction();
				if (action == MotionEvent.ACTION_UP) {
					cId = Configuration.UP_COLOR;
				} else if (action == MotionEvent.ACTION_DOWN) {
					cId = Configuration.PRESSED_COLOR;
				}
				if (cId == -1) {
					return false;
				}
				Intent intent = new Intent();
				switch (v.getId()) {
				case R.id.poo_exchange:
					exchange_tv.setTextColor(getResources().getColor(cId));
					if (cId == Configuration.UP_COLOR) {
						intent.setClass(ctx, ReleaseBookActivity.class);
						intent.putExtra("type", 0);
						startActivityForResult(intent, 1);
						dismiss();
					}

					// //exchange_ll.setBackgroundColor(getResources().getColor(
					// colorId));
					break;
				case R.id.poo_give:
					give_tv.setTextColor(getResources().getColor(cId));
					if (cId == Configuration.UP_COLOR) {
						intent.setClass(ctx, ReleaseBookActivity.class);
						intent.putExtra("type", 1);
						startActivityForResult(intent, 2);
						dismiss();
					}

					// give_ll.setBackgroundColor(getResources().getColor(colorId));
					break;
				case R.id.poo_sale:
					sale_tv.setTextColor(getResources().getColor(cId));
					if (cId == Configuration.UP_COLOR) {
						intent.setClass(ctx, ReleaseBookSale.class);
						startActivityForResult(intent, 3);
						dismiss();
					}

					// sale_ll.setBackgroundColor(getResources().getColor(colorId));
					break;
				}
				return true;
			}
		}
	}

	private OnClickListener popWindowOnClick = new OnClickListener() {

		public void onClick(View v) {
			actionBarPopupWindow.dismiss();
		}
	};

}
