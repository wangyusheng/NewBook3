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
 * ������
 * 
 * @author Li.fx 2015��8��14��08:39:55
 *
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	private TextView register_link, register_tv;
	private Button login_button;
	private EditText username_edit, password_edit;
	private ProgressDialog proDialog;

	public static final int LOGIN_SUCCESS = 0; // ��¼�ɹ�
	public static final int LOGIN_FAILED = LOGIN_SUCCESS + 1; // ��¼ʧ��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// ��ʼ���ؼ�
		initViewComponent();
		// Ĭ�ϵ�½
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
			// ע��
			@Override
			public void onClick(View v) {
				String lsUsername = username_edit.getText().toString();
				if (TextUtils.isEmpty(lsUsername)) {
					showToast("�û�������Ϊ��");
					return;
				}
				String lsPassword = password_edit.getText().toString();
				if (TextUtils.isEmpty(lsPassword)) {
					showToast("���벻��Ϊ��");
					return;
				}
				startLogin(lsUsername, CryptionTools.getInstance()
						.mySha1_decrypt(lsPassword));
			}
		});
		register_tv = (TextView) findViewById(R.id.register_tv);
		register_tv.setTypeface(myApplication.popTypeface);
		register_tv.setText("��ɽ��·����Ϊ����");
	}

	/**
	 * �������ļ��� ��ȡ��¼
	 */
	private void loginDefault() {
		// ��ȡĬ��ֵ
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
			// һ��
			return;
		}
		username_edit.setText(userName);
		password_edit.setText(passWord);
		// �Զ���¼
		startLogin(userName, passWord);
	}

	private void startLogin(final String lsUsername, final String lsPassword) {
		Log.d(TAG, "startLogin");
		try {
			// ��ʾ�Ի���
			dialogShow();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "login");
			jsonObject.put("username", lsUsername);
			jsonObject.put("password", lsPassword);
			Log.d(TAG, "jsonObject:" + jsonObject);

			// �������������Ϣ
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
		// ���ضԻ���
		dialogHide();
		showToast(VolleyErrorHelper.getMessage(error, ctx));
	}

	private void onResponse_Result(String response, String userName,
			String password) {
		// ���ضԻ���
		dialogHide();
		try {
			JSONObject jsonObject = new JSONObject(response);
			int error_Code = jsonObject.getInt("errorcode");
			if (error_Code == 0) {
				// ����UserId

				int user_Id = jsonObject.getInt("userid");
				String sex = jsonObject.getString("sex");
				// �洢��Ϣ
				SharedPreferencesTool.writeLongValue(ctx,
						KeyConstant.time_login, System.currentTimeMillis());
				SharedPreferencesTool.writeStringValue(ctx,
						KeyConstant.user_Name, userName);
				SharedPreferencesTool.writeStringValue(ctx,
						KeyConstant.pass_Word, password);

				baseInfo.userId = user_Id;
				baseInfo.sex = sex;

				// ��ת
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();

			} else if (error_Code == 5) {
				showToast("�û��������������");
			} else {
				showToast("error_Code��" + error_Code);
			}
		} catch (JSONException e) {
			showToast("����������ֵ���ִ���");
			e.printStackTrace();
		}
	}

	/**
	 * ��ʾ�Ի���
	 */
	private void dialogShow() {

		runOnUiThread(new Runnable() {
			public void run() {
				if (proDialog != null) {
					proDialog.dismiss();
					proDialog = null;
				}
				proDialog = android.app.ProgressDialog.show(ctx, "��¼", "���ڵ�¼��");
			}

		});
	}

	/**
	 * ���ضԻ���
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
