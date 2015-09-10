package com.example.newbook4;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.newbook4.adapter.SubjectListAdapter;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.book.BookDetailActivity;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.example.newbook4.wdiget.MyListView;
import com.example.newbook4.wdiget.MyListView.OnRefreshListener;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("InflateParams")
/**
 * 图书主页面
 * @author li.fx
 *2015年8月14日09:35:33
 */
public class BookFragment extends Fragment {
	private static final String TAG = BookFragment.class.getName();
	private View view;
	private Context ctx;
	private RequestQueue requestQueue = null;
	/**
	 * listView对象
	 */
	private MyListView listview;
	/**
	 * 控制显示正在加载的progress
	 */
	private LinearLayout ll_loading;
	/**
	 * 要显示的列表
	 */
	private ArrayList<BookBean> list;
	private ArrayList<BookBean> temp_List;
	/**
	 * 数据适配器
	 */
	private SubjectListAdapter adapter;

	private OnRefreshListener headRefreshListener;
	private OnRefreshListener footrefreshListener;

	private int clickedPost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_book, null);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ctx = getActivity();
		initViewComponent();
		initData();
		requestQueue = Volley.newRequestQueue(ctx);
		// 第一次加载数据
		initListView();
	}

	private void initData() {
		list = new ArrayList<BookBean>();
		temp_List = new ArrayList<BookBean>();
		adapter = new SubjectListAdapter(ctx, list, listview,
				NetUtil.getBookUpLoadAddress(ctx));
	}

	private void initViewComponent() {
		listview = (MyListView) view.findViewById(R.id.listView);
		ll_loading = (LinearLayout) view.findViewById(R.id.ll_main_progress);
		listview.setOnItemClickListener(onItemClickListener);
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
			clickedPost = position;
			Intent intent = new Intent();
			intent.setClass(ctx, BookDetailActivity.class);
			intent.putExtra("bookId", bookBean.book_Id);
			intent.putExtra("bookName", bookBean.book_Name);
			startActivityForResult(intent, 1);
		}

	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {
			switch (requestCode) {
			case 1:
				clickedPost--;
				int size = list.size();
				if (clickedPost >= 0 && clickedPost < size) {
					list.remove(clickedPost);
					adapter.notifyDataSetChanged();
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

	private void startRetrieval() {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ll_loading.setVisibility(View.VISIBLE);
				listview.setLoading(true);
			}

		});
	}

	private void finishRetrieval() {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				listview.onRefreshComplete();
				ll_loading.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				listview.setLoading(false);
			}

		});

	}

	private void updateData(final int type) {

		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				// 显示 加载更多
				listview.dispalyFooterView();
				if (type == 0 || type == 1) {
					list.addAll(temp_List);
					// Log.d(TAG, "list addLast");
				} else if (type == 2) {
					// Log.d(TAG, "list addFirst");
					list.addAll(temp_List);
					// 排序
					Collections.sort(list, BookBean.Comparator);
				}
			}

		});

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
		try {
			if (listview.getLoading()) {
				// 正在加载
				return;
			}
			// 开始显示
			startRetrieval();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "retrievalBook");
			jsonObject.put("bookId", bookId);
			jsonObject.put("type", type);

			Log.d(TAG, "jsonObject=" + jsonObject);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBookBusincesAddress(ctx), requestQueue,
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
						updateData(type);

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
		// 结束显示
		finishRetrieval();
	}

	protected void showToast(final String strToast) {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(ctx, strToast, Toast.LENGTH_SHORT).show();
			}

		});
	}

}
