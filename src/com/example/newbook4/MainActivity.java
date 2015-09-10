package com.example.newbook4;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.newbook4.tools.ECMessageHelper;
import com.example.newbook4.tools.NetUtil;
import com.example.newbook4.tools.VolleyHelper;
import com.example.newbook4.tools.VolleyHelper.ErrorResponseCB;
import com.example.newbook4.tools.VolleyHelper.ResponseCB;
import com.example.newbook4.wdiget.ChangeColorIconWithTextView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ������
 * 
 * @author Li.fx 2015��8��14��08:39:55
 *
 */
public class MainActivity extends FragmentActivity implements
		OnPageChangeListener, OnClickListener {

	private static final String TAG = "MainActivity";
	/**
	 * �����ؼ�
	 */
	private ViewPager mViewPager;
	/**
	 * ͼ��
	 */
	private BookFragment bookFragment;

	/**
	 * ���ֲ�
	 */
	private ClubFragment clubFragment;
	/**
	 * ��Ϣ
	 */
	private InfobuyFragment infoFragment;
	/**
	 * ��
	 */
	private MeFragment meFragment;
	/**
	 * ������
	 */
	private TextView actionbar_tv;
	/**
	 * ��� ����
	 */
	private Button actionbar_search;
	/**
	 * �����ָʾ
	 */
	private List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<ChangeColorIconWithTextView>();
	/**
	 * �����������
	 */
	private RequestQueue requestQueue = null;
	/**
	 * ������Ϣ
	 */
	private BaseInfo baseInfo;

	private MyApplication myApplication;

	private Context ctx;
	/**
	 * ������ť
	 */
	private Button btn_add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ctx = this;
		myApplication = (MyApplication) getApplication();
		baseInfo = myApplication.baseInfo;
		// �����ڴ治��
		setOverflowShowingAlways();
		// ��ʼ���ռ�
		initComponent();

		initTabIndicator();

		mViewPager.setOnPageChangeListener(this);
		requestQueue = Volley.newRequestQueue(this);
		loginIM();
	}

	/**
	 * ��¼IM��Ϣ
	 */
	private void loginIM() {
		Log.d(TAG, "loginIM");
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "retrieval_ImAccount");
			jsonObject.put("userId", baseInfo.userId);

			VolleyHelper.doPost_JSONObject(jsonObject,
					NetUtil.getBaseAddress(ctx), requestQueue,
					new ResponseCB() {
						@Override
						public void callBack(JSONObject response) {
							Log.d(TAG, "response:" + response);
							try {
								int errorCode = response.getInt("errorcode");
								if (errorCode == 0) {
									String imAccount = response
											.getString("imAccount");
									if (!TextUtils.isEmpty(imAccount)) {
										ECMessageHelper.initStep1(imAccount);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

					}, new ErrorResponseCB() {
						@Override
						public void callBack(VolleyError error) {
							Log.d(TAG, "error:" + error);
						}

					});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initComponent() {
		actionbar_tv = (TextView) findViewById(R.id.actionbar_tv);
		actionbar_tv.setTypeface(myApplication.popTypeface);

		actionbar_search = (Button) findViewById(R.id.actionbar_search);
		actionbar_search.setOnClickListener(actionbar_searchListener);

		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mViewPager.setOffscreenPageLimit(3);

		btn_add = (Button) findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, NewListActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_up_in,
						R.anim.slide_down_out);
			}
		});
	}

	
	
	private View.OnClickListener actionbar_searchListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			//showToast("���������");
		}
	};

	private void initTabIndicator() {
		ChangeColorIconWithTextView one = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_one);
		ChangeColorIconWithTextView clubFragment = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_two);
		ChangeColorIconWithTextView infoFragment = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_third);
		ChangeColorIconWithTextView meFragment = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_four);

		mTabIndicator.add(one);
		mTabIndicator.add(clubFragment);
		mTabIndicator.add(infoFragment);
		mTabIndicator.add(meFragment);

		one.setOnClickListener(this);
		clubFragment.setOnClickListener(this);
		infoFragment.setOnClickListener(this);
		meFragment.setOnClickListener(this);

		one.setIconAlpha(1.0f);
	}

	@Override
	public void onPageSelected(int arg0) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// showTost("position=" + position + "positionOffset:" +
		// positionOffset);

		if (positionOffset > 0) {
			ChangeColorIconWithTextView left = mTabIndicator.get(position);
			ChangeColorIconWithTextView right = mTabIndicator.get(position + 1);

			left.setIconAlpha(1 - positionOffset);
			right.setIconAlpha(positionOffset);
		}

		setState(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	private void setState(int post) {
		switch (post) {
		case 0:
			actionbar_search.setVisibility(View.VISIBLE);
			break;
		case 1:
			actionbar_search.setVisibility(View.GONE);
			break;
		case 2:
			actionbar_search.setVisibility(View.GONE);
			break;
		case 3:
			actionbar_search.setVisibility(View.GONE);
			break;
		}
		actionbar_tv.setText(mViewPager.getAdapter().getPageTitle(post));
	}

	/**
	 * ���
	 */
	@Override
	public void onClick(View v) {

		resetOtherTabs();
		int post = -1;
		switch (v.getId()) {
		case R.id.id_indicator_one:
			mTabIndicator.get(0).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(0, false);
			post = 0;
			break;
		case R.id.id_indicator_two:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(1, false);
			post = 1;
			break;
		case R.id.id_indicator_third:
			mTabIndicator.get(2).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(2, false);
			post = 2;
			break;
		case R.id.id_indicator_four:
			mTabIndicator.get(3).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(3, false);
			post = 3;
			break;
		}
		if (post != -1) {
			setState(post);
		}

	}

	/**
	 * ����������Tab
	 */
	private void resetOtherTabs() {
		for (int i = 0; i < mTabIndicator.size(); i++) {
			mTabIndicator.get(i).setIconAlpha(0);
		}

	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = { "ͼ��", "���ֲ�", "��Ϣ", "��" };

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (bookFragment == null) {
					bookFragment = new BookFragment();
				}
				return bookFragment;
			case 1:
				if (clubFragment == null) {
					clubFragment = new ClubFragment();
				}
				return clubFragment;
			case 2:
				if (infoFragment == null) {
					infoFragment = new InfobuyFragment();
				}
				return infoFragment;
			case 3:
				if (meFragment == null) {
					meFragment = new MeFragment();
				}
				return meFragment;
			default:
				return null;
			}
		}
	}

	private void showToast(String srt) {
		Toast.makeText(this, srt, Toast.LENGTH_SHORT).show();
	}

	/**
	 * ������ʾ�˳�
	 */
	private int keyBackClickCount = 0;

	/**
	 * �˳� ��ť
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyBackClickCount++) {
			case 0:
				Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT).show();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						keyBackClickCount = 0;
					}
				}, 3000);
				break;
			case 1:
				this.finish();
				break;
			default:
				break;
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

}
