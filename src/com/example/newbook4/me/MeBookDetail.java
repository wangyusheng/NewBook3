package com.example.newbook4.me;

import com.example.newbook4.BaseInfo;
import com.example.newbook4.MyApplication;
import com.example.newbook4.R;
import com.example.newbook4.bean.BookBean;
import com.example.newbook4.tools.Configuration;
import com.example.newbook4.tools.NetUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MeBookDetail extends Activity {
	private static final String TAG = "MeBookDetail";
	private Context ctx;
	private TextView actionbar_tv;
	private TextView content, content_infos, content_way;
	private BookBean bookBean;
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mebookdetail);
		Intent intent = getIntent();
		Object object = intent.getParcelableExtra("BookBean");
		if (object instanceof BookBean) {
			bookBean = (BookBean) object;
			if (bookBean == null) {
				finish();
			}
		} else {
			finish();
		}
		ctx = this;

		setupViewComponent();

	}

	private void setupViewComponent() {
		// 设置加号不可见
		((Button) findViewById(R.id.actionbar_add)).setVisibility(View.GONE);
		// 设置头部颜色
		((RelativeLayout) findViewById(R.id.action_bar))
				.setBackgroundColor(getResources().getColor(
						Configuration.BOOK_COLOR));
		// 设置底部颜色
		// ((LinearLayout) findViewById(R.id.detail_foot))
		// .setBackgroundColor(getResources().getColor(
		// Configuration.BOOK_COLOR));
		Typeface type = Typeface.createFromAsset(getAssets(), "fonts/pop.ttf");

		actionbar_tv = (TextView) findViewById(R.id.actionbar_tv);
		content = (TextView) findViewById(R.id.content_tv);

		((Button) findViewById(R.id.actionbar_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}

				});
		imageView = (ImageView) findViewById(R.id.imageview);
		// comment_num = (TextView) findViewById(R.id.comment_num);
		// comment_num.setTypeface(type);
		// comment_num.setText("123");

		actionbar_tv.setText(bookBean.book_Name);
		content.setText(bookBean.abstract_content);

		ImageLoader.getInstance().displayImage(
				NetUtil.getBookUpLoadAddress(ctx) + bookBean.picture_Path,
				imageView);

		content_infos = (TextView) findViewById(R.id.content_infos);
		content_way = (TextView) findViewById(R.id.content_way);

		content_infos.setTypeface(type);
		StringBuilder sb = new StringBuilder();
		sb.append("发布者ID：" + bookBean.user_Id + "\n");
		sb.append("发布时间：" + bookBean.generation_time + "\n");
		sb.append("新旧程度：" + bookBean.new_Old + "\n");
		sb.append("书籍类别：" + bookBean.interest + "\n");
		sb.append("出版年限：" + bookBean.time_Release);
		content_infos.setText(sb.toString());

		content_way.setTypeface(type);
		content_way.append(bookBean.transcation);

	}

	public void showToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

}
