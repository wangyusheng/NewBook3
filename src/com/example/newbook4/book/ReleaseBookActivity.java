package com.example.newbook4.book;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.newbook4.BaseInfo;
import com.example.newbook4.MyApplication;
import com.example.newbook4.R;
import com.example.newbook4.tools.Configuration;
import com.example.newbook4.tools.ImageTool;
import com.example.newbook4.tools.KeyConstant;
import com.example.newbook4.tools.PictureUtil;
import com.example.newbook4.tools.SharedPreferencesTool;
import com.example.newbook4.tools.VolleyErrorHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ReleaseBookActivity extends Activity {

	private Context ctx;
	// 方式、标题
	private TextView tv_stype, actionbar_tv;

	private int type;

	private static final String[] TYPE = { "交换", "赠送" };

	private static final String TAG = "ReleaseBookActivity";
	private ImageView img_content;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private File pictureFile;
	private Spinner spinner_NewOld, spinner_Interest, spinner_Times;
	private String newOld, interest, times;
	private EditText et_BookName, et_AuthorName, et_Content;
	private RatingBar ratingBar;
	private Button btn_OK;

	private ProgressDialog proDialog;

	private BaseInfo baseInfo;

	private RequestQueue requestQueue = null;

	private final int SHOW_TOAST = 0;
	private final int DIALOG_DIMSS = SHOW_TOAST + 1;
	private final int DATA_COMPONENT = DIALOG_DIMSS + 1;
	private final int RB_IMAGE_REQUEST_CODE = DATA_COMPONENT + 1;
	private final int RB_CAMERA_REQUEST_CODE = RB_IMAGE_REQUEST_CODE + 1;

	private boolean sign = false;

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
				btn_OK.setEnabled(true);
				break;

			case DATA_COMPONENT:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_book_layout);
		ctx = this;

		Intent intent = getIntent();
		type = intent.getIntExtra("type", -1);
		if (type == -1) {
			finish();
		}
		if (type != 0 && type != 1) {
			finish();
		}
		initViews();
		requestQueue = Volley.newRequestQueue(ctx);

	}

	private void initViews() {
		actionbar_tv = (TextView) findViewById(R.id.actionbar_tv);
		tv_stype = (TextView) findViewById(R.id.tv_stype);
		actionbar_tv.setText(TYPE[type]);
		tv_stype.setText(TYPE[type]);
		tv_stype.setTextColor(getResources().getColor(Configuration.BOOK_COLOR));
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (sign) {
							setResult(RESULT_OK);

						} else {
							setResult(RESULT_CANCELED);
						}
						finish();
					}

				});

		baseInfo = ((MyApplication) getApplication()).baseInfo;

		img_content = (ImageView) findViewById(R.id.img_content);
		Log.d(TAG, img_content.getWidth() + "," + img_content.getHeight());
		img_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(ctx)
						.setTitle("设置图片")
						.setItems(items, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:

									Intent intent = new Intent();
									intent.setType("image/*");
									intent.setAction(Intent.ACTION_PICK);
									startActivityForResult(intent,
											RB_IMAGE_REQUEST_CODE);
									break;
								case 1:

									// 启动拍照,并保存到临时文件
									pictureFile = new File(
											getExternalFilesDir(Environment.DIRECTORY_PICTURES),
											"book.jpg");

									Intent mIntent = new Intent();
									mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
									mIntent.putExtra(MediaStore.EXTRA_OUTPUT,
											Uri.fromFile(pictureFile));
									mIntent.putExtra(
											MediaStore.Images.Media.ORIENTATION,
											0);
									startActivityForResult(mIntent,
											RB_CAMERA_REQUEST_CODE);
									break;
								}
							}
						})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();
			}

		});

		spinner_NewOld = (Spinner) findViewById(R.id.spinner_newold);
		spinner_NewOld.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				newOld = getResources().getStringArray(R.array.new_old)[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
		spinner_Interest = (Spinner) findViewById(R.id.spinner_interest);
		spinner_Interest
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						interest = getResources().getStringArray(
								R.array.interests)[position];
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

		spinner_Times = (Spinner) findViewById(R.id.spinner_times);
		spinner_Times.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				times = getResources().getStringArray(R.array.times)[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		et_BookName = (EditText) findViewById(R.id.et_bookname);
		et_AuthorName = (EditText) findViewById(R.id.et_authorname);
		et_Content = (EditText) findViewById(R.id.et_content);

		ratingBar = (RatingBar) findViewById(R.id.room_ratingbar);

		btn_OK = (Button) findViewById(R.id.btn_ok);
		btn_OK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 发布
				release();
			}

		});

	}

	public void setData(int requestCode, Intent data) {

		String fileSrc = null;
		if (requestCode == RB_IMAGE_REQUEST_CODE) {
			if ("file".equals(data.getData().getScheme())) {
				// 有些低版本机型返回的Uri模式为file
				fileSrc = data.getData().getPath();
			} else {
				// Uri模型为content
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(data.getData(),
						proj, null, null, null);
				cursor.moveToFirst();
				int idx = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				fileSrc = cursor.getString(idx);
				cursor.close();
			}
		} else if (requestCode == RB_CAMERA_REQUEST_CODE) {
			fileSrc = pictureFile.getAbsolutePath();
		}
		pictureFile = new File(fileSrc);
		Log.d(TAG, "fileSrc=" + fileSrc);
		// 获取图片的宽和高
		Options options = new Options();
		options.inJustDecodeBounds = true;
		Bitmap img = BitmapFactory.decodeFile(fileSrc, options);

		// 压缩图片
		options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
				(double) options.outWidth / 1024f,
				(double) options.outHeight / 1024f)));
		options.inJustDecodeBounds = false;
		img = BitmapFactory.decodeFile(fileSrc, options);

		// 部分手机会对图片做旋转，这里检测旋转角度
		int degree = ImageTool.readPictureDegree(fileSrc);
		if (degree != 0) {
			// 把图片旋转为正的方向
			img = ImageTool.rotateImage(degree, img);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// 可根据流量及网络状况对图片进行压缩
		img.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		// byte[] imgData = baos.toByteArray();

		img_content.setImageBitmap(img);

		Log.d(TAG, img_content.getWidth() + "," + img_content.getHeight());
	}

	/**
	 * 发布
	 */
	private void release() {
		final long startTime = System.currentTimeMillis();
		// 作者名称
		String authorName = et_AuthorName.getText().toString();
		if (TextUtils.isEmpty(authorName)) {
			showToast("请填写作者名称");
			return;
		}
		String bookName = et_BookName.getText().toString();
		if (TextUtils.isEmpty(bookName)) {
			showToast("请填写书名");
			return;
		}
		String content = et_Content.getText().toString();
		if (TextUtils.isEmpty(content)) {
			showToast("请填写书籍的主要内容");
			return;
		}
		if (pictureFile == null || !pictureFile.exists()) {
			showToast("请为书籍拍一张照吧！");
			return;
		}
		int rating = (int) ratingBar.getRating();
		// 提交
		proDialog = android.app.ProgressDialog.show(ctx, "发布", "正在发布图书！");

		btn_OK.setEnabled(false);

		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "releaseBook");
		map.put("userId", baseInfo.userId + "");
		map.put("authorName", authorName);
		map.put("bookName", bookName);
		map.put("content", content);
		map.put("rating", rating + "");
		map.put("interest", interest);
		map.put("newOld", newOld);
		map.put("transaction", TYPE[type]);
		map.put("times", times);
		map.put("fileName", pictureFile.getName());
		map.put("fileContent",
				PictureUtil.bitmapToString(pictureFile.getAbsolutePath()));

		JSONObject jsonObject = new JSONObject(map);
		Log.d(TAG, jsonObject.toString());
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
				Method.POST, getAddress(), jsonObject,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(final JSONObject response) {

						long tempTime = 1000 + startTime
								- System.currentTimeMillis();
						if (tempTime > 0) {
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									handler.sendEmptyMessage(DIALOG_DIMSS);
									releaseBook_Result(response);
								}
							}, tempTime);
						} else {
							handler.sendEmptyMessage(DIALOG_DIMSS);
							releaseBook_Result(response);
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError error) {
						long tempTime = 1000 + startTime
								- System.currentTimeMillis();
						if (tempTime > 0) {
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									handler.sendEmptyMessage(DIALOG_DIMSS);
									showToast(VolleyErrorHelper.getMessage(
											error, ctx));
								}
							}, tempTime);
						} else {
							handler.sendEmptyMessage(DIALOG_DIMSS);
							showToast(VolleyErrorHelper.getMessage(error, ctx));
						}

					}

				}

		) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/json;charset=UTF-8");

				return headers;
			}

		};
		requestQueue.add(jsonRequest);
		Log.d(TAG, "jsonRequest=" + jsonRequest);
	}

	private String getAddress() {
		String ip_address = SharedPreferencesTool.readStringValue(ctx,
				KeyConstant.ip_Address);
		return "http://" + ip_address + ":8080/Book//bookBusiness";
	}

	protected void releaseBook_Result(JSONObject response) {
		Log.d(TAG, response.toString());
		try {
			int error_Code = response.getInt("errorcode");
			if (error_Code == 0) {
				showToast("发布成功");
				reset();

				sign = true;

				// Intent intent = new Intent();
				// intent.putExtra("result", "这是处理结果");
				// setResult(20, intent);// 设置返回数据
				// finish();// 关闭activity

			} else {
				showToast("发布失败:" + error_Code);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			showToast("服务器返回值出现错误");
		}

	}

	/**
	 * 重置控件
	 */
	protected void reset() {
		et_BookName.setText("");
		et_AuthorName.setText("");
		et_Content.setText("");

		pictureFile = null;
		img_content.setImageDrawable(getResources().getDrawable(
				R.drawable.no_photo));
	}

	private void showToast(String str) {
		Message msg = handler.obtainMessage();
		msg.what = SHOW_TOAST;
		msg.obj = str;
		handler.sendMessage(msg);
	}

	// private void display_Error(String result) {
	// handler.sendEmptyMessage(DIALOG_DIMSS);
	// showToast(result);
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			Log.d(TAG, "!RESULT_OK");
			return;
		}
		Log.d(TAG, "requestCode=" + requestCode);
		switch (requestCode) {
		case RB_IMAGE_REQUEST_CODE:
			setData(requestCode, data);
			break;
		case RB_CAMERA_REQUEST_CODE:
			setData(requestCode, data);
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (sign) {
				this.setResult(RESULT_OK);

			} else {
				this.setResult(RESULT_CANCELED);
			}
			this.finish();

		}
		return super.onKeyDown(keyCode, event);

	}
}
