package com.example.newbook4.me;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.MyApplication;
import com.example.newbook4.R;
import com.example.newbook4.adapter.MeBookGiveInfoListAdapter;
import com.example.newbook4.bean.BookGiveInfoBean;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.example.newbook4.wdiget.MyListView;
import com.example.newbook4.wdiget.MyListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MeGiveBookInfosActivity extends BaseActivity {
	private static final String TAG = "MeBookInfoActivity";

	private MyListView listview;// listView����
	private LinearLayout ll_loading;// ������ʾ���ڼ��ص�progress
	private ArrayList<BookGiveInfoBean> list;// Ҫ��ʾ���б�
	private ArrayList<BookGiveInfoBean> temp_List;
	private MeBookGiveInfoListAdapter adapter;// ����������

	private OnRefreshListener headRefreshListener;
	private OnRefreshListener footrefreshListener;

	private final int SHOW_TOAST = 0;
	private final int DIALOG_DIMSS = SHOW_TOAST + 1;
	private final int DATA_COMPONENT = DIALOG_DIMSS + 1;
	private final int REFRESH_COMPLETE = DATA_COMPONENT + 1;
	private final int START_RETRIEVAL = REFRESH_COMPLETE + 1;
	private final int END_RETRIEVAL = START_RETRIEVAL + 1;
	private final int UPDATE_DATA = END_RETRIEVAL + 1;

	private int book_id;
	private String book_name;

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
				Log.d(TAG, "����adapter");
				adapter.notifyDataSetChanged();
				Log.d(TAG, "һ����" + list.size() + "����");
				listview.setLoading(false);
				break;

			case UPDATE_DATA:
				// ��ʾ ���ظ���
				listview.dispalyFooterView();
				int type = (Integer) msg.obj;
				if (type == 0 || type == 1) {
					list.addAll(temp_List);
					Log.d(TAG, "list addLast");
				} else if (type == 2) {
					Log.d(TAG, "list addFirst");
					list.addAll(temp_List);
					// ����
					Collections.sort(list, BookGiveInfoBean.Comparator);
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
		book_id = intent.getIntExtra("book_id", -1);
		if (book_id == -1) {
			showToast("��������");
			finish();
		}

		book_name = intent.getStringExtra("book_name");
		if (TextUtils.isEmpty(book_name)) {
			showToast("��������");
			finish();
		}
		initView();
		baseInfo = ((MyApplication) getApplication()).baseInfo;
		adapter = new MeBookGiveInfoListAdapter(ctx, list, listview,
				NetUtil.getBookUpLoadAddress(ctx));
		requestQueue = Volley.newRequestQueue(ctx);
		// ��һ�μ�������
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
		((TextView) findViewById(R.id.actionbar_tv)).setText(book_name);
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);
		listview = (MyListView) findViewById(R.id.listView);
		ll_loading = (LinearLayout) findViewById(R.id.ll_main_progress);
		listview.setOnItemClickListener(onItemClickListener);
		list = new ArrayList<BookGiveInfoBean>();
		temp_List = new ArrayList<BookGiveInfoBean>();

	}

	/**
	 * ListView����¼�
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			//showToast("�����" + position);
			// ��ϸ��Ϣ
			BookGiveInfoBean bookGiveInfoBean = (BookGiveInfoBean) parent
					.getAdapter().getItem(position);
			Intent intent = new Intent();
			intent.putExtra("BookGiveInfoBean", bookGiveInfoBean);
			intent.setClass(ctx, MeBookInfoDetialActivity.class);
			startActivityForResult(intent, 1);

		}

	};

	

	/**
	 * ��ʼ��listView �������������� ����Adapter
	 */
	public void initListView() {
		/**
		 * ����
		 */
		headRefreshListener = new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// downLoad_More(true);
				if (list.isEmpty()) {
					retrievalBook(true, 0, 0);
				} else {
					// ���ظ����
					// �������id ��һ��
					retrievalBook(true, list.get(0).info_id, 2);
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
					// ���ظ����
					// �������id ��һ��
					retrievalBook(false, list.get(list.size() - 1).info_id, 1);
				}
			}

		};
		listview.setonFootRefreshListener(footrefreshListener);
		listview.setAdapter(adapter);

	}

	/**
	 * �����鼮
	 * 
	 * @param sign
	 * @param bookId
	 * @param type
	 */
	private void retrievalBook(final boolean sign, final int info_id,
			final int type) {
		try {
			if (listview.getLoading()) {
				// ���ڼ���
				return;
			}
			handler.sendEmptyMessage(START_RETRIEVAL);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "book_give_retrieval");
			jsonObject.put("info_id", info_id);
			jsonObject.put("book_id", book_id);
			jsonObject.put("type", type);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBookGiveAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							retrievalCompleted(sign, true, response.toString(),
									type);
						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							retrievalCompleted(sign, false,
									VolleyErrorHelper.getMessage(error, ctx),
									type);
						}

					});

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * �������
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
					// ����UserId
					JSONArray array = jsonObject
							.getJSONArray("exchangBookInfoArray");
					temp_List.clear();
					Log.d(TAG, "array=" + array);
					int len = array.length();
					for (int i = 0; i < len; i++) {

						JSONObject arrayObject = array.getJSONObject(i);
						BookGiveInfoBean bookGiveInfoBean = new BookGiveInfoBean();
						bookGiveInfoBean.info_id = arrayObject
								.getInt("info_id");
						bookGiveInfoBean.release_user = arrayObject
								.getInt("release_user");
						bookGiveInfoBean.obtain_user = arrayObject
								.getInt("obtain_user");
						bookGiveInfoBean.release_book = arrayObject
								.getInt("release_book");
					/*	bookGiveInfoBean.obtain_book = arrayObject
								.getInt("obtain_book");*/
						bookGiveInfoBean.obtain_msg = arrayObject
								.getString("obtain_msg");
						bookGiveInfoBean.generate_time = arrayObject
								.getString("generation_time");
					/*	bookGiveInfoBean.obtain_bookname = arrayObject
								.getString("obtain_bookname");
						*/
						bookGiveInfoBean.release_picture = arrayObject
								.getString("release_picture");
						
						bookGiveInfoBean.release_bookname = arrayObject
								.getString("release_bookname");
						temp_List.add(bookGiveInfoBean);
					}
					if (!temp_List.isEmpty()) {
						Message msg = handler.obtainMessage();
						msg.what = UPDATE_DATA;
						msg.obj = type;
						handler.sendMessage(msg);
					} else {
						if (list.isEmpty()) {
							showToast("��ʱû����Ϣ");
						} else {
							showToast("û�и�����Ϣ");
						}
					}
				} else {
					showToast("error_Code��" + error_Code);
				}

			} catch (JSONException e) {
				showToast("����������ֵ���ִ���");
				e.printStackTrace();
			}
		} else {
			showToast(response);
		}
		handler.sendEmptyMessage(END_RETRIEVAL);
	}

}
