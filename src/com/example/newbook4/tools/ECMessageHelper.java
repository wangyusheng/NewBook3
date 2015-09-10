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
			// 组建一个待发送的ECMessage
			ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
			// 设置消息的属性：发出者，接受者，发送时间等
			msg.setForm(fromId);
			msg.setMsgTime(System.currentTimeMillis());
			// 设置消息接收者
			msg.setTo(toId);
			msg.setSessionId(toId);
			// 设置消息发送类型（发送或者接收）
			msg.setDirection(ECMessage.Direction.SEND);

			// 创建一个文本消息体，并添加到消息对象中
			ECTextMessageBody msgBody = new ECTextMessageBody(content);

			// 将消息体存放到ECMessage中
			msg.setBody(msgBody);
			// 调用SDK发送接口发送消息到服务器
			ECChatManager manager = ECDevice.getECChatManager();
			manager.sendMessage(msg, new ECChatManager.OnSendMessageListener() {
				@Override
				public void onSendMessageComplete(ECError error,
						ECMessage message) {
					// 处理消息发送结果
					if (message == null) {
						// 如果信息为空
						return;
					}
					// 将发送的消息更新到本地数据库并刷新UI
				}

				@Override
				public void onProgress(String msgId, int totalByte,
						int progressByte) {
					// 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
				}

				@Override
				public void onComplete(ECError error) {
					// 忽略
				}
			});
		} catch (Exception e) {
			// 处理发送异常
			Log.e("ECMessageHelper", "send message fail , e=" + e.getMessage());
		}

	}

	public static void initStep1(final String im_account) {

		Log.e("ECMessageHelper", "initStep1");
		// 第一步：初始化SDK
		// 判断SDK是否已经初始化，如果已经初始化则可以直接调用登陆接口
		// 没有初始化则先进行初始化SDK，然后调用登录接口注册SDK
		if (!ECDevice.isInitialized()) {
			ECDevice.initial(ctx, new ECDevice.InitListener() {
				@Override
				public void onInitialized() {
					// SDK已经初始化成功
					initStep2(im_account);
				}

				@Override
				public void onError(Exception exception) {
					// SDK 初始化失败,可能有如下原因造成
					// 1、可能SDK已经处于初始化状态
					// 2、SDK所声明必要的权限未在清单文件（AndroidManifest.xml）里配置、
					// 或者未配置服务属性android:exported="false";
					// 3、当前手机设备系统版本低于ECSDK所支持的最低版本（当前ECSDK支持
					// Android Build.VERSION.SDK_INT 以及以上版本）
				}
			});
		}
	}

	private static void initStep2(final String im_account) {

		// 第二步：设置注册参数、设置通知回调监听
		// 构建注册所需要的参数信息
		ECInitParams params = new ECInitParams();
		params.setUserid(im_account);
		params.setAppKey("aaf98f894c9d994b014cb0bb82730c25");
		params.setToken("db9ff9fb0dae11e5ac73ac853d9f54f2");
		// 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
		// 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
		// 3 LoginMode（FORCE_LOGIN AUTO）
		params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);

		// 设置登陆状态回调
		params.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
			public void onConnect() {
				// 兼容4.0，5.0可不必处理
			}

			@Override
			public void onDisconnect(ECError error) {
				// 兼容4.0，5.0可不必处理
			}

			@Override
			public void onConnectState(ECDevice.ECConnectState state,
					ECError error) {
				if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
					// if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
					// // 账号异地登陆
					// showToast("账号异地登陆");
					// } else {
					// // 连接状态失败
					// showToast("连接状态失败");
					// }
					return;
				} else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
					// 登陆成功
					// showToast("登陆成功");

					Log.e("ECMessageHelper", "登陆成功");
				}
			}
		});

		// 设置SDK接收消息回调
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
				// 注册SDK的参数需要设置如下才能收到该回调
				// ECInitParams.setOnChatReceiveListener(new
				// OnChatReceiveListener());
				// count参数标识当前账号的离线消息数
			}

			@Override
			public int onGetOfflineMessage() {
				// 注册SDK的参数需要设置如下才能收到该回调
				// ECInitParams.setOnChatReceiveListener(new
				// OnChatReceiveListener());
				// 建议根据onHistoryMessageCount(int count)设置接收的离线消息数
				// 消息数 ECDevice.SYNC_OFFLINE_MSG_ALL:全部获取 0:不获取
				return ECDevice.SYNC_OFFLINE_MSG_ALL;
			}

			@Override
			public void onReceiveOfflineMessageCompletion() {
				// SDK离线消息拉取完成之后会通过该接口通知应用
				// 应用可以在此做类似于Loading框的关闭，Notification通知等等
			}

			@Override
			public void onReceiveOfflineMessage(List<ECMessage> msgs) {
				// 离线消息的处理可以参考 void OnReceivedMessage(ECMessage msg)方法
				// 处理逻辑完全一样
				// 参考 IMChattingHelper.java
				for (ECMessage msg : msgs) {
					dealMessage(msg);
				}

			}

		});

		// 第三步：验证参数是否正确，注册SDK
		if (params.validate()) {
			// 判断注册参数是否正确
			ECDevice.login(params);
		}

	}

	/**
	 * 处理消息
	 * 
	 * @param msg
	 */
	private static void dealMessage(ECMessage msg) {
		if (msg == null) {
			return;
		}
		// 接收到的IM消息，根据IM消息类型做不同的处理(IM消息类型：ECMessage.Type)
		ECMessage.Type type = msg.getType();
		if (type == ECMessage.Type.TXT) {
			// 在这里处理文本消息
			ECTextMessageBody textMessageBody = (ECTextMessageBody) msg
					.getBody();

			// 这里只处理文本信息
			MessageIntentionHelper.getInstance().dealTxtMessage( textMessageBody.getMessage());
			// Intent intent = new Intent();
			// intent.setAction(Utils.RECEIVE_MSG);
			// intent.putExtra("content", content);
			// ctx.sendBroadcast(intent);
			// Toast.makeText(ctx, content, Toast.LENGTH_SHORT).show();
			
			

		}
	}

}
