package com.example.newbook4.tools;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.newbook4.book.BookSimpleDetialActivity;
import com.example.newbook4.me.MeExBookInfosActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageIntentionHelper {

	private static final String TAG = "MessageIntentionHelper";

	private static MessageIntentionHelper instance;

	private static Context ctx;

	public static void setContext(Context context) {
		ctx = context;
	}

	public static MessageIntentionHelper getInstance() {
		if (instance == null) {
			instance = new MessageIntentionHelper();
		}
		return instance;
	}

	public void dealTxtMessage(String content) {
		try {
			JSONObject jsonObject = new JSONObject(content);
			Log.d(TAG, "dealTxtMessage-JSONObject:" + jsonObject.toString());
			String action = jsonObject.getString("action");
			if ("exchange_info_reserved".equals(action)) {
				// ԤԼ֪ͨ
				//�鿴ԤԼ��Ϣ
				String book_name = jsonObject.getString("book_name");
				int book_id = jsonObject.getInt("book_id");
				Intent resultIntent = new Intent(ctx, MeExBookInfosActivity.class);
				resultIntent.putExtra("book_id", book_id);
				resultIntent.putExtra("book_name", book_name);
				NotificationsHelper
						.getInstance()
						.showIntentActivityNotify("ԤԼ֪ͨ",
								"����ԤԼ�������-exchange_info_reserved", resultIntent);
			} else if ("exchange_info_fail".equals(action)) {
				// ԤԼʧ��֪ͨ
				// �鿴ͼ������
				int book_id = jsonObject.getInt("book_id");
				Intent resultIntent = new Intent(ctx,
						BookSimpleDetialActivity.class);
				resultIntent.putExtra("book_id", book_id);
				NotificationsHelper.getInstance().showIntentActivityNotify(
						"ԤԼʧ��", "������˼���Է�δͬ�����ԤԼ- exchange_info_fail",
						resultIntent);

			} else if ("exchange_info_succeed".equals(action)) {
				// ԤԼ�ɹ�֪ͨ
				//�鿴ͼ������
				int book_id = jsonObject.getInt("book_id");
				Intent resultIntent = new Intent(ctx,
						BookSimpleDetialActivity.class);
				resultIntent.putExtra("book_id", book_id);
				NotificationsHelper
						.getInstance()
						.showIntentActivityNotify("ԤԼ�ɹ�",
								"��ϲ����ɹ�ԤԼ��-exchange_info_succeed", resultIntent);

			}

		} catch (JSONException e) {
			e.printStackTrace();
			Log.d(TAG, "dealTxtMessage-JSONException:" + e.toString());
		}

	}
}
