package com.example.newbook4;

import com.example.newbook4.book.ReleaseBookActivity;
import com.example.newbook4.book.ReleaseBookSale;
import com.example.newbook4.club.ReleaseClubActivity;
import com.example.newbook4.info.ReleaseInfo_buyActivity;
import com.example.newbook4.info.ReleasePosterActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class NewListActivity extends Activity {
	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newlist);
		ctx = this;
		initViewComponent();

	}

	private void initViewComponent() {
		((Button) findViewById(R.id.btn_close))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.slide_up_in,
								R.anim.slide_down_out);
					}
				});

		((Button) findViewById(R.id.btn_club))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						
						
						Intent intent = new Intent();
						intent.setClass(ctx, ReleaseClubActivity.class);
						startActivity(intent);
						finish();

					}
				});

		((Button) findViewById(R.id.btn_poster))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ctx, ReleasePosterActivity.class);
						startActivity(intent);
						finish();
					}
				});

		((Button) findViewById(R.id.btn_info))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ctx, ReleaseInfo_buyActivity.class);
						startActivity(intent);
						finish();
					}
				});

		((Button) findViewById(R.id.btn_ex))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ctx, ReleaseBookActivity.class);
						intent.putExtra("type", 0);
						startActivity(intent);
						finish();

					}
				});

		((Button) findViewById(R.id.btn_sale))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ctx, ReleaseBookActivity.class);
						startActivity(intent);
						finish();
					}
				});

		((Button) findViewById(R.id.btn_give))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ctx, ReleaseBookActivity.class);
						intent.putExtra("type", 1);
						startActivity(intent);
						finish();

					}
				});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果按下的是返回键，并且没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			return false;
		}
		return false;
	}
}
