package com.example.newbook4;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import com.example.newbook4.adapter.InfoListAdapter;

import com.example.newbook4.bean.InfoBean;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.example.newbook4.wdiget.MyListView;
import com.example.newbook4.wdiget.MyListView.OnRefreshListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class InfobuyFragment extends Fragment{
	private static final String TAG = InfobuyFragment.class.getName();

	private View view;
	private Context ctx;
	private RequestQueue requestQueue = null;
	/**
	 * listView����
	 */
	private MyListView listview;
	/**
	 * ������ʾ���ڼ��ص�progress
	 */
	private LinearLayout ll_loading;
	/**
	 * Ҫ��ʾ���б�
	 */
	private ArrayList<InfoBean> list;
	private ArrayList<InfoBean> temp_List;
	/**
	 * ����������
	 */
	private InfoListAdapter adapter;

	private OnRefreshListener headRefreshListener;
	private OnRefreshListener footrefreshListener;

	private int clickedPost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_info, null);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ctx = getActivity();
		initViewComponent();
		initData();
		requestQueue = Volley.newRequestQueue(ctx);
		// ��һ�μ�������
		initListView();
	}

	private void initData() {
		list = new ArrayList<InfoBean>();
		temp_List = new ArrayList<InfoBean>();
		adapter = new InfoListAdapter(ctx, list, listview,
				NetUtil.getBookUpLoadAddress(ctx));
	}

	private void initViewComponent() {
		listview = (MyListView) view.findViewById(R.id.listView);
		ll_loading = (LinearLayout) view.findViewById(R.id.ll_main_progress);
		listview.setOnItemClickListener(onItemClickListener);
	}

	/**
	 * ListView����¼�
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// ��ϸ��Ϣ

			showToast("�����");
			
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
					retrievalinfo(true, 0, 0);
				} else {
					// ���ظ����
					// �������id ��һ��
					retrievalinfo(true, list.get(0).info_id, 2);
				}
			}
		};
		listview.setonRefreshListener(headRefreshListener);

		footrefreshListener = new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (list.isEmpty()) {
					retrievalinfo(false, 0, 0);
				} else {
					// ���ظ����
					// �������id ��һ��
					retrievalinfo(false, list.get(list.size() - 1).info_id, 1);
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
				// ��ʾ ���ظ���
				listview.dispalyFooterView();
				if (type == 0 || type == 1) {
					list.addAll(temp_List);
					// Log.d(TAG, "list addLast");
				} else if (type == 2) {
					// Log.d(TAG, "list addFirst");
					list.addAll(temp_List);
					// ����
					Collections.sort(list, InfoBean.Comparator);
				}
			}

		});

	}

	/**
	 * �����鼮
	 * 
	 * @param sign
	 * @param bookId
	 * @param type
	 */
	private void retrievalinfo(final boolean sign, final int info_id,
			final int type) {
		try {
			if (listview.getLoading()) {
				// ���ڼ���
				return;
			}
			// ��ʼ��ʾ
			startRetrieval();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "retrievalInfo");
			jsonObject.put("info_id", info_id);
			jsonObject.put("type", type);

			Log.d(TAG, "jsonObject=" + jsonObject);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getInfobuyBusinessAddress(ctx), requestQueue,
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
					JSONArray infoArray = jsonObject.getJSONArray("infoArray");
					temp_List.clear();
					Log.d(TAG, "bookArray=" + infoArray);
					int len = infoArray.length();
					for (int i = 0; i < len; i++) {
						
						JSONObject infoObject = infoArray.getJSONObject(i);
						InfoBean infoBean = new InfoBean();
						infoBean.info_id = infoObject.getInt("info_id");
						infoBean.user_id = infoObject.getInt("user_id");

						infoBean.bookname = infoObject.getString("bookname");
						
						infoBean.price = infoObject.getString("price");
						infoBean.address = infoObject.getString("address");
						infoBean.concern_num = infoObject.getInt("concern_num");

						infoBean.accusation_num = infoObject
								.getInt("accusation_num");
						infoBean.generate_time = infoObject
								.getString("generate_time");

						temp_List.add(infoBean);
					}
					if (!temp_List.isEmpty()) {
						updateData(type);

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
		// ������ʾ
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
