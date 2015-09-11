package com.example.newbook4.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.newbook4.MyApplication;

import android.text.TextUtils;
import android.util.Log;

public class ClubBean {

	private static final String TAG = "ClubBean";

	public int club_id;

	public int user_id;

	public String topic;

	public String recommend_book;

	public String time;

	public String address;

	public int enroll_num;
	public int concern_num;

	public int accusation_num;

	public String generate_time;

	public ClubBean() {

	}

	public String getTime() {
		Log.d(TAG, "generate_time=" + generate_time);
		if (TextUtils.isEmpty(generate_time)) {
			return null;
		}
		long temp_Long = 0l;
		try {
			temp_Long = Long.parseLong(generate_time);
		} catch (Exception e) {
			Log.e(TAG, "getTime");
		}
		return MyApplication.formatter.format(temp_Long);
	}

	public String getAddress1() {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(address);
			return jsonObject.getString("address1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getAddress2() {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(address);
			return jsonObject.getString("address2");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getContact() {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(address);
			return jsonObject.getString("contact");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getPhone() {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(address);
			return jsonObject.getString("phone");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String> getAllBooks() {
		Log.d(TAG, "getAllBooks:"+recommend_book);
		ArrayList<String> books = new ArrayList<String>();
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(recommend_book);
			for (int i = 0, len = jsonArray.length(); i < len; i++) {
				books.add(jsonArray.getString(i));
			}
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
		}
		return books;
	}

	// public String getRealAddress() {
	// try {
	// JSONObject jsonObject = new JSONObject(address);
	// return jsonObject.getString("address1");
	// } catch (JSONException e) {
	// Log.d("ClubBean", e.toString());
	// }
	// return "";
	// // {"contact":"联系人","address":"吉林省长春市市辖区详细地址","phone":"18940997430"}
	// }

	public static Comparator<ClubBean> Comparator = new Comparator<ClubBean>() {
		public int compare(ClubBean s1, ClubBean s2) {
			return s2.club_id - s1.club_id;
		}
	};

}
