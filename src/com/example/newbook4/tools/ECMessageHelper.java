package com.example.newbook4.tools;

import java.util.List;
import android.content.Context;
import android.util.Log;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

public class ECMessageHelper {

	private static Context ctx;

	public static void setContext(Context context) {
		ctx = context;
	}

	public static void sendMessage(String fromId, String toId, String content) {
		try {
			// �齨һ�������͵�ECMessage
			ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
			// ������Ϣ�����ԣ������ߣ������ߣ�����ʱ���
			msg.setForm(fromId);
			msg.setMsgTime(System.currentTimeMillis());
			// ������Ϣ������
			msg.setTo(toId);
			msg.setSessionId(toId);
			// ������Ϣ�������ͣ����ͻ��߽��գ�
			msg.setDirection(ECMessage.Direction.SEND);

			// ����һ���ı���Ϣ�壬����ӵ���Ϣ������
			ECTextMessageBody msgBody = new ECTextMessageBody(content);

			// ����Ϣ���ŵ�ECMessage��
			msg.setBody(msgBody);
			// ����SDK���ͽӿڷ�����Ϣ��������
			ECChatManager manager = ECDevice.getECChatManager();
			manager.sendMessage(msg, new ECChatManager.OnSendMessageListener() {
				@Override
				public void onSendMessageComplete(ECError error,
						ECMessage message) {
					// ������Ϣ���ͽ��
					if (message == null) {
						// �����ϢΪ��
						return;
					}
					// �����͵���Ϣ���µ��������ݿⲢˢ��UI
				}

				@Override
				public void onProgress(String msgId, int totalByte,
						int progressByte) {
					// �����ļ������ϴ����ȣ����ϴ��ļ���ͼƬʱ��SDK�ص��÷�����
				}

				@Override
				public void onComplete(ECError error) {
					// ����
				}
			});
		} catch (Exception e) {
			// �������쳣
			Log.e("ECMessageHelper", "send message fail , e=" + e.getMessage());
		}

	}

	public static void initStep1(final String im_account) {

		Log.e("ECMessageHelper", "initStep1");
		// ��һ������ʼ��SDK
		// �ж�SDK�Ƿ��Ѿ���ʼ��������Ѿ���ʼ�������ֱ�ӵ��õ�½�ӿ�
		// û�г�ʼ�����Ƚ��г�ʼ��SDK��Ȼ����õ�¼�ӿ�ע��SDK
		if (!ECDevice.isInitialized()) {
			ECDevice.initial(ctx, new ECDevice.InitListener() {
				@Override
				public void onInitialized() {
					// SDK�Ѿ���ʼ���ɹ�
					initStep2(im_account);
				}

				@Override
				public void onError(Exception exception) {
					// SDK ��ʼ��ʧ��,����������ԭ�����
					// 1������SDK�Ѿ����ڳ�ʼ��״̬
					// 2��SDK��������Ҫ��Ȩ��δ���嵥�ļ���AndroidManifest.xml�������á�
					// ����δ���÷�������android:exported="false";
					// 3����ǰ�ֻ��豸ϵͳ�汾����ECSDK��֧�ֵ���Ͱ汾����ǰECSDK֧��
					// Android Build.VERSION.SDK_INT �Լ����ϰ汾��
				}
			});
		}
	}

	private static void initStep2(final String im_account) {

		// �ڶ���������ע�����������֪ͨ�ص�����
		// ����ע������Ҫ�Ĳ�����Ϣ
		ECInitParams params = new ECInitParams();
		params.setUserid(im_account);
		params.setAppKey("aaf98f894c9d994b014cb0bb82730c25");
		params.setToken("db9ff9fb0dae11e5ac73ac853d9f54f2");
		// 1�����û���+�����½������ǿ�����ߣ��ߵ��Ѿ����ߵ��豸��
		// 2�����Զ�����ע�ᣨ����˺��Ѿ��������豸��¼�����ʾ��ص�½��
		// 3 LoginMode��FORCE_LOGIN AUTO��
		params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);

		// ���õ�½״̬�ص�
		params.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
			public void onConnect() {
				// ����4.0��5.0�ɲ��ش���
			}

			@Override
			public void onDisconnect(ECError error) {
				// ����4.0��5.0�ɲ��ش���
			}

			@Override
			public void onConnectState(ECDevice.ECConnectState state,
					ECError error) {
				if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
					// if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
					// // �˺���ص�½
					// showToast("�˺���ص�½");
					// } else {
					// // ����״̬ʧ��
					// showToast("����״̬ʧ��");
					// }
					return;
				} else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
					// ��½�ɹ�
					// showToast("��½�ɹ�");

					Log.e("ECMessageHelper", "��½�ɹ�");
				}
			}
		});

		// ����SDK������Ϣ�ص�
		params.setOnChatReceiveListener(new OnChatReceiveListener() {

			@Override
			public void OnReceivedMessage(ECMessage msg) {
				dealMessage(msg);

			}

			@Override
			public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage arg0) {

			}

			@Override
			public void onReceiveDeskMessage(ECMessage arg0) {

			}

			@Override
			public void onServicePersonVersion(int arg0) {

			}

			@Override
			public void onSoftVersion(String arg0, int arg1) {

			}

			@Override
			public void onOfflineMessageCount(int count) {
				// ע��SDK�Ĳ�����Ҫ�������²����յ��ûص�
				// ECInitParams.setOnChatReceiveListener(new
				// OnChatReceiveListener());
				// count������ʶ��ǰ�˺ŵ�������Ϣ��
			}

			@Override
			public int onGetOfflineMessage() {
				// ע��SDK�Ĳ�����Ҫ�������²����յ��ûص�
				// ECInitParams.setOnChatReceiveListener(new
				// OnChatReceiveListener());
				// �������onHistoryMessageCount(int count)���ý��յ�������Ϣ��
				// ��Ϣ�� ECDevice.SYNC_OFFLINE_MSG_ALL:ȫ����ȡ 0:����ȡ
				return ECDevice.SYNC_OFFLINE_MSG_ALL;
			}

			@Override
			public void onReceiveOfflineMessageCompletion() {
				// SDK������Ϣ��ȡ���֮���ͨ���ýӿ�֪ͨӦ��
				// Ӧ�ÿ����ڴ���������Loading��Ĺرգ�Notification֪ͨ�ȵ�
			}

			@Override
			public void onReceiveOfflineMessage(List<ECMessage> msgs) {
				// ������Ϣ�Ĵ�����Բο� void OnReceivedMessage(ECMessage msg)����
				// �����߼���ȫһ��
				// �ο� IMChattingHelper.java
				for (ECMessage msg : msgs) {
					dealMessage(msg);
				}

			}

		});

		// ����������֤�����Ƿ���ȷ��ע��SDK
		if (params.validate()) {
			// �ж�ע������Ƿ���ȷ
			ECDevice.login(params);
		}

	}

	/**
	 * ������Ϣ
	 * 
	 * @param msg
	 */
	private static void dealMessage(ECMessage msg) {
		if (msg == null) {
			return;
		}
		// ���յ���IM��Ϣ������IM��Ϣ��������ͬ�Ĵ���(IM��Ϣ���ͣ�ECMessage.Type)
		ECMessage.Type type = msg.getType();
		if (type == ECMessage.Type.TXT) {
			// �����ﴦ���ı���Ϣ
			ECTextMessageBody textMessageBody = (ECTextMessageBody) msg
					.getBody();

			// ����ֻ�����ı���Ϣ
			MessageIntentionHelper.getInstance().dealTxtMessage( textMessageBody.getMessage());
			// Intent intent = new Intent();
			// intent.setAction(Utils.RECEIVE_MSG);
			// intent.putExtra("content", content);
			// ctx.sendBroadcast(intent);
			// Toast.makeText(ctx, content, Toast.LENGTH_SHORT).show();
			
			

		}
	}

}
