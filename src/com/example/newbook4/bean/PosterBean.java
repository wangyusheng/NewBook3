package com.example.newbook4.bean;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PosterBean {
	public int poster_id;

	public int user_id;

	public String topic;

	public String person;

	public String time;

	public String address;
	
	public int concern_num;

	public int accusation_num;

	public String generate_time;
	public PosterBean(){
		
	}
	public String getRealAddress() {
		try {
			JSONObject jsonObject = new JSONObject(address);
			return jsonObject.getString("address1");
		} catch (JSONException e) {
			Log.d("PosterBean", e.toString());
		}
		return "";
		// {"contact":"联系人","address":"吉林省长春市市辖区详细地址","phone":"18940997430"}
	}
	
	
	public static Comparator<PosterBean> Comparator = new Comparator<PosterBean>() {
		public int compare(PosterBean s1, PosterBean s2) {
			return s2.poster_id - s1.poster_id;
		}
	};
}
