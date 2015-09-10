package com.example.newbook4;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InfoFragment extends Fragment{
	FragmentTabHost mTabHost = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		Bundle bundle = getArguments();
		if (null != bundle) {
			//
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//T.showShort(getActivity(), "FragmentMessage==onCreateView");
		View view = inflater.inflate(R.layout.fragment_info, null);

		initView(view);

		return view;
	}

	private void initView(View view) {

		mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		
		mTabHost.setup(getActivity(), getChildFragmentManager(),
				android.R.id.tabcontent);

		mTabHost.addTab(mTabHost.newTabSpec("sub1").setIndicator("求购信息"),
				InfobuyFragment.class, null);

		mTabHost.addTab(mTabHost.newTabSpec("sub2").setIndicator("海报"),
				PosterFragment.class, null);

		
		
		mTabHost.getTabWidget().setDividerDrawable(R.color.white);

	}
}
