package com.example.newbook4.me;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.R;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.bean.BookExchangeInfoBean;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MeBookInfoDetialActivity extends BaseActivity {

	private static final String TAG = "MeBookInfoDetialActivity";

	private BookExchangeInfoBean bookExchangeInfoBean;

	private TextView content_infos, content_way, content_tv;

	private ImageView imageView;

	private String[] info_type;
	private boolean[] info_checkSign;

	private Button btn_exchange, btn_report;

	private TextView tv_report, tv_exchange;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me_bookexchangeinfodetial);
		Intent intent = getIntent();
		bookExchangeInfoBean = intent
				.getParcelableExtra("BookExchangeInfoBean");
		if (bookExchangeInfoBean == null) {
			finish();
		}

		ctx = this;
		requestQueue = Volley.newRequestQueue(ctx);
		info_type = getResources().getStringArray(R.array.bad_info);
		setViewComponent();
		// 查询数据
		retrievalBook(bookExchangeInfoBean.obtain_book);
	}

	private void setViewComponent() {
		// +号
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);
		// 标题
		((TextView) findViewById(R.id.actionbar_tv))
				.setText(bookExchangeInfoBean.obtain_bookname);
		// 返回按钮
		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

		content_infos = (TextView) findViewById(R.id.content_infos);
		content_way = (TextView) findViewById(R.id.content_way);
		content_tv = (TextView) findViewById(R.id.content_tv);
		imageView = (ImageView) findViewById(R.id.imageview);

		content_infos.setTypeface(myApplication.popTypeface);
		content_way.setTypeface(myApplication.popTypeface);

		content_way.setText(getStr(bookExchangeInfoBean.obtain_msg));

		btn_exchange = (Button) findViewById(R.id.btn_exchange);
		btn_report = (Button) findViewById(R.id.btn_report);

		tv_report = (TextView) findViewById(R.id.tv_report);
		tv_exchange = (TextView) findViewById(R.id.tv_exchange);

		btn_exchange.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("BookExchangeInfoBean", bookExchangeInfoBean);
				intent.setClass(ctx, MeBookExhangeChooseAddress.class);
				startActivityForResult(intent, 1);
			}
		});

		btn_report.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(1);
			}
		});

		
		btn_exchange.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下
					tv_exchange.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 弹起
					tv_exchange.setTextColor(getResources().getColor(
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
	}

	/**
	 * {"address1":"江西省萍乡市上栗县","address2":"金山镇张芳村","phone":"17722595175","name":
	 * "哩封信"}
	 * 
	 * @param json_Str
	 * @return
	 */
	private String getStr(String json_Str) {
		StringBuilder sb = new StringBuilder();
		try {
			JSONObject jsonObject = new JSONObject(json_Str);
			String address1 = jsonObject.getString("address1");
			String address2 = jsonObject.getString("address2");
			String phone = jsonObject.getString("phone");
			String name = jsonObject.getString("name");
			// sb.append(address1 + "(" + name + "收)\n");
			// sb.append(address2 + phone);

			sb.append("对方所在区域：" + address1);
			// sb.append("联系人:" + name + "\n");
			// sb.append("联系电话:" + phone);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
			break;

		}
	}

	/**
	 * 查找书籍
	 * 
	 * @param sign
	 * @param bookId
	 * @param popTypeface
	 */
	private void retrievalBook(final int bookId) {
		Log.d(TAG, "bookId=" + bookId);
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "retrievalBookSingle");
			jsonObject.put("bookId", bookId);

			Log.d(TAG, "jsonObject=" + jsonObject);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBookBusincesAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							retrievaleBookComped(response);
						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							showToast(VolleyErrorHelper.getMessage(error, ctx));
						}
					});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void retrievaleBookComped(JSONObject jsonObject) {
		try {
			int error_Code = jsonObject.getInt("errorcode");
			if (error_Code == 0) {
				JSONObject jsonResult = jsonObject.getJSONObject("jsonObject");
				BookBean bookBean = new BookBean();
				bookBean.book_Id = jsonResult.getInt("book_id");
				bookBean.user_Id = jsonResult.getInt("user_id");
				bookBean.book_Name = jsonResult.getString("book_name");
				bookBean.abstract_content = jsonResult.getString("abstract");
				bookBean.time_Release = jsonResult.getString("time_release");
				bookBean.transcation = jsonResult.getString("transaction");
				bookBean.new_Old = jsonResult.getString("new_old");
				bookBean.picture_Path = jsonResult.getString("picture");
				bookBean.priority = jsonResult.getInt("priority");
				bookBean.rating = jsonResult.getInt("rating");
				String generation_time = jsonResult
						.getString("generation_time");
				String[] temp = generation_time.split("-");
				bookBean.generation_time = temp[3] + ":" + temp[4] + ":"
						+ temp[5];
				bookBean.author_name = jsonResult.getString("author_name");
				bookBean.interest = jsonResult.getString("interest");

				// 设置信息
				ImageLoader.getInstance().displayImage(
						NetUtil.getBookUpLoadAddress(ctx)
								+ bookBean.picture_Path, imageView);

				StringBuilder sb = new StringBuilder();
				sb.append("发布者ID：" + bookBean.user_Id + "\n");
				sb.append("发布时间：" + bookBean.generation_time + "\n");
				sb.append("新旧程度：" + bookBean.new_Old + "\n");
				sb.append("书籍类别：" + bookBean.interest + "\n");
				sb.append("出版年限：" + bookBean.time_Release);
				content_infos.setText(sb.toString());

				content_tv.setText(bookBean.abstract_content);

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
			int len = info_type.length;
			info_checkSign = new boolean[len];
			for (int i = 0; i < len; i++) {
				info_checkSign[i] = false;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
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
							reportBadInfo();
						}

					});
			builder.setCancelable(false);

			builder.show();
		}
		return super.onCreateDialog(id);
	}

	protected void reportBadInfo() {
		int len = info_type.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			if (info_checkSign[i]) {
				sb.append(info_type[i] + "\t");
			}
		}
		//
		String result = sb.toString();
		if (TextUtils.isEmpty(result)) {
			return;
		}
		showToast(result);
	}

}
