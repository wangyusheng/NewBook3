package com.example.newbook4.bean;

import java.io.Serializable;
import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

public class BookBean implements Parcelable {

	public int book_Id;

	public int user_Id;

	public String book_Name;

	public String author_name;

	public String abstract_content;

	public String time_Release;

	public String transcation;

	public String new_Old;

	public String picture_Path;

	public int priority;

	public int rating;

	public String generation_time;

	public String interest;

	public int state;

	public BookBean() {

	}

	public BookBean(Parcel source) {
		// ��ȡ

		book_Id = source.readInt();
		user_Id = source.readInt();
		book_Name = source.readString();
		author_name = source.readString();
		abstract_content = source.readString();
		time_Release = source.readString();
		transcation = source.readString();
		new_Old = source.readString();
		picture_Path = source.readString();
		priority = source.readInt();
		rating = source.readInt();
		generation_time = source.readString();
		interest = source.readString();
		state=source.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// д��
		dest.writeInt(book_Id);
		dest.writeInt(user_Id);
		dest.writeString(book_Name);
		dest.writeString(author_name);
		dest.writeString(abstract_content);
		dest.writeString(time_Release);
		dest.writeString(transcation);
		dest.writeString(new_Old);
		dest.writeString(picture_Path);
		dest.writeInt(priority);
		dest.writeInt(rating);
		dest.writeString(generation_time);
		dest.writeString(interest);
		dest.writeInt(state);

	}

	public static final Parcelable.Creator<BookBean> CREATOR = new Creator<BookBean>() {

		@Override
		public BookBean[] newArray(int size) {
			return new BookBean[size];
		}

		// ��Parcel�������л�ΪParcelableDate
		@Override
		public BookBean createFromParcel(Parcel source) {
			return new BookBean(source);
		}
	};

	public static Comparator<BookBean> Comparator = new Comparator<BookBean>() {
		public int compare(BookBean s1, BookBean s2) {
			return s2.book_Id - s1.book_Id;
		}
	};
}
