package com.example.newbook4.info;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.R;
import com.example.newbook4.me.AddAddressActivity;
import com.example.newbook4.tools.NetUtil;
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

public class ReleasePosterActivity extends BaseActivity implements
View.OnTouchListener {
	private Button btn_book;
	private EditText et_subject,et_person,et_time,et_place;
	private String address1;
	private String address2;
	private String contact;
	private String phone;

	private ProgressDialog proDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_poster_layout);
		initViewComponent();

	}

	private void initViewComponent() {
		
		et_subject = (EditText) findViewById(R.id.et_subject);
		et_person=(EditText) findViewById(R.id.et_person);
		et_time = (EditText) findViewById(R.id.et_time);
		et_place=(EditText) findViewById(R.id.et_place);

		
		et_time.setOnTouchListener(this);	
		

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.actionbar_tv)).setText("海报-信息");

		((Button) findViewById(R.id.btn_release))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						release_Poster();

					}
				});

	}

	protected void release_Poster() {
		// 主题
		// 时间
		// 联系方式
		// 书籍
		String subject = et_subject.getText().toString();
		if (!Tools.strIsOk(subject)) {
			showToast("主题无效");
			return;
		}
		String person=et_person.getText().toString();
		if(!Tools.strIsOk(person)){
			showToast("主讲人无效");
			return;
		}
		String time = et_time.getText().toString();
		if (!Tools.strIsOk(time)) {
			showToast("时间无效");
			return;
		}
		String address=et_place.getText().toString();
		if(!Tools.strIsOk(address)){
			showToast("地址无效");
			return;
		}
		

	
		try {
			onCreateDialog(1);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "release-poster");
			jsonObject.put("user_id", baseInfo.userId);
			jsonObject.put("subject", subject);
			jsonObject.put("person",person);
			jsonObject.put("time", time);
			jsonObject.put("address", address);		

			
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
			} 

		}

		return true;
	}
	
}
