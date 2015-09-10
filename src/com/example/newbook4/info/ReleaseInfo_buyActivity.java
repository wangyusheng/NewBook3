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

public class ReleaseInfo_buyActivity extends BaseActivity implements
		View.OnTouchListener {
	
	private EditText et_bookname,et_price,et_content;
	private String address1;
	private String address2;
	private String contact;
	private String phone;
	private ProgressDialog proDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_infobuy_layout);
		initViewComponent();
	}
	private void initViewComponent() {		
		et_bookname=(EditText) findViewById(R.id.et_bookname);
		et_price=(EditText)findViewById(R.id.et_price);
		et_content = (EditText) findViewById(R.id.et_content);
		et_content.setOnTouchListener(this);

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);//隐藏“+”号按键
		((TextView) findViewById(R.id.actionbar_tv)).setText("求购-信息");
		((Button) findViewById(R.id.btn_release))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						release_Infobuy();

					}
				});

	}
	protected void release_Infobuy() {
		
		String bookname = et_bookname.getText().toString();
		if (!Tools.strIsOk(bookname)) {
			showToast("书名无效");
			return;
		}

		String price = et_price.getText().toString();
		if (!Tools.strIsOk(price)) {
			showToast("价格无效");
			return;
		}

		if (!Tools.strIsOk(address1) || !Tools.strIsOk(address2)
				|| !Tools.strIsOk(contact) || !Tools.strIsOk(phone)) {
			showToast("联系方式无效");
			return;
		}
	
		try {
			onCreateDialog(1);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "release-infobuy");
			jsonObject.put("user_id", baseInfo.userId);
			jsonObject.put("bookname", bookname);
			jsonObject.put("price", price);
			
			JSONObject more_Info = new JSONObject();
			more_Info.put("address1", address1);
			more_Info.put("address2", address2);
			more_Info.put("contact", contact);
			more_Info.put("phone", phone);
			jsonObject.put("more_Info", more_Info.toString());
			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getInfobuyBusinessAddress(ctx), requestQueue,
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

			if (v.getId() == R.id.et_content) {
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
