package com.example.newbook4.me;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.R;
import com.example.newbook4.citys.DBManager;
import com.example.newbook4.citys.MyAdapter;
import com.example.newbook4.citys.MyListItem;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddAddressActivity extends BaseActivity {
	private static String TAG = "AddAddressActivity";
	private EditText et_address, et_contact, et_phone;
	private Button btn_save;
	private boolean submitting = false;
	private DBManager dbm;
	private SQLiteDatabase db;
	private Spinner spinner1 = null;
	private Spinner spinner2 = null;
	private Spinner spinner3 = null;
	private String province = null;
	private String city = null;
	private String district = null;
	private final int RESET = 0;

	private boolean sign = false;

	private int type;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case RESET:
				reset();
				break;

			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addaddress);
		Intent intent = getIntent();
		type = intent.getIntExtra("type", -1);
		initActionbar();
		initViewComponent();

	}

	private void initActionbar() {
		((TextView) findViewById(R.id.actionbar_tv)).setText("添加地址");
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new OnClickListener() {
					// 返回键

					@Override
					public void onClick(View v) {
						doBack();
					}

				});

	}

	private void initViewComponent() {

		et_address = (EditText) findViewById(R.id.et_address);
		et_contact = (EditText) findViewById(R.id.et_contact);
		et_phone = (EditText) findViewById(R.id.et_phone);

		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city)
						|| TextUtils.isEmpty(district)) {
					showToast("所在地区不完整");
					return;
				}
				String address = et_address.getText().toString();
				if (TextUtils.isEmpty(address)) {
					showToast("请填写详细地址");
					return;
				}
				String contact = et_contact.getText().toString();
				if (TextUtils.isEmpty(contact)) {
					showToast("请填写收货人姓名");
					return;
				}
				String phone = et_phone.getText().toString();
				if (TextUtils.isEmpty(phone)) {
					showToast("请填写联系方式");
					return;
				}
				if (type == 0) {
					sign = true;
					doBack();
				} else {
					addAddress(province.trim() + city.trim() + district.trim(),
							address, contact, phone);
				}
			}

		});

		spinner1 = (Spinner) findViewById(R.id.spinner_pro);
		spinner2 = (Spinner) findViewById(R.id.spinner_city);
		spinner3 = (Spinner) findViewById(R.id.spinner_dict);
		spinner1.setPrompt("省");
		spinner2.setPrompt("城市");
		spinner3.setPrompt("地区");

		initSpinner1();

	}

	protected void addAddress(String address1, String address2, String contact,
			String phone) {
		try {
			if (getBusy()) {
				showToast("正在提交");
				return;
			}
			Log.d(TAG, "addAddress");
			setBusy(true);

			JSONObject jsonObject = new JSONObject();

			jsonObject.put("action", "add_address");
			jsonObject.put("address1", address1);
			jsonObject.put("address2", address2);
			jsonObject.put("contact", contact);
			jsonObject.put("phone", phone);
			jsonObject.put("userId", baseInfo.userId);

			Log.d(TAG, "jsonObject=" + jsonObject);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBaseAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							addCompleted(response.toString());
						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							showToast(VolleyErrorHelper.getMessage(error, ctx));
							setBusy(false);
						}

					});

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	protected void addCompleted(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			int error_Code = jsonObject.getInt("errorcode");
			if (error_Code == 0) {
				showToast("保存地址成功！");
				sign = true;

				doBack();
				// // 清空
				// handler.sendEmptyMessage(RESET);

			} else {
				showToast("error_Code：" + error_Code);
			}

		} catch (JSONException e) {
			showToast("服务器返回值出现错误");
		}
		setBusy(false);
	}

	private void reset() {
		et_address.setText("");
		et_contact.setText("");
		et_phone.setText("");
	}

	private synchronized void setBusy(boolean b) {
		submitting = b;
	}

	private synchronized boolean getBusy() {
		return submitting;
	}

	public void initSpinner1() {
		dbm = new DBManager(this);
		dbm.openDatabase();
		db = dbm.getDatabase();
		List<MyListItem> list = new ArrayList<MyListItem>();
		MyListItem myListItem = new MyListItem();
		myListItem.setName("");
		myListItem.setPcode(null);
		list.add(myListItem);
		try {
			String sql = "select * from province";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("code"));
				byte bytes[] = cursor.getBlob(2);
				String name = new String(bytes, "gbk");
				myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("code"));
			byte bytes[] = cursor.getBlob(2);
			String name = new String(bytes, "gbk");
			myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		dbm.closeDatabase();
		db.close();

		MyAdapter myAdapter = new MyAdapter(this, list);
		spinner1.setAdapter(myAdapter);
		spinner1.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
	}

	public void initSpinner2(String pcode) {
		if (TextUtils.isEmpty(pcode)) {
			spinner2.setAdapter(null);
			return;
		}
		dbm = new DBManager(this);
		dbm.openDatabase();
		db = dbm.getDatabase();
		List<MyListItem> list = new ArrayList<MyListItem>();

		try {
			String sql = "select * from city where pcode='" + pcode + "'";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("code"));
				byte bytes[] = cursor.getBlob(2);
				String name = new String(bytes, "gbk");
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("code"));
			byte bytes[] = cursor.getBlob(2);
			String name = new String(bytes, "gbk");
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		dbm.closeDatabase();
		db.close();

		MyAdapter myAdapter = new MyAdapter(this, list);
		spinner2.setAdapter(myAdapter);
		spinner2.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
	}

	public void initSpinner3(String pcode) {
		if (TextUtils.isEmpty(pcode)) {
			spinner3.setAdapter(null);
			return;
		}
		dbm = new DBManager(this);
		dbm.openDatabase();
		db = dbm.getDatabase();
		List<MyListItem> list = new ArrayList<MyListItem>();

		try {
			String sql = "select * from district where pcode='" + pcode + "'";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("code"));
				byte bytes[] = cursor.getBlob(2);
				String name = new String(bytes, "gbk");
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("code"));
			byte bytes[] = cursor.getBlob(2);
			String name = new String(bytes, "gbk");
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		dbm.closeDatabase();
		db.close();

		MyAdapter myAdapter = new MyAdapter(this, list);
		spinner3.setAdapter(myAdapter);
		spinner3.setOnItemSelectedListener(new SpinnerOnSelectedListener3());
	}

	class SpinnerOnSelectedListener1 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			province = ((MyListItem) adapterView.getItemAtPosition(position))
					.getName();
			String pcode = ((MyListItem) adapterView
					.getItemAtPosition(position)).getPcode();

			initSpinner2(pcode);
			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}

	class SpinnerOnSelectedListener2 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			city = ((MyListItem) adapterView.getItemAtPosition(position))
					.getName();
			String pcode = ((MyListItem) adapterView
					.getItemAtPosition(position)).getPcode();

			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}

	class SpinnerOnSelectedListener3 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			district = ((MyListItem) adapterView.getItemAtPosition(position))
					.getName();
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doBack();

		}
		return super.onKeyDown(keyCode, event);

	}

	private void doBack() {
		if (sign) {
			if (type == 0) {
				String address = et_address.getText().toString();
				String contact = et_contact.getText().toString();
				String phone = et_phone.getText().toString();
				Intent intent = new Intent();
				intent.putExtra("address1", province.trim() + city.trim()
						+ district.trim());
				intent.putExtra("address2", address);
				intent.putExtra("contact", contact);
				intent.putExtra("phone", phone);
				setResult(RESULT_OK, intent);
			} else {
				setResult(RESULT_OK);
			}
		} else {
			setResult(RESULT_CANCELED);
		}

		finish();
	}
}
