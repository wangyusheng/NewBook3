package com.example.newbook4.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class AddressBean {

	public int userId;

	public int addressId;

	public boolean flag;

	public String address1;

	public String address2;

	public String contact;

	public String phone;

	public String getSimpleStr() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("address1", address1);
			jsonObject.put("address2", address2);
			jsonObject.put("phone", phone);
			jsonObject.put("name", contact);
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
}
