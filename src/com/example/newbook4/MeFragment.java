package com.example.newbook4;

import com.example.newbook4.club.ClubListActivity;
import com.example.newbook4.me.MeExBookListActivity;
import com.example.newbook4.tools.KeyConstant;
import com.example.newbook4.tools.SharedPreferencesTool;

import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MeFragment extends Fragment {
	private static final String TAG = MeFragment.class.getName();

	private View view;

	private Context ctx;

	private RelativeLayout layout_exit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_me,
				null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ctx = getActivity();
		initView();

	}

	private void initView() {
		layout_exit = (RelativeLayout) view.findViewById(R.id.layout_exit);
		layout_exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setMessage("是否退出当前账号？");
				builder.setTitle("提示");
				builder.setPositiveButton("否",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}

						});

				builder.setNegativeButton("是",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// 退出当前账号
								// 清空账号信息

								Intent intent = new Intent();
								SharedPreferencesTool.writeStringValue(ctx,
										KeyConstant.user_Name, "");
								SharedPreferencesTool.writeStringValue(ctx,
										KeyConstant.pass_Word, "");
								intent.setClass(ctx, LoginActivity.class);
								startActivity(intent);
								getActivity().finish();
							}

						});
				builder.create().show();
			}
		});
		// 图书--交换
		((RelativeLayout) view.findViewById(R.id.layout_exbook))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ctx, MeExBookListActivity.class);
						startActivity(intent);
					}
				});

		// 俱乐部--发布

		((RelativeLayout) view.findViewById(R.id.layout_meclub))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra("sign", 1);
						intent.setClass(ctx, ClubListActivity.class);
						startActivity(intent);
					}
				});
		// 俱乐部--收藏
		((RelativeLayout) view.findViewById(R.id.layout_enrollclub))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra("sign", 2);
						intent.setClass(ctx, ClubListActivity.class);
						startActivity(intent);
					}
				});
	}
}
