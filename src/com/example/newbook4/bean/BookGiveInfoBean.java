package com.example.newbook4.bean;

import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;


	/**
	 * ‘˘ÀÕ Õº È –≈œ¢
	 * 
	 * @author Administrator
	 *
	 */
public class BookGiveInfoBean implements Parcelable {
		public int info_id;
		public int release_user;
		public int obtain_user;
		public int release_book;
		public String release_picture;
		public String release_bookname;
		public String obtain_msg;
		public String generate_time;
		

		public BookGiveInfoBean() {

		}

		public BookGiveInfoBean(Parcel source) {
			info_id = source.readInt();
			release_user = source.readInt();
			obtain_user = source.readInt();
			release_book = source.readInt();
			release_picture=source.readString();
			release_bookname=source.readString();
			obtain_msg = source.readString();
			generate_time = source.readString();
			
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
			;
			

		}

		public static final Parcelable.Creator<BookGiveInfoBean> CREATOR = new Creator<BookGiveInfoBean>() {

			@Override
			public BookGiveInfoBean[] newArray(int size) {
				return new BookGiveInfoBean[size];
			}

			@Override
			public BookGiveInfoBean createFromParcel(Parcel source) {
				return new BookGiveInfoBean(source);
			}
		};

		public static Comparator<BookGiveInfoBean> Comparator = new Comparator<BookGiveInfoBean>() {
			public int compare(BookGiveInfoBean s1, BookGiveInfoBean s2) {
				return s2.info_id - s1.info_id;
			}
		};

}
