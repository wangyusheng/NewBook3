package com.example.newbook4;

import java.text.SimpleDateFormat;
import com.example.newbook4.tools.ECMessageHelper;
import com.example.newbook4.tools.KeyConstant;
import com.example.newbook4.tools.MessageIntentionHelper;
import com.example.newbook4.tools.NotificationsHelper;
import com.example.newbook4.tools.SharedPreferencesTool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;

public class MyApplication extends Application {

	public BaseInfo baseInfo;

	public static Context ctx;

	public Typeface popTypeface = null;

	public SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	public void onCreate() {
		super.onCreate();

		initValue();

		baseInfo = new BaseInfo();
		// TODO
		baseInfo.userId = 4;

		ctx = this;

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.discCacheSize(50 * 1024 * 1024)//
				.discCacheFileCount(100)// ª∫¥Ê“ª∞Ÿ’≈Õº∆¨
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);

		ECMessageHelper.setContext(ctx);
		NotificationsHelper.setContext(ctx);
		MessageIntentionHelper.setContext(ctx);

		popTypeface = Typeface.createFromAsset(getAssets(), "fonts/pop.ttf");

	}

	private void initValue() {
		if (TextUtils.isEmpty(SharedPreferencesTool.readStringValue(this,
				KeyConstant.ip_Address))) {
			SharedPreferencesTool.writeStringValue(this,
					KeyConstant.ip_Address, "219.223.233.12");//219.223.238.80
		}

	}

}
