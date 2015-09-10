package com.example.newbook4.bean;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class InfoBean {
	public int info_id;

	public int user_id;

	public String bookname;

	public String price;

	//public String time;

	public String address;
	
	public int concern_num;

	public int accusation_num;

	public String generate_time;

	public InfoBean() {

	}

	public String getRealAddress() {
		try {
			JSONObject jsonObject = new JSONObject(address);
			return jsonObject.getString("address1");
		} catch (JSONException e) {
			Log.d("InfoBean", e.toString());
		}
		return "";
		// {"contact":"联系人","address":"吉林省长春市市辖区详细地址","phone":"18940997430"}
	}
	
	
	public static Comparator<InfoBean> Comparator = new Comparator<InfoBean>() {
		public int compare(InfoBean s1, InfoBean s2) {
			return s2.info_id - s1.info_id;
		}
	};
}
