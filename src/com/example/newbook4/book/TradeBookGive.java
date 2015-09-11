package com.example.newbook4.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.example.newbook4.bean.AddressBean;

import com.example.newbook4.me.AddAddressActivity;
import com.example.newbook4.tools.ECMessageHelper;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.wdiget.MyListView;
import com.example.newbook4.wdiget.MyListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TradeBookGive extends Activity {
	private static final String TAG = "TradeBookGive";
	private Context ctx;
	private MyListView listview;// listView对象
	private LinearLayout ll_loading;// 控制显示正在加载的progress
	private AddressAdapter adapter;// 数据适配器

	private OnRefreshListener headRefreshListener;
	private OnRefreshListener footrefreshListener;
	private ArrayList<AddressBean> list;// 要显示的列表
	private ArrayList<AddressBean> temp_List;
	private RequestQueue requestQueue = null;
	private Typeface typeace = null;

	private final int SHOW_TOAST = 0;
	private final int DIALOG_DIMSS = SHOW_TOAST + 1;
	private final int DATA_COMPONENT = DIALOG_DIMSS + 1;
	private final int REFRESH_COMPLETE = DATA_COMPONENT + 1;
	private final int START_RETRIEVAL = REFRESH_COMPLETE + 1;
	private final int END_RETRIEVAL = START_RETRIEVAL + 1;
	private final int UPDATE_DATA = END_RETRIEVAL + 1;
	private BaseInfo baseInfo;

	private int release_bookId;
	private String release_bookname;	
	private int release_userId;	

	private String style = null;

	private String str_address;

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
				listview.setLoading(false);
				break;

			case UPDATE_DATA:
				list.clear();
				list.addAll(temp_List);

				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		release_bookId = intent.getIntExtra("release_bookId", -1);
		
		style = intent.getStringExtra("style");
		release_userId = intent.getIntExtra("release_userId", -1);
	

		release_bookname = intent.getStringExtra("release_bookname");
		if (release_bookId == -1) {
			finish();
		}
		
		if (release_userId == -1) {
			finish();
		}

		if (TextUtils.isEmpty(style)) {
			finish();
		}
		if (TextUtils.isEmpty(release_bookname)) {
			finish();
		}

		setContentView(R.layout.tradebookaddress);
		ctx = this;

		initViewComponent();

		baseInfo = ((MyApplication) getApplication()).baseInfo;
		// TODO
		adapter = new AddressAdapter(ctx, list, listview);
		requestQueue = Volley.newRequestQueue(ctx);
		typeace = Typeface.createFromAsset(getAssets(), "fonts/pop.ttf");

		// 第一次加载数据
		initListView();

	}

	private void initViewComponent() {
		((TextView) findViewById(R.id.actionbar_tv)).setText("选择地址");

		ll_loading = (LinearLayout) findViewById(R.id.ll_main_progress);
		listview = (MyListView) findViewById(R.id.listView);
		listview.setOnItemClickListener(onItemClickListener);
		list = new ArrayList<AddressBean>();
		temp_List = new ArrayList<AddressBean>();

		((Button) this.findViewById(R.id.actionbar_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}

				});

		((Button) this.findViewById(R.id.actionbar_add))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ctx, AddAddressActivity.class);
						startActivityForResult(intent, 0);
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
			AddressBean addressBean = (AddressBean) parent.getAdapter()
					.getItem(position);
			str_address = addressBean.getSimpleStr();
			showDialog(1);
		}

	};

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
				load_Address(true);
			}
		};
		listview.setonRefreshListener(headRefreshListener);

		footrefreshListener = new OnRefreshListener() {
			@Override
			public void onRefresh() {
				load_Address(false);
			}

		};
		listview.setonFootRefreshListener(footrefreshListener);
		listview.setAdapter(adapter);

	}
/**
 * 加载用户地址:Baseservlet
 * @param sign
 */
	protected void load_Address(final boolean sign) {
		if (listview.getLoading()) {
			// 正在加载
			return;
		}

		handler.sendEmptyMessage(START_RETRIEVAL);

		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "retrievalAddress-user");
		map.put("userId", baseInfo.userId + "");

		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
				Method.POST, NetUtil.getBaseAddress(ctx), jsonObject,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(final JSONObject response) {
						retrievalCompleted(sign, true, response.toString());
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError error) {
						retrievalCompleted(sign, false,
								VolleyErrorHelper.getMessage(error, ctx));
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
			String response) {

		if (isFinish) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				int error_Code = jsonObject.getInt("errorcode");
				if (error_Code == 0) {
					// 返回UserId
					temp_List.clear();
					JSONArray bookArray = jsonObject
							.getJSONArray("addressArray");
					Log.d(TAG, "bookArray=" + bookArray);
					int len = bookArray.length();
					for (int i = 0; i < len; i++) {

						JSONObject addressObject = bookArray.getJSONObject(i);
						AddressBean addressBean = new AddressBean();

						addressBean.addressId = addressObject
								.getInt("address_id");
						addressBean.userId = addressObject.getInt("user_id");
						addressBean.address1 = addressObject
								.getString("address1");
						addressBean.address2 = addressObject
								.getString("address2");
						addressBean.contact = addressObject.getString("name");
						addressBean.phone = addressObject.getString("phone");
						int flag = addressObject.getInt("flag");
						if (flag == 1) {
							addressBean.flag = true;
						} else {
							addressBean.flag = false;
						}
						temp_List.add(addressBean);
					}
					if (!temp_List.isEmpty()) {
						Message msg = handler.obtainMessage();
						msg.what = UPDATE_DATA;
						handler.sendMessage(msg);
					} else {
						showToast("没有地址信息，请添加");
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

	class AddressAdapter extends BaseAdapter {
		private static final String TAG = "AddressAdapter";
		private Context mContext;
		private List<AddressBean> mList;
		private final LayoutInflater mInflater;
		private Typeface type = null;

		public AddressAdapter(Context context, List<AddressBean> list,
				MyListView listView) {
			Log.d(TAG, "AddressAdapter");
			mContext = context;
			mList = list;
			type = Typeface.createFromAsset(context.getAssets(),
					"fonts/pop.ttf");
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
				view = mInflater.inflate(R.layout.address_listitem, null);
				holder = new ViewHolder();
				holder.address1 = (TextView) view
						.findViewById(R.id.tv_address1);
				holder.name = (TextView) view.findViewById(R.id.tv_name);
				holder.address2 = (TextView) view
						.findViewById(R.id.tv_address2);
				holder.phone = (TextView) view.findViewById(R.id.tv_phone);

				holder.address1.setTypeface(typeace);
				holder.name.setTypeface(typeace);
				holder.address2.setTypeface(typeace);
				holder.phone.setTypeface(typeace);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			AddressBean address = mList.get(position);
			holder.address1.setText(address.address1);
			holder.name.setText("(" + address.contact + "收)");
			holder.address2.setText(address.address2);
			holder.phone.setText(address.phone);
			return view;
		}

		class ViewHolder {
			TextView address1;
			TextView name;
			TextView address2;
			TextView phone;
		}

	}

	private void showToast(String srt) {
		Toast.makeText(this, srt, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
			AlertDialog.Builder builder = new Builder(ctx);
			builder.setMessage("确定提交订单？");
			builder.setTitle("确认");

			builder.setNegativeButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					submitOrder();
				}

			});
			builder.setPositiveButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					showToast("您取消了订单");
				}

			});
			builder.setCancelable(false);

			builder.show();
		}
		return super.onCreateDialog(id);
	}

	/**
	 * 几大要素 更新书的状态 然后在订单表中插入一条记录 提交订单
	 * 对应 BookGiveServlet insert()
	 */
	protected void submitOrder() {
		showToast("提交操作");

		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "book_give_insert");
		map.put("own_userId", baseInfo.userId + "");
		map.put("release_bookId", release_bookId + "");
		map.put("release_userId", release_userId + "");		
		map.put("own_address", str_address);		
		map.put("release_bookname", release_bookname);

		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
				Method.POST, NetUtil.getBookExchangeAddress(ctx), jsonObject,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(final JSONObject response) {
						subCompleted(response.toString());
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError error) {						
						showToast(VolleyErrorHelper.getMessage(error, ctx));
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
	 * 提交结果解析
	 * 
	 * @param string
	 */
	protected void subCompleted(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			int error_Code = jsonObject.getInt("errorcode");
			if (error_Code == 0) {
				// 返回UserId
				showToast("提交订单成功！");
				// 发送消息
				String imAccount = jsonObject.getString("imAccount");
				JSONObject content = new JSONObject();
				content.put("action", "give_info_reserved");
				content.put("book_name", release_bookname);
				content.put("book_id", release_bookId);

				ECMessageHelper.sendMessage(baseInfo.im_account, imAccount,
						content.toString());
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();// 结束之后会将结果传回From
			} else {
				showToast("error_Code：" + error_Code);
			}

		} catch (JSONException e) {
			showToast("服务器返回值出现错误");
			e.printStackTrace();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 0:
				if (headRefreshListener != null) {
					headRefreshListener.onRefresh();
				}
				break;

			}
		}

	}

}
