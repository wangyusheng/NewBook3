package com.example.newbook4.book;

import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.newbook4.BaseActivity;
import com.example.newbook4.R;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BookSimpleDetialActivity extends BaseActivity {

	private static final String TAG = "BookSimpleDetialActivity";

	private int book_id;
	private ImageView imageView;
	private TextView actionbar_tv;
	private TextView content, content_infos, content_way;
	private BookBean bookBean;

	private Dialog progressDialog;

	private ScrollView scrollView;

	private RelativeLayout rl_reload;

	private TextView tv_reload;
	private Button btn_reload;

	private boolean isOk = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simplebookdetial);

		Intent intent = getIntent();
		book_id = intent.getIntExtra("book_id", -1);

		ctx = this;
		if (book_id > 0) {
			isOk = true;
		}
		// TODO
		if (isOk) {
			requestQueue = Volley.newRequestQueue(ctx);
			initViewComponent();
			loadData(book_id);
		} else {
			showToast("参数无效book_id:" + book_id);
			finish();
		}
	}

	private void initViewComponent() {
		// 设置加号不可见
		// 头部
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}

				});
		actionbar_tv = (TextView) findViewById(R.id.actionbar_tv);

		// 内容
		scrollView = (ScrollView) findViewById(R.id.sv_content);
		rl_reload = (RelativeLayout) findViewById(R.id.rl_reload);

		scrollView.setVisibility(View.GONE);
		rl_reload.setVisibility(View.VISIBLE);

		tv_reload = (TextView) findViewById(R.id.tv_reload);
		btn_reload = (Button) findViewById(R.id.btn_reload);
		btn_reload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 加载数据
				loadData(book_id);
			}
		});

		btn_reload.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
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

	}

	/**
	 * 加载成功
	 */
	private void setupLoadSuccess() {

		scrollView.setVisibility(View.VISIBLE);
		rl_reload.setVisibility(View.GONE);
		content = (TextView) findViewById(R.id.content_tv);
		imageView = (ImageView) findViewById(R.id.imageview);
		content_infos = (TextView) findViewById(R.id.content_infos);
		content_way = (TextView) findViewById(R.id.content_way);
		content_infos.setTypeface(myApplication.popTypeface);
		content_way.setTypeface(myApplication.popTypeface);
	}

	/**
	 * 加载失败
	 */
	private void setupLoadFail() {
		scrollView.setVisibility(View.GONE);
		rl_reload.setVisibility(View.VISIBLE);
	}

	private void loadData(int bookId) {

		try {
			// 显示对话框
			dialogShow();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "retrievalBookSingle");
			jsonObject.put("bookId", bookId);

			Log.d(TAG, "jsonObject:" + jsonObject.toString());

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBookBusincesAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							dialogHide();
							dealResult(response);
						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							dialogHide();
							showToast(VolleyErrorHelper.getMessage(error, ctx));
							setupLoadFail();
						}

					});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void dealResult(JSONObject response) {
		try {
			int errorcode = response.getInt("errorcode");
			if (errorcode == 0) {
				JSONObject bookObject = response.getJSONObject("jsonObject");
				bookBean = new BookBean();
				bookBean.book_Id = bookObject.getInt("book_id");
				bookBean.user_Id = bookObject.getInt("user_id");
				bookBean.book_Name = bookObject.getString("book_name");
				bookBean.abstract_content = bookObject.getString("abstract");
				bookBean.time_Release = bookObject.getString("time_release");
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
				bookBean.state = bookObject.getInt("state");
				// 加载成功
				setupLoadSuccess();
				displayResult();

			} else {
				showToast("errorcode=" + errorcode);
			}
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			showToast(e.toString());
		}

	}

	/**
	 * 显示结果
	 */
	private void displayResult() {
		StringBuilder sb = new StringBuilder();
		sb.append("发布者ID：" + bookBean.user_Id + "\n");
		sb.append("发布时间：" + bookBean.generation_time + "\n");
		sb.append("新旧程度：" + bookBean.new_Old + "\n");
		sb.append("书籍类别：" + bookBean.interest + "\n");
		sb.append("出版年限：" + bookBean.time_Release);
		content_infos.setText(sb.toString());

		content_way.append(bookBean.transcation);
		actionbar_tv.setText(bookBean.book_Name);
		content.setText(bookBean.abstract_content);

		ImageLoader.getInstance().displayImage(
				NetUtil.getBookUpLoadAddress(ctx) + bookBean.picture_Path,
				imageView);
	}

	/**
	 * 显示对话框
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
				msg.setText("正在加载");
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
