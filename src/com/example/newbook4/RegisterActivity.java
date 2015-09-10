package com.example.newbook4;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.VolleyError;
import com.example.newbook4.tools.CodeGenerator;
import com.example.newbook4.tools.CodeGenerator.CodeInfo;
import com.example.newbook4.tools.CryptionTools;
import com.example.newbook4.tools.KeyConstant;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.SharedPreferencesTool;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 注册页面
 * 
 * @author li.fx 2015年8月14日09:00:12
 *
 */
public class RegisterActivity extends BaseActivity {
	private static final String TAG = "RegisterActivity";

	private EditText etUserName, etPassword, etPassword2, etCode, etEmail;

	private RadioButton radioMale;

	private ImageView imageView;

	private CodeInfo codeInfo;

	private Pattern name_Pattern = Pattern.compile("[a-zA-Z0-9]*");

	private Pattern email_Pattern = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	private Spinner spinner_Vocation, spinner_age;

	private String vocation_value, age_value;

	private final int SHOW_TOAST = 0;
	private final int DIALOG_DIMSS = SHOW_TOAST + 1;
	private final int DATA_COMPONENT = DIALOG_DIMSS + 1;
	private ProgressDialog proDialog;

	private int[] checkBox_Id = new int[] { R.id.checkbox1, R.id.checkbox2,
			R.id.checkbox3, R.id.checkbox4, R.id.checkbox5, R.id.checkbox6,
			R.id.checkbox7, R.id.checkbox8 };

	private CheckBox[] checkBoxs = null;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_TOAST:
				String str = msg.obj.toString();
				if (TextUtils.isEmpty(str)) {
					return;
				}
				Toast toast = Toast.makeText(ctx, str, Toast.LENGTH_SHORT);
				toast.show();
				break;
			case DIALOG_DIMSS:
				if (proDialog != null) {
					proDialog.dismiss();
				}
				break;

			case DATA_COMPONENT:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newregister);
		setupViewComponent();
		initData();
	}

	private void initData() {
		// 设置验证码
		setCode();
	}

	private void setupViewComponent() {
		((TextView) findViewById(R.id.actionbar_tv)).setText("注册");
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);
		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}

				});

		etUserName = (EditText) findViewById(R.id.username_edit);
		etPassword = (EditText) findViewById(R.id.password_edit);
		etPassword2 = (EditText) findViewById(R.id.password2_edit);
		etEmail = (EditText) findViewById(R.id.email_edit);

		radioMale = (RadioButton) findViewById(R.id.radioMale);

		etCode = (EditText) findViewById(R.id.code_edit);
		imageView = (ImageView) findViewById(R.id.imageview);

		etPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String pass2 = etPassword2.getText().toString();
				if (TextUtils.isEmpty(pass2)) {
					return;
				}
				String pass = s.toString();
				if (pass.equals(pass2)) {
					etPassword2.setTextColor(Color.BLACK);

				} else {
					etPassword2.setTextColor(Color.RED);
				}
			}

		});

		etPassword2.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(etPassword.getText())) {
					return;
				}
				if (TextUtils.isEmpty(s)) {
					return;
				}
				String pass = etPassword.getText().toString();
				if (pass.equals(s.toString())) {
					etPassword2.setTextColor(Color.BLACK);
				} else {
					etPassword2.setTextColor(Color.RED);
				}
			}

		});

		etCode.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 改变后
				String value = s.toString();
				if (TextUtils.isEmpty(value)) {
					return;
				}
				value = value.toLowerCase(Locale.getDefault());
				if (value.equals(codeInfo.getCode())) {
					etCode.setTextColor(Color.BLACK);
				} else {
					etCode.setTextColor(Color.RED);
				}

			}

		});

		etEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 改变后
				String value = s.toString();
				if (TextUtils.isEmpty(value)) {
					return;
				}
				Matcher matcher = email_Pattern.matcher(value);
				boolean sign = matcher.matches();
				if (sign) {
					etCode.setTextColor(Color.BLACK);
				} else {
					etCode.setTextColor(Color.RED);
				}

			}

		});

		spinner_Vocation = (Spinner) findViewById(R.id.spinner_vocation);
		spinner_Vocation
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						vocation_value = getResources().getStringArray(
								R.array.vocation)[position];

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

		spinner_age = (Spinner) findViewById(R.id.spinner_age);
		spinner_age.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				age_value = getResources().getStringArray(R.array.age)[position];

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		int len = checkBox_Id.length;
		checkBoxs = new CheckBox[len];
		for (int i = 0; i < len; i++) {
			checkBoxs[i] = (CheckBox) findViewById(checkBox_Id[i]);
		}

		((Button) findViewById(R.id.register_button))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// 注册
						String code = etCode.getText().toString();
						if (TextUtils.isEmpty(code)) {
							showToast("请填写验证码");
							return;
						}
						// 判断验证码是否正确
						code = code.toLowerCase(Locale.getDefault());
						if (!code.equals(codeInfo.getCode())) {
							showToast("验证码错误");
							return;
						}
						String userName = etUserName.getText().toString();
						if (TextUtils.isEmpty(userName)) {
							showToast("用户名不能为空");
							return;
						}
						String password = etPassword.getText().toString();
						if (TextUtils.isEmpty(password)) {
							showToast("密码不能为空");
							return;
						}

						String password2 = etPassword2.getText().toString();
						if (!password.equals(password2)) {
							showToast("两次输入的密码不一致");
							return;
						}
						if (userName.length() >= 15) {
							showToast("用户名太长");
							return;
						}
						if (password.length() >= 15) {
							showToast("密码太长");
							return;
						}

						Matcher matcher = name_Pattern.matcher(userName);
						boolean sign = matcher.matches();
						if (!sign) {
							showToast("用户名不符合规范");
							return;
						}
						matcher = name_Pattern.matcher(password);
						sign = matcher.matches();
						if (!sign) {
							showToast("密码不符合规范");
							return;
						}
						// 获取兴趣爱好
						String interest = getInterest();
						if (TextUtils.isEmpty(interest)) {
							showToast("至少选择一个兴趣爱好");
							return;
						}
						interest = interest.substring(0, interest.length() - 1);
						Log.d(TAG, "interest=" + interest);
						// 电子邮箱地址
						String email_value = etEmail.getText().toString();
						if (TextUtils.isEmpty(email_value)) {
							showToast("请填写电子邮箱");
							return;
						}
						matcher = email_Pattern.matcher(email_value);
						sign = matcher.matches();
						if (!sign) {
							showToast("电子邮箱不符合规范");
							return;
						}

						register(userName, password, interest, email_value);
					}
				});

		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 更换验证码
				setCode();
			}
		});
	}

	/**
	 * 获得兴趣爱好
	 * 
	 * @return
	 */
	private String getInterest() {
		StringBuilder sb = new StringBuilder();
		for (CheckBox checkBox : checkBoxs) {
			if (checkBox.isChecked()) {
				sb.append(checkBox.getText() + ",");
			}
		}
		return sb.toString();
	}

	/**
	 * 显示对话框
	 */
	private void dialogShow() {

		runOnUiThread(new Runnable() {
			public void run() {
				if (proDialog != null) {
					proDialog.dismiss();
					proDialog = null;
				}
				proDialog = android.app.ProgressDialog.show(ctx, "注册", "正在注册！");
			}

		});
	}

	/**
	 * 隐藏对话框
	 */
	private void dialogHide() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (proDialog != null) {
					proDialog.dismiss();
					proDialog = null;
				}

			}

		});
	}

	/**
	 * 注册
	 * 
	 * @param userName
	 * @param password
	 * @param interest
	 * @param email_value
	 */
	private void register(final String userName, final String password,
			final String interest, String email_value) {
		try {
			Log.d(TAG, "register");
			dialogShow();
			// 加密
			final String temp_password = CryptionTools.getInstance()
					.mySha1_decrypt(password);

			// 向服务器请求信息
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("action", "register");

			jsonObject.put("username", userName);
			jsonObject.put("password", temp_password);
			jsonObject.put("interest", interest);
			jsonObject.put("age", age_value);
			jsonObject.put("vocation", vocation_value);
			jsonObject.put("email", email_value);
			if (radioMale.isChecked()) {
				jsonObject.put("sex", "男");
			} else {
				jsonObject.put("sex", "女");
			}

			Log.d(TAG, jsonObject.toString());
			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getRegisterAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							register_Result(response.toString(), userName,
									password);
						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							onErrorResponse_Result(error);
						}

					});

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	protected void onErrorResponse_Result(VolleyError error) {
		// 隐藏对话框
		dialogHide();
		showToast(VolleyErrorHelper.getMessage(error, ctx));
	}

	protected void register_Result(String response, String userName,
			String password) {
		// 隐藏对话框
		dialogHide();
		try {
			JSONObject jsonObject = new JSONObject(response);
			int error_Code = jsonObject.getInt("errorcode");
			if (error_Code == 0) {
				// 存储信息
				SharedPreferencesTool.writeLongValue(ctx,
						KeyConstant.time_login, System.currentTimeMillis());
				SharedPreferencesTool.writeStringValue(ctx,
						KeyConstant.user_Name, userName);
				SharedPreferencesTool.writeStringValue(ctx,
						KeyConstant.pass_Word, CryptionTools.getInstance()
								.mySha1_decrypt(password));

				showToast("注册成功！");
				// 跳转
				Intent intent = new Intent();
				intent.setClass(ctx, LoginActivity.class);
				startActivity(intent);
				finish();
			} else if (error_Code == 6) {
				showToast("用户名已存在，换个名字吧！");
			} else {
				showToast("error_Code：" + error_Code);
			}

		} catch (JSONException e) {
			showToast("服务器返回值出现错误");
		}

	}

	/**
	 * 设置验证码
	 */
	private void setCode() {
		codeInfo = CodeGenerator.getInstance().generCode();
		imageView.setImageBitmap(codeInfo.getBitmap());
		etCode.setText("");
	}

}
