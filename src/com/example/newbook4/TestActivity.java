package com.example.newbook4;

import com.example.newbook4.tools.NotificationsHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newregister);

		//initViewComponent();

	}

//	private void initViewComponent() {
//		((Button) findViewById(R.id.btn))
//				.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
////						NotificationsHelper.getInstance()
////								.showIntentActivityNotify("关注通知","有人对你的书感兴趣");
//					}
//				});
//
//	}
}
