package com.example.newbook4.tools;

import android.text.TextUtils;

public class Tools {

	public static boolean strIsOk(String str) {
		if (TextUtils.isEmpty(str)) {
			return false;
		}
		str = str.trim();
		if ("".equals(str)) {
			return false;
		}
		return true;
	}

}
