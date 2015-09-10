package com.example.newbook4.bean;

import java.util.Comparator;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * 交换 图书 信息
 * 
 * @author Administrator
 *
 */
public class BookExchangeInfoBean implements Parcelable {
	public int info_id;
	public int release_user;
	public int obtain_user;
	public int release_book;
	public int obtain_book;
	public String obtain_msg;
	public String generate_time;
	public String obtain_bookname;
	public String obtain_picture;

	public String release_bookname;

	public BookExchangeInfoBean() {

	}

	public BookExchangeInfoBean(Parcel source) {
		info_id = source.readInt();
		release_user = source.readInt();
		obtain_user = source.readInt();
		release_book = source.readInt();
		obtain_book = source.readInt();
		obtain_msg = source.readString();
		generate_time = source.readString();
		obtain_bookname = source.readString();
		obtain_picture = source.readString();
		release_bookname=source.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(info_id);
		dest.writeInt(release_user);
		dest.writeInt(obtain_user);
		dest.writeInt(release_book);
		dest.writeInt(obtain_book);
		dest.writeString(obtain_msg);
		dest.writeString(generate_time);
		dest.writeString(obtain_bookname);
		dest.writeString(obtain_picture);
		dest.writeString(release_bookname);
		

	}

	public static final Parcelable.Creator<BookExchangeInfoBean> CREATOR = new Creator<BookExchangeInfoBean>() {

		@Override
		public BookExchangeInfoBean[] newArray(int size) {
			return new BookExchangeInfoBean[size];
		}

		@Override
		public BookExchangeInfoBean createFromParcel(Parcel source) {
			return new BookExchangeInfoBean(source);
		}
	};

	public static Comparator<BookExchangeInfoBean> Comparator = new Comparator<BookExchangeInfoBean>() {
		public int compare(BookExchangeInfoBean s1, BookExchangeInfoBean s2) {
			return s2.info_id - s1.info_id;
		}
	};

}
