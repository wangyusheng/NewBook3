package com.example.newbook4.bean;

import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class BookSaleInfoBean  implements Parcelable{
	public int info_id;
	public int release_user;
	public int obtain_user;
	public int release_book;
	public String release_picture;
	public String release_bookname;
	public String obtain_msg;
	public String generate_time;
	

	

	public BookSaleInfoBean() {

	}

	public BookSaleInfoBean(Parcel source) {
		info_id = source.readInt();
		release_user = source.readInt();
		obtain_user = source.readInt();
		release_book = source.readInt();
		release_picture=source.readString();
		release_bookname=source.readString();
		obtain_msg = source.readString();
		generate_time = source.readString();
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
		dest.writeString(release_picture);
		dest.writeString(release_bookname);
		dest.writeString(obtain_msg);
		dest.writeString(generate_time);
		dest.writeString(release_bookname);
		

	}

	public static final Parcelable.Creator<BookSaleInfoBean> CREATOR = new Creator<BookSaleInfoBean>() {

		@Override
		public BookSaleInfoBean[] newArray(int size) {
			return new BookSaleInfoBean[size];
		}

		@Override
		public BookSaleInfoBean createFromParcel(Parcel source) {
			return new BookSaleInfoBean(source);
		}
	};

	public static Comparator<BookSaleInfoBean> Comparator = new Comparator<BookSaleInfoBean>() {
		public int compare(BookSaleInfoBean s1, BookSaleInfoBean s2) {
			return s2.info_id - s1.info_id;
		}
	};
}
