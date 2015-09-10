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
				builder.setMessage("�Ƿ��˳���ǰ�˺ţ�");
				builder.setTitle("��ʾ");
				builder.setPositiveButton("��",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}

						});

				builder.setNegativeButton("��",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// �˳���ǰ�˺�
								// ����˺���Ϣ

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
		((RelativeLayout) view.findViewById(R.id.layout_exbook))//����
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ctx, MeExBookListActivity.class);//�»
						startActivity(intent);
					}
				});
		((RelativeLayout) view.findViewById(R.id.layout_givebook))//����
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ctx, MeExBookListActivity.class);
				startActivity(intent);
			}
		});
		((RelativeLayout) view.findViewById(R.id.layout_salebook))//����
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ctx, MeExBookListActivity.class);
				startActivity(intent);
			}
		});
		((RelativeLayout) view.findViewById(R.id.layout_collectbook))//�ղ�
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ctx, MeExBookListActivity.class);
				startActivity(intent);
			}
		});
		((RelativeLayout) view.findViewById(R.id.layout_baseInfo))//������Ϣ
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
