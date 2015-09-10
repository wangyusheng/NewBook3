package com.example.newbook4.book;

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
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyErrorHelper;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 图书交换的 详情 注意 无效的情况是哪些？
 * 
 * @author li.fx 2015年8月14日09:48:29
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class BookDetailActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "BookDetailActivity";
	private TextView actionbar_tv;
	private TextView content, content_infos, content_way;
	private BookBean bookBean;
	private int bookId;
	private ImageView imageView;

	private Button btn_ask, btn_comment, btn_collect, btn_report;
	private TextView tv_ask, tv_comment, tv_collect, tv_report;

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
		setContentView(R.layout.activity_bookdetail);
		Intent intent = getIntent();
		bookId = intent.getIntExtra("bookId", -1);
		if (bookId <= 0) {
			finish();
		} else {
			setupViewComponent();
			// 加载数据
			loadData(bookId);
		}

	}

	private void setupViewComponent() {

		// 设置加号不可见
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

		tv_ask = (TextView) findViewById(R.id.tv_ask);
		tv_comment = (TextView) findViewById(R.id.tv_comment);
		tv_collect = (TextView) findViewById(R.id.tv_collect);
		tv_report = (TextView) findViewById(R.id.tv_report);

		btn_ask = (Button) findViewById(R.id.btn_ask);
		btn_comment = (Button) findViewById(R.id.btn_comment);
		btn_collect = (Button) findViewById(R.id.btn_collect);
		btn_report = (Button) findViewById(R.id.btn_report);

		btn_ask.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下
					tv_ask.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 弹起
					tv_ask.setTextColor(getResources().getColor(
							R.color.textcolor_u));
				}

				return false;

			}

		});

		btn_comment.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下
					tv_comment.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 弹起
					tv_comment.setTextColor(getResources().getColor(
							R.color.textcolor_u));
				}

				return false;

			}

		});

		btn_collect.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 按下
					tv_collect.setTextColor(getResources().getColor(
							R.color.textcolor_p));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 弹起
					tv_collect.setTextColor(getResources().getColor(
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

		btn_ask.setOnClickListener(this);
		btn_comment.setOnClickListener(this);
		btn_collect.setOnClickListener(this);
		btn_report.setOnClickListener(this);

		tv_reload = (TextView) findViewById(R.id.tv_reload);
		btn_reload = (Button) findViewById(R.id.btn_reload);
		btn_reload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 加载数据
				loadData(bookId);
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
	}

	/**
	 * 加载成功
	 */
	private void setupLoadSuccess() {

		scrollView.setVisibility(View.VISIBLE);
		rl_reload.setVisibility(View.GONE);

		info_type = getResources().getStringArray(R.array.bad_info);

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

	/**
	 * 加载数据
	 */
	private void loadData(int bookId) {
		Log.d(TAG, "loadData");
		try {
			// 显示对话框
			dialogShow();
			bookBean = null;

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "retrievalBookSingle");
			jsonObject.put("bookId", bookId);

			Log.d(TAG, "jsonObject=" + jsonObject);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBookBusincesAddress(ctx), requestQueue,
					new ResponseCB() {

						@Override
						public void callBack(JSONObject response) {
							// 隐藏对话框
							dialogHide();
							dealResult(response);
						}

					}, new ErrorResponseCB() {

						@Override
						public void callBack(VolleyError error) {
							// 隐藏对话框
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
				isOk();

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

		// 评论数量
		// comment_num = (TextView) findViewById(R.id.comment_num);
		// comment_num.setTypeface(type);
		// comment_num.setText("123");
	}

	private void isOk() {
		if (bookBean.state != 0) {
			actionbar_tv.setText(bookBean.book_Name + "(人数已满)");
			showToast("不好意思，这本书籍预定人数已满！");
			bookBean = null;
			isAvailable = false;
		}
	}

	@Override
	public void onClick(View v) {
		if (bookBean == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_ask:
			if (bookBean.user_Id == baseInfo.userId) {
				showToast("该书是你自己发布的");
				return;
			}
			getBook();
			break;
		case R.id.btn_comment:
			showToast("评论功能");
			break;
		case R.id.btn_collect:
			showToast("收藏功能");
			break;
		case R.id.btn_report:
			showDialog(1);
			break;
		}

	}

	/**
	 * 举报
	 */
	private void reportBadInfo() {
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

	/**
	 * 获得图书
	 */
	protected void getBook() {
		Intent intent = new Intent();
		if ("交换".equals(bookBean.transcation)) {
			// 这个最复杂 先弄
			intent.setClass(ctx, TradeBookExchange.class);
			intent.putExtra("BookBean", bookBean);
			// if(bookBean==null){
			// showToast("bookBean==null");
			// }else{
			// showToast("bookBean!=null");
			// }
			startActivityForResult(intent, 0);

		} else if ("赠送".equals(bookBean.transcation)) {
			showToast("还未完成");
			// mTabIndicator.get(1).setIconAlpha(0f);
			// 选择联系地址
			// intent.setClass(ctx, TradeBookAddress.class);
			// intent.putExtra("BookBean", bookBean);
			// startActivity(intent);

		} else if ("出售".equals(bookBean.transcation)) {
			// 这个显示出售人的联系方式
			showToast("还未完成");
			// mTabIndicator.get(2).setIconAlpha(0f);
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
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
							// mTabIndicator.get(3).setIconAlpha(0);
							reportBadInfo();
						}

					});
			builder.setCancelable(false);

			builder.show();
		}
		return super.onCreateDialog(id);
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

}
