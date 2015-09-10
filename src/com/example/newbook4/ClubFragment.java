package com.example.newbook4;

import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.newbook4.adapter.ClubListAdapter;
import com.example.newbook4.adapter.SubjectListAdapter;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.bean.ClubBean;
import com.example.newbook4.book.BookDetailActivity;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.example.newbook4.wdiget.MyListView;
import com.example.newbook4.wdiget.MyListView.OnRefreshListener;
import android.support.v4.app.Fragment;
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

public class ClubFragment extends Fragment {
	private static final String TAG = ClubFragment.class.getName();
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
	private ArrayList<ClubBean> list;
	private ArrayList<ClubBean> temp_List;
	/**
	 * 数据适配器
	 */
	private ClubListAdapter adapter;

	private OnRefreshListener headRefreshListener;
	private OnRefreshListener footrefreshListener;

	private int clickedPost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_club, null);

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
		list = new ArrayList<ClubBean>();
		temp_List = new ArrayList<ClubBean>();
		adapter = new ClubListAdapter(ctx, list, listview,
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

			showToast("点击了");
			// BookBean bookBean = (BookBean) parent.getAdapter()
			// .getItem(position);
			// clickedPost = position;
			// Intent intent = new Intent();
			// intent.setClass(ctx, BookDetailActivity.class);
			// intent.putExtra("bookId", bookBean.book_Id);
			// intent.putExtra("bookName", bookBean.book_Name);
			// startActivityForResult(intent, 1);
		}

	};

	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// if (resultCode == Activity.RESULT_CANCELED) {
	// switch (requestCode) {
	// case 1:
	// clickedPost--;
	// int size = list.size();
	// if (clickedPost >= 0 && clickedPost < size) {
	// list.remove(clickedPost);
	// adapter.notifyDataSetChanged();
	// }
	// break;
	//
	// }
	// }
	//
	// }

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
					retrievalClub(true, 0, 0);
				} else {
					// 加载更多的
					// 获得最大的id 第一个
					retrievalClub(true, list.get(0).club_id, 2);
				}
			}
		};
		listview.setonRefreshListener(headRefreshListener);

		footrefreshListener = new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (list.isEmpty()) {
					retrievalClub(false, 0, 0);
				} else {
					// 加载更多的
					// 获得最大的id 第一个
					retrievalClub(false, list.get(list.size() - 1).club_id, 1);
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
					Collections.sort(list, ClubBean.Comparator);
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
	private void retrievalClub(final boolean sign, final int clubId,
			final int type) {
		try {
			if (listview.getLoading()) {
				// 正在加载
				return;
			}
			// 开始显示
			startRetrieval();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "retrievalClub");
			jsonObject.put("clubId", clubId);
			jsonObject.put("type", type);

			Log.d(TAG, "jsonObject=" + jsonObject);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getClubBusinessAddress(ctx), requestQueue,
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
					JSONArray clubArray = jsonObject.getJSONArray("clubArray");
					temp_List.clear();
					Log.d(TAG, "bookArray=" + clubArray);
					int len = clubArray.length();
					for (int i = 0; i < len; i++) {
						
						JSONObject clubObject = clubArray.getJSONObject(i);
						ClubBean clubBean = new ClubBean();
						clubBean.club_id = clubObject.getInt("club_id");
						clubBean.user_id = clubObject.getInt("user_id");

						clubBean.topic = clubObject.getString("topic");
						clubBean.recommend_book = clubObject
								.getString("recommend_book");
						clubBean.time = clubObject.getString("time");
						clubBean.address = clubObject.getString("address");
						clubBean.enroll_num = clubObject.getInt("enroll_num");
						clubBean.concern_num = clubObject.getInt("concern_num");

						clubBean.accusation_num = clubObject
								.getInt("accusation_num");
						clubBean.generate_time = clubObject
								.getString("generate_time");

						temp_List.add(clubBean);
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
