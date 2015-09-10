package com.example.newbook4.me;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.R;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.bean.ExchangeBookOrderFinish;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BookGiveOrderFinish extends BaseActivity {
	private static final String TAG = "BookGiveOrderDetial";
	private int book_id;

	private String book_name;

	private LinearLayout content;

	private RelativeLayout rl_reload;

	private Dialog progressDialog;

	private TextView tv_reload;
	private Button btn_reload;

	private TextView tv_ordertitle, title1, time1, msg1, tv_address1, title2,
			time2, msg2, tv_address2;

	private ImageView icon1, icon2;
	private RatingBar ratingBar1, ratingBar2;

	private Button btn_feedback;
	private TextView tv_feedback;

	private ExchangeBookOrderFinish exchangeBookOrderFinish;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange_bookorderfinish);

		Intent intent = getIntent();
		book_id = intent.getIntExtra("book_id", -1);
		book_name = intent.getStringExtra("book_name");
		// 传入的值
		if (book_id > 0 && !TextUtils.isEmpty(book_name)) {
			setupViewComponent();
			loadMainData(book_id);
		} else {
			finish();
		}

	}

	private void setupViewComponent() {
		content = (LinearLayout) findViewById(R.id.content);
		rl_reload = (RelativeLayout) findViewById(R.id.rl_reload);
		content.setVisibility(View.GONE);
		rl_reload.setVisibility(View.VISIBLE);

		tv_reload = (TextView) findViewById(R.id.tv_reload);
		btn_reload = (Button) findViewById(R.id.btn_reload);
		btn_reload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 加载数据
				loadMainData(book_id);
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

		btn_feedback = (Button) findViewById(R.id.btn_feedback);

		tv_feedback = (TextView) findViewById(R.id.tv_feedback);

		btn_feedback.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下
					tv_feedback.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 弹起
					tv_feedback.setTextColor(getResources().getColor(
							R.color.textcolor_u));
				}
				return false;

			}

		});

		btn_feedback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//

				if (exchangeBookOrderFinish == null) {
					return;
				}
				showToast("反馈信息");

			}
		});

		tv_ordertitle = (TextView) findViewById(R.id.tv_ordertitle);

		title1 = (TextView) findViewById(R.id.title1);
		time1 = (TextView) findViewById(R.id.time1);
		msg1 = (TextView) findViewById(R.id.msg1);
		tv_address1 = (TextView) findViewById(R.id.tv_address1);

		title2 = (TextView) findViewById(R.id.title2);
		time2 = (TextView) findViewById(R.id.time2);
		msg2 = (TextView) findViewById(R.id.msg2);
		tv_address2 = (TextView) findViewById(R.id.tv_address2);

		icon1 = (ImageView) findViewById(R.id.icon1);
		icon2 = (ImageView) findViewById(R.id.icon2);

		ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
		ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);

		((TextView) findViewById(R.id.actionbar_tv)).setText(book_name);

	}

	/**
	 * 加载数据
	 * 
	 * @param book_id2
	 */
	protected void loadMainData(int book_Id) {
		try {
			dialogShow("正在加载");
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("action", "book_exchange_retrievalOrderFinish");
			jsonObject.put("book_id", book_Id);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBookExchangeAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							dialogHide();
							dealMainResult(response);

						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							dialogHide();
							showToast(VolleyErrorHelper.getMessage(error, ctx));
						}

					});

		} catch (JSONException e) {
			showToast("JSONException:" + e.toString());
		}
	}

	protected void dealMainResult(JSONObject response) {
		try {
			int errorcode = response.getInt("errorcode");
			if (errorcode == 0) {
				JSONObject resultObject = response
						.getJSONObject("resultObject");
				if (resultObject.length() > 0) {

					exchangeBookOrderFinish = new ExchangeBookOrderFinish();

					exchangeBookOrderFinish.finish_id = resultObject
							.getInt("finish_id");

					exchangeBookOrderFinish.exchange_id = resultObject
							.getInt("exchange_id");
					exchangeBookOrderFinish.release_user = resultObject
							.getInt("release_user");

					exchangeBookOrderFinish.obtain_user = resultObject
							.getInt("obtain_user");

					exchangeBookOrderFinish.release_book = resultObject
							.getInt("release_book");

					exchangeBookOrderFinish.obtain_book = resultObject
							.getInt("obtain_book");

					exchangeBookOrderFinish.release_msg = resultObject
							.getString("release_msg");

					exchangeBookOrderFinish.obtain_msg = resultObject
							.getString("obtain_msg");

					exchangeBookOrderFinish.generate_time = resultObject
							.getString("generate_time");

					exchangeBookOrderFinish.generate_time = resultObject
							.getString("generate_time");

					exchangeBookOrderFinish.rfinish_time = resultObject
							.getString("rfinish_time");

					exchangeBookOrderFinish.ofinish_time = resultObject
							.getString("ofinish_time");

					displayMainData();

				} else {
					showToast("未查到相关记录,2秒后自动返回");
					new CountDownTimer(2000, 1000) {
						public void onTick(long millisUntilFinished) {
						}

						public void onFinish() {
							finish();
						}
					}.start();

				}
			} else {
				showToast("errorcode=" + errorcode);
			}
		} catch (JSONException e) {
			showToast("JSONException:" + e.toString());
		}

	}

	/**
	 * 显示数据
	 */
	private void displayMainData() {
		if (exchangeBookOrderFinish == null) {
			return;
		}

		rl_reload.setVisibility(View.GONE);
		content.setVisibility(View.VISIBLE);

		tv_ordertitle.setText("订单编号:" + exchangeBookOrderFinish.exchange_id
				+ "\n下单时间:" + exchangeBookOrderFinish.generate_time
				+ "\n完成时间:比较一下");

		String tempTime = exchangeBookOrderFinish.ofinish_time;
		tv_address1.setText("收货状态：已收货 "
				+ myApplication.formatter.format(tempTime) + "\n"
				+ getAddress(exchangeBookOrderFinish.obtain_msg));

		tempTime = exchangeBookOrderFinish.rfinish_time;
		tv_address2.setText("收货状态：已收货 "
				+ myApplication.formatter.format(tempTime) + "\n"
				+ getAddress(exchangeBookOrderFinish.release_msg));

		loadBookData(exchangeBookOrderFinish.obtain_book, 0);
		loadBookData(exchangeBookOrderFinish.release_book, 1);
	}

	private void loadBookData(int book_id, final int i) {
		try {
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("action", "retrievalBookSingle");
			jsonObject.put("bookId", book_id);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBookBusincesAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							dealBookResult(response, i);

						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							showToast(VolleyErrorHelper.getMessage(error, ctx));
						}

					});

		} catch (JSONException e) {
			showToast("JSONException:" + e.toString());
		}
	}

	protected void dealBookResult(JSONObject response, int i) {
		try {
			int errorcode = response.getInt("errorcode");
			if (errorcode == 0) {
				JSONObject bookObject = response.getJSONObject("jsonObject");
				if (bookObject.length() > 0) {

					BookBean bookBean = new BookBean();
					bookBean.book_Id = bookObject.getInt("book_id");
					bookBean.user_Id = bookObject.getInt("user_id");
					bookBean.book_Name = bookObject.getString("book_name");
					bookBean.abstract_content = bookObject
							.getString("abstract");
					bookBean.time_Release = bookObject
							.getString("time_release");
					bookBean.transcation = bookObject.getString("transaction");
					bookBean.new_Old = bookObject.getString("new_old");
					bookBean.picture_Path = bookObject.getString("picture");
					bookBean.priority = bookObject.getInt("priority");
					bookBean.rating = bookObject.getInt("rating");
					String generation_time = bookObject
							.getString("generation_time");
					String[] temp = generation_time.split("-");
					bookBean.generation_time = temp[3] + ":" + temp[4] + ":"
							+ temp[5];
					bookBean.author_name = bookObject.getString("author_name");
					bookBean.interest = bookObject.getString("interest");

					displayBookResult(bookBean, i);
				}
			} else {
				showToast("errorcode=" + errorcode);
			}
		} catch (JSONException e) {
			showToast("JSONException:" + e.toString());
		}

	}

	/**
	 * 
	 * 
	 * 
	 * @param newBook
	 * @param i
	 */
	private void displayBookResult(BookBean newBook, int i) {
		if (i == 0) {
			title1.setText(newBook.book_Name);
			msg1.setText(newBook.interest + ":" + newBook.transcation);
			time1.setText(newBook.generation_time);
			ratingBar1.setRating(newBook.rating);
			String imgUrl = NetUtil.getBookUpLoadAddress(ctx)
					+ newBook.picture_Path;
			ImageLoader.getInstance().displayImage(imgUrl, icon1);
		} else {

			title2.setText(newBook.book_Name);
			msg2.setText(newBook.interest + ":" + newBook.transcation);
			time2.setText(newBook.generation_time);
			ratingBar2.setRating(newBook.rating);
			String imgUrl = NetUtil.getBookUpLoadAddress(ctx)
					+ newBook.picture_Path;
			ImageLoader.getInstance().displayImage(imgUrl, icon2);
		}

	}

	/**
	 * {"address1":"江西省萍乡市上栗县","address2":"金山镇张芳村","phone":"17722595175","name":
	 * "哩封信"}
	 * 
	 * @param json_Str
	 * @return
	 */
	private String getAddress(String json_Str) {
		StringBuilder sb = new StringBuilder();
		try {
			JSONObject jsonObject = new JSONObject(json_Str);
			String address1 = jsonObject.getString("address1");
			String address2 = jsonObject.getString("address2");
			String phone = jsonObject.getString("phone");
			String name = jsonObject.getString("name");

			sb.append(address1 + "(" + name + "收)\n");
			sb.append(address2 + phone);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 显示对话框
	 */
	private void dialogShow(final String text) {
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
				msg.setText(text);
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

}
