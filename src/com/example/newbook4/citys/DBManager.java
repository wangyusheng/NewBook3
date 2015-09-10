package com.example.newbook4.citys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.newbook4.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DBManager {
	private final int BUFFER_SIZE = 1024;
	public static final String DB_NAME = "city_cn.s3db";
	// TODO
	public static final String PACKAGE_NAME = "com.example.newbook4";
	public static final String DB_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME;
	private SQLiteDatabase database;
	private Context context;
	private File file = null;

	public DBManager(Context context) {
		Log.d("cc", "DBManager");
		this.context = context;
	}

	public void openDatabase() {
		Log.d("cc", "openDatabase()");
		this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
	}

	public SQLiteDatabase getDatabase() {
		Log.d("cc", "getDatabase()");
		return this.database;
	}

	private SQLiteDatabase openDatabase(String dbfile) {
		try {
			Log.d("cc", "open and return");
			file = new File(dbfile);
			if (!file.exists()) {
				Log.d("cc", "file");
				InputStream is = context.getResources().openRawResource(
						R.raw.city);
				if (is != null) {
					Log.d("cc", "is null");
				} else {
				}
				FileOutputStream fos = new FileOutputStream(dbfile);
				if (is != null) {
					Log.d("cc", "fosnull");
				} else {
				}
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
					Log.d("cc", "while");
					fos.flush();
				}
				fos.close();
				is.close();
			}
			database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
			return database;
		} catch (FileNotFoundException e) {
			Log.d("cc", "File not found");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("cc", "IO exception");
			e.printStackTrace();
		} catch (Exception e) {
			Log.d("cc", "exception " + e.toString());
		}
		return null;
	}

	public void closeDatabase() {
		Log.d("cc", "closeDatabase()");
		if (this.database != null)
			this.database.close();
	}
}