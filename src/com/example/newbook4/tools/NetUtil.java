package com.example.newbook4.tools;

import android.content.Context;

public class NetUtil {

	public static String getRootAddress(Context ctx) {
		String ip_address = SharedPreferencesTool.readStringValue(ctx,
				KeyConstant.ip_Address);
		String address = "http://" + ip_address + ":8080/Book/";
		return address;
	}

	public static String getBookUpLoadAddress(Context ctx) {
		return getRootAddress(ctx) + "upload_book/";
	}

	public static String getBookBusincesAddress(Context ctx) {
		return getRootAddress(ctx) + "bookBusiness";
	}

	public static String getBaseAddress(Context ctx) {
		return getRootAddress(ctx) + "base";
	}

	public static String getBookExchangeAddress(Context ctx) {
		return getRootAddress(ctx) + "bookExchange";
	}
	
	public static String getBookGiveAddress(Context ctx) {
		return getRootAddress(ctx) + "bookGive";
	}
	public static String getBookBusinessSpecial(Context ctx) {
		return getRootAddress(ctx) + "bookBusinessSpecial";
	}

	public static String getchannelIdAddress(Context ctx) {
		return getRootAddress(ctx) + "channelId";
	}

	// public static String getChannelIdAddress(Context ctx) {
	// return getRootAddress(ctx) + "channelId";
	// }

	public static String getLoginAddress(Context ctx) {
		return getRootAddress(ctx) + "login";
	}

	public static String getRegisterAddress(Context ctx) {
		return getRootAddress(ctx) + "register";
	}

	public static String getClubBusinessAddress(Context ctx) {
		return getRootAddress(ctx) + "clubBusiness";
	}
	public static String getInfobuyBusinessAddress(Context ctx){
		return getRootAddress(ctx)+"infobuyBusiness";
	}
}
