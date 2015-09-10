package com.example.newbook4.book;


import com.example.newbook4.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class ReleaseBookSale extends Activity {
	private Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.releasebooksale);
		ctx = this;

	}
}
