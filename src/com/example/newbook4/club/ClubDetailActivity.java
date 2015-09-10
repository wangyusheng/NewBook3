package com.example.newbook4.club;

import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.R;

import com.example.newbook4.bean.ClubBean;
import com.example.newbook4.book.BookDetailActivity;
import com.example.newbook4.book.TradeBookExchange;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("ClickableViewAccessibility")
public class ClubDetailActivity extends BaseActivity implements View.OnClickListener{
	private static final String TAG = "ClubDetailActivity";
	private TextView actionbar_tv;
	private TextView content_tv, content_way;
	private ClubBean clubBean;
	private int clubId;
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_club_detial);
		Intent intent = getIntent();
		clubId = intent.getIntExtra("bookId", -1);
		if (clubId <= 0) {
			finish();
		} else {
			setupViewComponent();
			// ��������
			loadData(clubId);
		}

	}

	private void setupViewComponent() {

		// ���üӺŲ��ɼ�
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);
		actionbar_tv = (TextView) findViewById(R.id.actionbar_tv);

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
					// ����
					tv_concern.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// ����
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
					// ����
					tv_enroll.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// ����
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
					// ����
					tv_report.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// ����
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
				// ��������
				loadData(clubId);
			}
		});

		btn_reload.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// ����
					tv_reload.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// ����
					tv_reload.setTextColor(getResources().getColor(
							R.color.textcolor_u));
				}

				return false;

			}

		});
	}

	/**
	 * ���سɹ�
	 */
	private void setupLoadSuccess() {

		scrollView.setVisibility(View.VISIBLE);
		rl_reload.setVisibility(View.GONE);

		info_type = getResources().getStringArray(R.array.bad_info);

		content_tv = (TextView) findViewById(R.id.content_tv);

		content_way = (TextView) findViewById(R.id.content_way);
		content_way.setTypeface(myApplication.popTypeface);

	}

	/**
	 * ����ʧ��
	 */
	private void setupLoadFail() {
		scrollView.setVisibility(View.GONE);
		rl_reload.setVisibility(View.VISIBLE);
	}

	/**
	 * ��������
	 */
	private void loadData(int bookId) {
		Log.d(TAG, "loadData");
		try {
			// ��ʾ�Ի���
			dialogShow();
			clubBean = null;

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "retrievalBookSingle");
			jsonObject.put("bookId", bookId);

			Log.d(TAG, "jsonObject=" + jsonObject);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBookBusincesAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							// ���ضԻ���
							dialogHide();
				//			dealResult(response);
						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							// ���ضԻ���
							dialogHide();
							showToast(VolleyErrorHelper.getMessage(error, ctx));
							setupLoadFail();
						}

					});

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
/*		protected void dealResult(JSONObject response) {
			try {
	
				int errorcode = response.getInt("errorcode");
				if (errorcode == 0) {
					JSONObject bookObject = response.getJSONObject("jsonObject");
					clubBean = new clubBean();
					clubBean.book_Id = bookObject.getInt("book_id");
					clubBean.user_Id = bookObject.getInt("user_id");
					clubBean.book_Name = bookObject.getString("book_name");
					clubBean.abstract_content = bookObject.getString("abstract");
					clubBean.time_Release = bookObject.getString("time_release");
					clubBean.transcation = bookObject.getString("transaction");
					clubBean.new_Old = bookObject.getString("new_old");
					clubBean.picture_Path = bookObject.getString("picture");
					clubBean.priority = bookObject.getInt("priority");
					clubBean.rating = bookObject.getInt("rating");
					String generation_time = bookObject
							.getString("generation_time");
					String[] temp = generation_time.split("-");
					clubBean.generation_time = temp[3] + ":" + temp[4] + ":"
							+ temp[5];
					clubBean.author_name = bookObject.getString("author_name");
					clubBean.interest = bookObject.getString("interest");
					clubBean.state = bookObject.getInt("state");
					// ���سɹ�
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
*/	
		/**
		 * ��ʾ���
		 */
/*		private void displayResult() {
			StringBuilder sb = new StringBuilder();
			sb.append("������ID��" + clubBean.user_Id + "\n");
			sb.append("����ʱ�䣺" + clubBean.generation_time + "\n");
			sb.append("�¾ɳ̶ȣ�" + clubBean.new_Old + "\n");
			sb.append("�鼮���" + clubBean.interest + "\n");
			sb.append("�������ޣ�" + clubBean.time_Release);
			content_infos.setText(sb.toString());
	
			content_way.append(clubBean.transcation);
			actionbar_tv.setText(clubBean.book_Name);
			content.setText(clubBean.abstract_content);
	
			ImageLoader.getInstance().displayImage(
					NetUtil.getBookUpLoadAddress(ctx) + clubBean.picture_Path,
					imageView);
	
			// ��������
			// comment_num = (TextView) findViewById(R.id.comment_num);
			// comment_num.setTypeface(type);
			// comment_num.setText("123");
		}
	
		private void isOk() {
			if (clubBean.state != 0) {
				actionbar_tv.setText(clubBean.book_Name + "(��������)");
				showToast("������˼���Ȿ�鼮Ԥ������������");
				clubBean = null;
				isAvailable = false;
			}
		}
	
		@Override
		public void onClick(View v) {
			if (clubBean == null) {
				return;
			}
			switch (v.getId()) {
			case R.id.btn_ask:
				if (clubBean.user_Id == baseInfo.userId) {
					showToast("���������Լ�������");
					return;
				}
				getBook();
				break;
			case R.id.btn_comment:
				showToast("���۹���");
				break;
			case R.id.btn_collect:
				showToast("�ղع���");
				break;
			case R.id.btn_report:
				showDialog(1);
				break;
			}
	
		}
*/
	/**
	 * �ٱ�
	 */
/*	private void reportBadInfo() {
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
*/
	/**
	 * ���ͼ��
	 */
/*	protected void getBook() {
		Intent intent = new Intent();
		if ("����".equals(clubBean.transcation)) {
			// ������ ��Ū
			intent.setClass(ctx, TradeBookExchange.class);
			intent.putExtra("clubBean", clubBean);
			// if(clubBean==null){
			// showToast("clubBean==null");
			// }else{
			// showToast("clubBean!=null");
			// }
			startActivityForResult(intent, 0);

		} else if ("����".equals(clubBean.transcation)) {
			showToast("��δ���");
			// mTabIndicator.get(1).setIconAlpha(0f);
			// ѡ����ϵ��ַ
			// intent.setClass(ctx, TradeBookAddress.class);
			// intent.putExtra("clubBean", clubBean);
			// startActivity(intent);

		} else if ("����".equals(clubBean.transcation)) {
			// �����ʾ�����˵���ϵ��ʽ
			showToast("��δ���");
			// mTabIndicator.get(2).setIconAlpha(0f);
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { // resultCodeΪ�ش��ı�ǣ�����B�лش�����RESULT_OK
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
					BookDetailActivity.this);
			builder.setIcon(R.drawable.report);
			builder.setTitle("�ٱ�");

			// ����һ������ѡ��������
			builder.setMultiChoiceItems(info_type, null,
					new DialogInterface.OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							info_checkSign[which] = isChecked;
						}
					});
			builder.setPositiveButton("ȷ��",
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
*/
	/**
	 * ��ʾ�Ի���
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
				msg.setText("���ڼ���");
				progressDialog.show();
			}

		});
	}

	/**
	 * ���ضԻ���
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
