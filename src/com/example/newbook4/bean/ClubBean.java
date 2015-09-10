package com.example.newbook4.bean;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ClubBean {

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

	public String getRealAddress() {
		try {
			JSONObject jsonObject = new JSONObject(address);
			return jsonObject.getString("address1");
		} catch (JSONException e) {
			Log.d("ClubBean", e.toString());
		}
		return "";
		// {"contact":"联系人","address":"吉林省长春市市辖区详细地址","phone":"18940997430"}
	}
	
	
	public static Comparator<ClubBean> Comparator = new Comparator<ClubBean>() {
		public int compare(ClubBean s1, ClubBean s2) {
			return s2.club_id - s1.club_id;
		}
	};

}
