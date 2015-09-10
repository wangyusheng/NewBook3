package com.example.newbook4;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class BaseActivity extends Activity {
	protected Context ctx;

	protected RequestQueue requestQueue = null;

	protected MyApplication myApplication;

	protected BaseInfo baseInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		requestQueue = Volley.newRequestQueue(ctx);
		myApplication = (MyApplication) getApplication();
		baseInfo = myApplication.baseInfo;

	}

	
	protected void showToast(final String strToast) {

		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(ctx, strToast, Toast.LENGTH_SHORT).show();
			}

		});
	}
}
