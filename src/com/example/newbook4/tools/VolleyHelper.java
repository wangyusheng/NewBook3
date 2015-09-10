package com.example.newbook4.tools;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

public class VolleyHelper {

	public interface ResponseCB {
		public void callBack(JSONObject response);

	}

	public interface ErrorResponseCB {
		public void callBack(VolleyError error);
	}

	public static void doPost_JSONObject(JSONObject jsonObject, String address,
			RequestQueue requestQueue, final ResponseCB responseCB,
			final ErrorResponseCB errorResponseCB) {

		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
				Method.POST, address, jsonObject,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(final JSONObject response) {
						if(responseCB!=null){
							responseCB.callBack(response);
						}

					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError error) {
						if(errorResponseCB!=null){
							errorResponseCB.callBack(error);
						}
					}

				}

		) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/json;charset=UTF-8");
				return headers;
			}

		};
		requestQueue.add(jsonRequest);
		Log.d("VolleyHelper", "jsonRequest=" + jsonRequest);

	}

}
