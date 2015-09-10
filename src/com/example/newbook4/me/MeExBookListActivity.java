package com.example.newbook4.me;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.volley.VolleyError;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.R;
import com.example.newbook4.adapter.SubjectListAdapter;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.book.BookSimpleDetialActivity;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.example.newbook4.wdiget.MyListView;
import com.example.newbook4.wdiget.MyListView.OnRefreshListener;

public class MeExBookListActivity extends BaseActivity {

	private static final String TAG = "MeExBookListActivity";

	private Spinner spinner;

	private MyListView listview;// listView����
	private LinearLayout ll_loading;// ������ʾ���ڼ��ص�progress
	private ArrayList<BookBean> list;// Ҫ��ʾ���б�
	private ArrayList<BookBean> temp_List;
	private SubjectListAdapter adapter;// ����������

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
	private String action = "retrievalAllMyBook";

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
					Collections.sort(list, BookBean.Comparator);
				}
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meexbooklist);
		initViewComponent();

		adapter = new SubjectListAdapter(ctx, list, listview,
				NetUtil.getBookUpLoadAddress(ctx));
		// ��һ�μ�������
		initListView();
	}

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
				if (list.isEmpty()) {
					retrievalBook(true, 0, 0);
				} else {
					// ���ظ����
					// �������id ��һ��
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
					// ���ظ����
					// �������id ��һ��
					retrievalBook(false, list.get(list.size() - 1).book_Id, 1);
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
	private void retrievalBook(final boolean sign, final int bookId,
			final int type) {
		try {
			if (listview.getLoading()) {
				// ���ڼ���
				return;
			}

			handler.sendEmptyMessage(START_RETRIEVAL);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", action);
			jsonObject.put("bookId", bookId);
			jsonObject.put("userId", baseInfo.userId);
			jsonObject.put("type", type);
			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBookBusinessSpecial(ctx), requestQueue,
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
						showToast("û�и������ݣ����Ժ�ˢ��");
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

	private void initViewComponent() {
		spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String type = getResources().getStringArray(
						R.array.exchange_book_type)[position];
				if ("ȫ��".equals(type)) {
					action = "retrievalAllMyBook";
				} else if ("����ǰ".equals(type)) {
					action = "retrievalMyBeforeTrade";
				} else if ("������".equals(type)) {
					action = "retrievalMyTrading";
				} else if ("�������".equals(type)) {
					action = "retrievalMyTraded";
				} else {
					showToast("û����");
				}

				list.clear();
				adapter.notifyDataSetChanged();
				headRefreshListener.onRefresh();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});

		((TextView) findViewById(R.id.actionbar_tv)).setText("ͼ��-����");

		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);

		listview = (MyListView) findViewById(R.id.listView);

		ll_loading = (LinearLayout) findViewById(R.id.ll_main_progress);

		listview.setOnItemClickListener(onItemClickListener);

		list = new ArrayList<BookBean>();
		temp_List = new ArrayList<BookBean>();

	}

	/**
	 * ListView����¼�
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// // ��ϸ��Ϣ
			BookBean bookBean = (BookBean) parent.getAdapter()
					.getItem(position);
			clickedPostion = position - 1;
			if ("retrievalAllMyBook".equals(action)) {
				Intent resultIntent = new Intent(ctx,
						BookSimpleDetialActivity.class);
				resultIntent.putExtra("book_id", bookBean.book_Id);
				startActivity(resultIntent);
			} else if ("retrievalMyBeforeTrade".equals(action)) {
				Intent resultIntent = new Intent(ctx,
						MeExBookInfosActivity.class);
				resultIntent.putExtra("book_id", bookBean.book_Id);
				resultIntent.putExtra("book_name", bookBean.book_Name);
				startActivity(resultIntent);
			} else if ("retrievalMyTrading".equals(action)) {
				// ������
				Intent resultIntent = new Intent(ctx,
						BookExchangeOrderDetial.class);
				resultIntent.putExtra("book_id", bookBean.book_Id);
				resultIntent.putExtra("book_name", bookBean.book_Name);
				startActivityForResult(resultIntent, 2);

			} else if ("retrievalMyTraded".equals(action)) {
				// ���׺�
				Intent resultIntent = new Intent(ctx,
						BookExchangeOrderFinish.class);
				resultIntent.putExtra("book_id", bookBean.book_Id);
				resultIntent.putExtra("book_name", bookBean.book_Name);
				startActivity(resultIntent);
			}

		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 2) {
				// �������ύ�ɹ�
				int size = list.size();
				if (clickedPostion >= 0 && clickedPostion < size) {
					list.remove(clickedPostion);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
