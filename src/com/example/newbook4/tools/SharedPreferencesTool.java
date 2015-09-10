package com.example.newbook4.tools;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesTool {

	public static String readStringValue(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences("data",
				Context.MODE_PRIVATE);
		return pref.getString(key, null);
	}

	public static void writeStringValue(Context context, String key,
			String value) {
		SharedPreferences.Editor editor = context.getSharedPreferences("data",
				Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static long readLongValue(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences("data",
				Context.MODE_PRIVATE);
		return pref.getLong(key, -1);
	}

	public static void writeLongValue(Context context, String key, long value) {
		SharedPreferences.Editor editor = context.getSharedPreferences("data",
				Context.MODE_PRIVATE).edit();
		editor.putLong(key, value);
		editor.commit();
	}
}
