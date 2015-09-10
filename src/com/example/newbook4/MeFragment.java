package com.example.newbook4;

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
		((RelativeLayout) view.findViewById(R.id.layout_exbook))//交换
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ctx, MeExBookListActivity.class);//新活动
						startActivity(intent);
					}
				});
		((RelativeLayout) view.findViewById(R.id.layout_givebook))//赠送
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ctx, MeExBookListActivity.class);
				startActivity(intent);
			}
		});
		((RelativeLayout) view.findViewById(R.id.layout_salebook))//出售
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ctx, MeExBookListActivity.class);
				startActivity(intent);
			}
		});
		((RelativeLayout) view.findViewById(R.id.layout_collectbook))//收藏
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ctx, MeExBookListActivity.class);
				startActivity(intent);
			}
		});
		((RelativeLayout) view.findViewById(R.id.layout_baseInfo))//基本信息
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ctx, MeExBookListActivity.class);
				startActivity(intent);
			}
		});

	}
}
