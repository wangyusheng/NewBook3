package com.example.newbook4.club;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.LoginActivity;
import com.example.newbook4.MainActivity;
import com.example.newbook4.R;
import com.example.newbook4.me.AddAddressActivity;
import com.example.newbook4.tools.KeyConstant;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.SharedPreferencesTool;
import com.example.newbook4.tools.Tools;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class ReleaseClubActivity extends BaseActivity implements
		View.OnTouchListener {
	private Button btn_book;

	private LinearLayout tag_vessel;
	private List<String> mTagList = new ArrayList<String>();
	private int screenWidth = 0;

	private EditText et_time, et_content, et_subject;

	private TextView tv_booknum;

	private String address1;
	private String address2;
	private String contact;
	private String phone;

	private ProgressDialog proDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_club_layout);
		initViewComponent();
	}

	private void initViewComponent() {
		btn_book = (Button) findViewById(R.id.btn_book);

		btn_book.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 添加标签
				AddTagDialog();
			}
		});
		screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素
		tag_vessel = (LinearLayout) findViewById(R.id.tag_vessel);

		et_time = (EditText) findViewById(R.id.et_time);
		et_content = (EditText) findViewById(R.id.et_content);

		et_subject = (EditText) findViewById(R.id.et_subject);
		et_time.setOnTouchListener(this);
		et_content.setOnTouchListener(this);

		tv_booknum = (TextView) findViewById(R.id.tv_booknum);

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.actionbar_tv)).setText("俱乐部-信息");

		((Button) findViewById(R.id.btn_release))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						release_Club();

					}
				});

	}

	protected void release_Club() {
		// 主题
		// 时间
		// 联系方式
		// 书籍
		String subject = et_subject.getText().toString();
		if (!Tools.strIsOk(subject)) {
			showToast("主题无效");
			return;
		}

		String time = et_time.getText().toString();
		if (!Tools.strIsOk(time)) {
			showToast("时间无效");
			return;
		}

		if (!Tools.strIsOk(address1) || !Tools.strIsOk(address2)
				|| !Tools.strIsOk(contact) || !Tools.strIsOk(phone)) {
			showToast("联系方式无效");
			return;
		}

		if (mTagList.isEmpty()) {
			showToast("推荐书籍为空");
			return;
		}
		try {
			onCreateDialog(1);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "release-club");
			jsonObject.put("user_id", baseInfo.userId);
			jsonObject.put("subject", subject);
			jsonObject.put("time", time);

			JSONObject more_Info = new JSONObject();
			more_Info.put("address1", address1);
			more_Info.put("address2", address2);
			more_Info.put("contact", contact);
			more_Info.put("phone", phone);
			jsonObject.put("more_Info", more_Info.toString());

			JSONArray books = new JSONArray();
			for (String book : mTagList) {
				books.put(book);
			}
			jsonObject.put("recommend_book", books.toString());
			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getClubBusinessAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							proDialog.dismiss();
							dealResult(response);

						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							proDialog.dismiss();
							showToast(VolleyErrorHelper.getMessage(error, ctx));
						}

					});

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void reset() {

	}

	protected void dealResult(JSONObject jsonObject) {
		try {
			int error_Code = jsonObject.getInt("errorcode");
			if (error_Code == 0) {
				showToast("发布成功,1秒后自动返回！");

				new CountDownTimer(1000, 1000) {
					public void onTick(long millisUntilFinished) {
					}

					public void onFinish() {
						finish();
					}
				}.start();

			} else {
				showToast("error_Code：" + error_Code);
			}
		} catch (JSONException e) {
			showToast("服务器返回值出现错误");
			e.printStackTrace();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
			proDialog = android.app.ProgressDialog.show(ctx, "发布", "正在发布图书！");
			proDialog.setCancelable(false);
		}
		return super.onCreateDialog(id);
	}

	/**
	 * 添加标签的对话框
	 */
	public void AddTagDialog() {
		final Dialog dlg = new Dialog(ctx, R.style.dialog);
		dlg.show();
		dlg.getWindow().setGravity(Gravity.CENTER);
		dlg.getWindow().setLayout((int) (screenWidth * 0.8),
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);
		dlg.getWindow().setContentView(R.layout.release_club_add_tags_dialg);
		TextView add_tag_dialg_title = (TextView) dlg
				.findViewById(R.id.add_tag_dialg_title);
		final EditText add_tag_dialg_content = (EditText) dlg
				.findViewById(R.id.add_tag_dialg_content);
		TextView add_tag_dialg_no = (TextView) dlg
				.findViewById(R.id.add_tag_dialg_no);
		TextView add_tag_dialg_ok = (TextView) dlg
				.findViewById(R.id.add_tag_dialg_ok);
		add_tag_dialg_title.setText("添加图书");
		add_tag_dialg_no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		add_tag_dialg_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newTag = add_tag_dialg_content.getText().toString();
				if (TextUtils.isEmpty(newTag)) {
					showToast("书名为空");
					return;
				}
				newTag = newTag.trim();
				if (TextUtils.isEmpty(newTag)) {
					showToast("书名无效");
					return;
				}
				if (mTagList.contains(newTag)) {
					showToast("书名为不可以重复");
					return;
				}
				InputMethodManager imm = (InputMethodManager) ctx
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
						InputMethodManager.HIDE_NOT_ALWAYS);

				mTagList.add(newTag);
				AddTag(newTag, mTagList.size() - 1);

				dlg.dismiss();
				tv_booknum.setText("共" + mTagList.size() + "本(长按删除)");
			}
		});
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

		mTag.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				// 长按标签删除操作
				final AlertDialog dlg = new AlertDialog.Builder(ctx).create();
				dlg.show();
				dlg.getWindow().setGravity(Gravity.CENTER);
				dlg.getWindow().setLayout((int) (screenWidth * 0.8),
						android.view.WindowManager.LayoutParams.WRAP_CONTENT);
				dlg.getWindow().setContentView(
						R.layout.release_club_add_tags_dialg);
				TextView add_tag_dialg_title = (TextView) dlg
						.findViewById(R.id.add_tag_dialg_title);
				EditText add_tag_dialg_content = (EditText) dlg
						.findViewById(R.id.add_tag_dialg_content);
				TextView add_tag_dialg_no = (TextView) dlg
						.findViewById(R.id.add_tag_dialg_no);
				TextView add_tag_dialg_ok = (TextView) dlg
						.findViewById(R.id.add_tag_dialg_ok);
				add_tag_dialg_title.setText("删除确认");
				add_tag_dialg_content.setText("您确定要删除“"
						+ mTag.getText().toString() + "”吗？");
				add_tag_dialg_no.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
				add_tag_dialg_ok.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						tag_vessel.removeView(mTag);
						for (int j = 0; j < mTagList.size(); j++) {
							Log.v("==", mTag.getText().toString() + "=="
									+ mTagList.get(j).toString());
							if (mTag.getText().toString().trim()
									.equals(mTagList.get(j).toString().trim())) {
								mTagList.remove(j);
							}
						}
						dlg.dismiss();
						tv_booknum.setText("共" + mTagList.size() + "本(长按删除)");
					}
				});

				return true;
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = View.inflate(this, R.layout.date_time_dialog, null);
			final DatePicker datePicker = (DatePicker) view
					.findViewById(R.id.date_picker);
			final TimePicker timePicker = (android.widget.TimePicker) view
					.findViewById(R.id.time_picker);
			builder.setView(view);

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH), null);

			timePicker.setIs24HourView(true);
			timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(Calendar.MINUTE);

			if (v.getId() == R.id.et_time) {

				builder.setTitle("选中时间");
				builder.setPositiveButton("确  定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								StringBuffer sb = new StringBuffer();
								sb.append(String.format("%d-%02d-%02d",
										datePicker.getYear(),
										datePicker.getMonth() + 1,
										datePicker.getDayOfMonth()));
								sb.append("  ");
								sb.append(timePicker.getCurrentHour())
										.append(":")
										.append(timePicker.getCurrentMinute());

								et_time.setText(sb);
								et_time.requestFocus();

								dialog.cancel();
							}
						});
				Dialog dialog = builder.create();
				dialog.show();
			} else if (v.getId() == R.id.et_content) {
				Intent intent = new Intent();
				intent.setClass(ctx, AddAddressActivity.class);
				intent.putExtra("type", 0);
				startActivityForResult(intent, 1);
			}

		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				address1 = data.getStringExtra("address1");
				address2 = data.getStringExtra("address2");

				contact = data.getStringExtra("contact");
				phone = data.getStringExtra("phone");

				et_content.setText("联系人:" + contact + "\n电话:" + phone + "\n地址:"
						+ address1 + address2);
			}
		}

	}
}
