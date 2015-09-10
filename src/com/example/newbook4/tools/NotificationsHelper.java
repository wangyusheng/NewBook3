package com.example.newbook4.tools;

import com.example.newbook4.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationsHelper {

	private static NotificationsHelper instance;

	public static NotificationsHelper getInstance() {
		if (instance == null) {
			instance = new NotificationsHelper();
		}
		return instance;
	}

	private NotificationsHelper() {
		// ��ʼ��
		initService();
		initNotify();
	}

	/** Notification������ */
	private NotificationCompat.Builder mBuilder;
	/** Notification��ID */
	private final int notifyId = 100;

	/** Notification���� */
	private NotificationManager mNotificationManager;

	private static Context ctx;

	public static void setContext(Context context) {
		ctx = context;
	}

	/** ��ʼ��֪ͨ�� */
	private void initNotify() {
		mBuilder = new NotificationCompat.Builder(ctx);
		mBuilder.setContentTitle("���Ա���")
				.setContentText("��������")
				.setContentIntent(
						getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
				// .setNumber(number)//��ʾ����
				.setTicker("����֪ͨ����")// ֪ͨ�״γ�����֪ͨ��������������Ч����
				.setWhen(System.currentTimeMillis())// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ
				.setPriority(Notification.DEFAULT_ALL)// ���ø�֪ͨ���ȼ�
				// .setAutoCancel(true)//���������־���û��������Ϳ�����֪ͨ���Զ�ȡ��
				.setOngoing(false)// ture��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)
				.setDefaults(Notification.DEFAULT_VIBRATE)// ��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ�������ϣ�
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND ������� //
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.book_launcher);
	}

	/** ��ʾ֪ͨ�������ת��ָ��Activity */
	public void showIntentActivityNotify(String title,String content,Intent resultIntent) {
		// Notification.FLAG_ONGOING_EVENT --���ó�פ
		// Flag;Notification.FLAG_AUTO_CANCEL ֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		// //��֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
		mBuilder.setAutoCancel(true)
				// �������֪ͨ����ʧ
				.setContentTitle(title).setContentText(content)
				.setDefaults(Notification.DEFAULT_SOUND)
				.setVibrate(new long[] { 0, 300, 500, 700 }).setTicker("");
		// �������ͼACTION����ת��Intent
		//Intent resultIntent = new Intent(ctx, MainActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		mNotificationManager.notify(notifyId, mBuilder.build());
	}

	/**
	 * ��ʼ��Ҫ�õ���ϵͳ����
	 */
	private void initService() {
		mNotificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	/**
	 * �����ǰ������֪ͨ��
	 */
	public void clearNotify(int notifyId) {
		mNotificationManager.cancel(notifyId);// ɾ��һ���ض���֪ͨID��Ӧ��֪ͨ
		// mNotification.cancel(getResources().getString(R.string.app_name));
	}

	/**
	 * �������֪ͨ��
	 * */
	public void clearAllNotify() {
		mNotificationManager.cancelAll();// ɾ���㷢������֪ͨ
	}

	/**
	 * @��ȡĬ�ϵ�pendingIntent,Ϊ�˷�ֹ2.3�����°汾����
	 * @flags����: �ڶ�����פ:Notification.FLAG_ONGOING_EVENT ���ȥ����
	 *           Notification.FLAG_AUTO_CANCEL
	 */
	public PendingIntent getDefalutIntent(int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 1,
				new Intent(), flags);
		return pendingIntent;
	}

}
