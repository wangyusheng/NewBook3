package com.example.newbook4.club;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.R;
import com.example.newbook4.bean.ClubBean;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;

public class ClubDetailActivity extends BaseActivity implements
		View.OnClickListener {
	private static final String TAG = "ClubDetailActivity";
	private TextView actionbar_tv;
	private TextView tv_enrollnum, tv_concernnum, content_way;
	private ClubBean clubBean;
	private int clubId;
	private String clubTopic;

	private Button btn_concern, btn_enroll, btn_report;
	private TextView tv_concern, tv_enroll, tv_report;

	private String[] info_type;
	private boolean[] info_checkSign;

	private Dialog progressDialog;

	private ScrollView scrollView;

	private RelativeLayout rl_reload;

	private TextView tv_reload;
	private Button btn_reload;
	private boolean isAvailable = true;

	private LinearLayout tag_vessel;
	private List<String> mTagList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_club_detial);
		Intent intent = getIntent();
		clubId = intent.getIntExtra("clubId", -1);
		clubTopic = intent.getStringExtra("clubTopic");
		if (clubId <= 0) {
			finish();
		} else {
			setupViewComponent();
			// 加载数据
			loadData(clubId);
		}

	}

	@SuppressLint("ClickableViewAccessibility")
	private void setupViewComponent() {

		tag_vessel = (LinearLayout) findViewById(R.id.tag_vessel);

		// 设置加号不可见
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);
		actionbar_tv = (TextView) findViewById(R.id.actionbar_tv);

		actionbar_tv.setText(clubTopic);

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}

				});

		scrollView = (ScrollView) findViewById(R.id.sv_content);
		rl_reload = (RelativeLayout) findViewById(R.id.rl_reload);

		scrollView.setVisibility(View.GONE);
		rl_reload.setVisibility(View.VISIBLE);

		tv_concern = (TextView) findViewById(R.id.tv_concern);
		tv_enroll = (TextView) findViewById(R.id.tv_enroll);
		tv_report = (TextView) findViewById(R.id.tv_report);

		btn_concern = (Button) findViewById(R.id.btn_concern);
		btn_enroll = (Button) findViewById(R.id.btn_enroll);
		btn_report = (Button) findViewById(R.id.btn_report);

		btn_concern.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下
					tv_concern.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 弹起
					tv_concern.setTextColor(getResources().getColor(
							R.color.textcolor_u));
				}

				return false;

			}

		});

		btn_enroll.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下
					tv_enroll.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 弹起
					tv_enroll.setTextColor(getResources().getColor(
							R.color.textcolor_u));
				}

				return false;

			}

		});

		btn_report.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下
					tv_report.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 弹起
					tv_report.setTextColor(getResources().getColor(
							R.color.textcolor_u));
				}

				return false;

			}

		});

		btn_concern.setOnClickListener(this);
		btn_enroll.setOnClickListener(this);
		btn_report.setOnClickListener(this);

		tv_reload = (TextView) findViewById(R.id.tv_reload);
		btn_reload = (Button) findViewById(R.id.btn_reload);
		btn_reload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 加载数据
				loadData(clubId);
			}
		});

		btn_reload.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下
					tv_reload.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 弹起
					tv_reload.setTextColor(getResources().getColor(
							R.color.textcolor_u));
				}

				return false;

			}

		});
	}

	/**
	 * 加载成功
	 */
	private void setupLoadSuccess() {

		scrollView.setVisibility(View.VISIBLE);
		rl_reload.setVisibility(View.GONE);

		info_type = getResources().getStringArray(R.array.bad_info);

		tv_enrollnum = (TextView) findViewById(R.id.tv_enrollnum);
		tv_concernnum = (TextView) findViewById(R.id.tv_concernnum);

		content_way = (TextView) findViewById(R.id.content_way);
		content_way.setTypeface(myApplication.popTypeface);

	}

	/**
	 * 加载失败
	 */
	private void setupLoadFail() {
		scrollView.setVisibility(View.GONE);
		rl_reload.setVisibility(View.VISIBLE);
	}

	/**
	 * 加载数据
	 */
	private void loadData(int bookId) {
		Log.d(TAG, "loadData");
		try {
			// 显示对话框
			dialogShow();
			clubBean = null;

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "retrievalClubSingle");
			jsonObject.put("clubId", clubId);

			Log.d(TAG, "jsonObject=" + jsonObject);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getClubBusinessAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							// 隐藏对话框
							dialogHide();
							dealResult(response);
						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							// 隐藏对话框
							dialogHide();
							showToast(VolleyErrorHelper.getMessage(error, ctx));
							setupLoadFail();
						}

					});

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	protected void dealResult(JSONObject response) {
		try {

			int errorcode = response.getInt("errorcode");
			if (errorcode == 0) {
				JSONObject clubObject = response.getJSONObject("jsonObject");
				clubBean = new ClubBean();
				clubBean.club_id = clubObject.getInt("club_id");
				clubBean.user_id = clubObject.getInt("user_id");

				clubBean.topic = clubObject.getString("topic");
				clubBean.recommend_book = clubObject
						.getString("recommend_book");
				clubBean.time = clubObject.getString("time");
				clubBean.address = clubObject.getString("address");
				clubBean.enroll_num = clubObject.getInt("enroll_num");
				clubBean.concern_num = clubObject.getInt("concern_num");

				clubBean.accusation_num = clubObject.getInt("accusation_num");
				clubBean.generate_time = clubObject.getString("generate_time");
				// 加载成功
				setupLoadSuccess();
				displayResult();
				isOk();

			} else {
				showToast("errorcode=" + errorcode);
			}
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			showToast(e.toString());
		}

	}

	/**
	 * 显示结果
	 */
	private void displayResult() {
		StringBuilder sb = new StringBuilder();
		sb.append("举行时间：" + clubBean.time + "\n");
		sb.append("发布时间：" + clubBean.getTime() + "\n");
		sb.append("举行地址:" + clubBean.getAddress1() + clubBean.getAddress2()
				+ "\n");
		sb.append("联系人：" + clubBean.getContact() + "\n");
		sb.append("联系电话：" + clubBean.getPhone() + "\n");
		sb.append("发布者：" + clubBean.user_id);
		ArrayList<String> books = clubBean.getAllBooks();
		for (String book : books) {
			mTagList.add(book);
			AddTag(book, mTagList.size() - 1);
		}

		content_way.setText(sb.toString());
		actionbar_tv.setText(clubBean.topic);

		tv_enrollnum.setText(clubBean.enroll_num + "");
		tv_concernnum.setText(clubBean.concern_num + "");

		// 评论数量
		// comment_num = (TextView) findViewById(R.id.comment_num);
		// comment_num.setTypeface(type);
		// comment_num.setText("123");
	}

	/**
	 * 添加标签
	 * 
	 * @param tag
	 * @param i
	 */
	@SuppressLint("NewApi")
	public void AddTag(String tag, int i) {
		final TextView mTag = new TextView(ctx);
		mTag.setText("  " + tag + "   ");
		mTag.setGravity(Gravity.CENTER);
		mTag.setTextSize(20);
		mTag.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.mylable));
		mTag.setTextColor(Color.GRAY);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 10, 20, 10);
		tag_vessel.addView(mTag, i, params);

	}

	private void isOk() {
		// 时间 是否过了
		// TODO
		// if (bookBean.state != 0) {
		// actionbar_tv.setText(bookBean.book_Name + "(人数已满)");
		// showToast("不好意思，这本书籍预定人数已满！");
		// bookBean = null;
		// isAvailable = false;
		// }
	}

	@Override
	public void onClick(View v) {
		if (clubBean == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_concern:
			showToast("关注");
			break;
		case R.id.btn_enroll:
			showToast("参加");
			break;
		case R.id.btn_report:
			showDialog(1);
			break;

		}

	}

	/**
	 * 举报
	 */
	private void reportBadInfo() {
		int len = info_type.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			if (info_checkSign[i]) {
				sb.append(info_type[i] + "\t");
			}
		}
		String result = sb.toString();
		if (TextUtils.isEmpty(result)) {
			return;
		}
		showToast(result);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 0:
			// mTabIndicator.get(0).setIconAlpha(0f);
			break;

		}
	}

	public void showToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
			int len = info_type.length;
			info_checkSign = new boolean[len];
			for (int i = 0; i < len; i++) {
				info_checkSign[i] = false;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ClubDetailActivity.this);
			builder.setIcon(R.drawable.report);
			builder.setTitle("举报");

			// 设置一个单项选择下拉框
			builder.setMultiChoiceItems(info_type, null,
					new DialogInterface.OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							info_checkSign[which] = isChecked;
						}
					});
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// mTabIndicator.get(3).setIconAlpha(0);
							reportBadInfo();
						}

					});
			builder.setCancelable(false);

			builder.show();
		}
		return super.onCreateDialog(id);
	}

	/**
	 * 显示对话框
	 */
	private void dialogShow() {
		Log.d(TAG, "dialogShow");
		runOnUiThread(new Runnable() {
			public void run() {

				tv_reload.setVisibility(View.GONE);
				btn_reload.setVisibility(View.GONE);
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				progressDialog = new Dialog(ctx, R.style.progress_dialog);
				progressDialog.setContentView(R.layout.dialog);
				progressDialog.setCancelable(false);
				progressDialog.getWindow().setBackgroundDrawableResource(
						android.R.color.transparent);
				TextView msg = (TextView) progressDialog
						.findViewById(R.id.id_tv_loadingmsg);
				msg.setText("正在加载");
				progressDialog.show();
			}

		});
	}

	/**
	 * 隐藏对话框
	 */
	private void dialogHide() {
		Log.d(TAG, "dialogHide");
		runOnUiThread(new Runnable() {
			public void run() {
				tv_reload.setVisibility(View.VISIBLE);
				btn_reload.setVisibility(View.VISIBLE);
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}

			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isAvailable) {
				this.setResult(RESULT_OK);
			} else {
				this.setResult(RESULT_CANCELED);
			}
			this.finish();
		}
		return super.onKeyDown(keyCode, event);

	}
}
