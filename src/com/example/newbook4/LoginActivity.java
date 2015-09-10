package com.example.newbook4;

import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.VolleyError;
import com.example.newbook4.tools.CryptionTools;
import com.example.newbook4.tools.KeyConstant;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.SharedPreferencesTool;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 主界面
 * 
 * @author Li.fx 2015年8月14日08:39:55
 *
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	private TextView register_link, register_tv;
	private Button login_button;
	private EditText username_edit, password_edit;
	private ProgressDialog proDialog;

	public static final int LOGIN_SUCCESS = 0; // 登录成功
	public static final int LOGIN_FAILED = LOGIN_SUCCESS + 1; // 登录失败

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// 初始化控件
		initViewComponent();
		// 默认登陆
		loginDefault();
	}

	private void initViewComponent() {
		register_link = (TextView) findViewById(R.id.register_link);
		register_link.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);

			}
		});
		login_button = (Button) findViewById(R.id.login_button);
		username_edit = (EditText) findViewById(R.id.username_edit);
		password_edit = (EditText) findViewById(R.id.password_edit);

		login_button.setOnClickListener(new View.OnClickListener() {
			// 注册
			@Override
			public void onClick(View v) {
				String lsUsername = username_edit.getText().toString();
				if (TextUtils.isEmpty(lsUsername)) {
					showToast("用户名不能为空");
					return;
				}
				String lsPassword = password_edit.getText().toString();
				if (TextUtils.isEmpty(lsPassword)) {
					showToast("密码不能为空");
					return;
				}
				startLogin(lsUsername, CryptionTools.getInstance()
						.mySha1_decrypt(lsPassword));
			}
		});
		register_tv = (TextView) findViewById(R.id.register_tv);
		register_tv.setTypeface(myApplication.popTypeface);
		register_tv.setText("书山有路，勤为径！");
	}

	/**
	 * 从配置文件中 读取登录
	 */
	private void loginDefault() {
		// 读取默认值
		String userName = SharedPreferencesTool.readStringValue(ctx,
				KeyConstant.user_Name);
		if (TextUtils.isEmpty(userName)) {
			return;
		}
		String passWord = SharedPreferencesTool.readStringValue(ctx,
				KeyConstant.pass_Word);
		if (TextUtils.isEmpty(passWord)) {
			return;
		}
		long time_login = SharedPreferencesTool.readLongValue(ctx,
				KeyConstant.time_login);
		if (time_login == -1L) {
			return;
		}
		long time = System.currentTimeMillis() - time_login;
		if (time > 86400000) {
			// 一天
			return;
		}
		username_edit.setText(userName);
		password_edit.setText(passWord);
		// 自动登录
		startLogin(userName, passWord);
	}

	private void startLogin(final String lsUsername, final String lsPassword) {
		Log.d(TAG, "startLogin");
		try {
			// 显示对话框
			dialogShow();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "login");
			jsonObject.put("username", lsUsername);
			jsonObject.put("password", lsPassword);
			Log.d(TAG, "jsonObject:" + jsonObject);

			// 向服务器请求信息
			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getLoginAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							onResponse_Result(response.toString(), lsUsername,
									lsPassword);

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

	private void onResponse_Result(String response, String userName,
			String password) {
		// 隐藏对话框
		dialogHide();
		try {
			JSONObject jsonObject = new JSONObject(response);
			int error_Code = jsonObject.getInt("errorcode");
			if (error_Code == 0) {
				// 返回UserId

				int user_Id = jsonObject.getInt("userid");
				String sex = jsonObject.getString("sex");
				// 存储信息
				SharedPreferencesTool.writeLongValue(ctx,
						KeyConstant.time_login, System.currentTimeMillis());
				SharedPreferencesTool.writeStringValue(ctx,
						KeyConstant.user_Name, userName);
				SharedPreferencesTool.writeStringValue(ctx,
						KeyConstant.pass_Word, password);

				baseInfo.userId = user_Id;
				baseInfo.sex = sex;

				// 跳转
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();

			} else if (error_Code == 5) {
				showToast("用户名或者密码错误");
			} else {
				showToast("error_Code：" + error_Code);
			}
		} catch (JSONException e) {
			showToast("服务器返回值出现错误");
			e.printStackTrace();
		}
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
				proDialog = android.app.ProgressDialog.show(ctx, "登录", "正在登录！");
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
}
