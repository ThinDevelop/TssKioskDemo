package com.tss.tsskioskdemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class DialogFactory {
	private static HashMap<String, Dialog> dialogs = new HashMap<String, Dialog>();

	private static TextView tvTitle;
	private static TextView tvCount;
	private static LinearLayout llBtn;
	private static TextView tvMsg;
	private static TextView tvOk;
	private static TextView tvCancel;
	private static CountDownTimer loadingTimer = null;

	public static boolean dismissAlert(Activity activity) {
		if (null != loadingTimer) {
			loadingTimer.cancel();
			loadingTimer = null;
		}
		Dialog dialog = (Dialog) dialogs.get(activity.toString());
		if ((dialog != null) && (dialog.isShowing())) {
			try {
				dialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dialogs.remove(activity.toString());
			return true;
		} else {
			return false;
		}
	}

	private static Dialog createDialog(Activity activity) {

		dismissAlert(activity);
		Dialog dialog = new Dialog(activity, R.style.dialog_basic);
		dialog.setContentView(R.layout.dialog_show_tip);

		dialogs.put(activity.toString(), dialog);

		tvTitle = (TextView) dialog.findViewById(R.id.dialog_tip_title);
		tvCount = (TextView) dialog.findViewById(R.id.dialog_tip_time);
		llBtn = (LinearLayout) dialog.findViewById(R.id.dialog_btn_ll);
		tvMsg = (TextView) dialog.findViewById(R.id.dialog_tip_content);
		tvOk = (TextView) dialog.findViewById(R.id.dialog_tip_confirm_btn);
		tvCancel = (TextView) dialog.findViewById(R.id.dialog_tip_cancel_btn);
		// 设置对话框点击空白处不可关闭，点击返回按钮不可关闭。
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				} else {
					return false; // 默认返回 false
				}
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	/**
	 * 显示提示对话框，无取消按钮，不可取消，只能通过调用该类的dismissAlert方法
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param msg
	 *            内容
	 */
	public static void showMessage(Activity context, String title, String msg) {
		clearBeforeDialog(context);
		Dialog dialog = createDialog(context);
		llBtn.setVisibility(View.GONE);
		tvCount.setVisibility(View.GONE);
		tvTitle.setText(title);
		tvMsg.setText(msg);
		tvMsg.setTextColor(context.getResources().getColor(R.color.green_3ea36b));
		dialog.show();
	}

	/**
	 * 显示两个消息按钮的消息提示框，按钮点击事件均需实现,取消按钮可以传null；
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param okTitle
	 * @param okClick
	 * @param canTitle
	 * @param canClick
	 */
	public static void showMessage(final Activity context, String title, String msg, String okTitle, OnClickListener okClick, String canTitle, OnClickListener canClick) {
		clearBeforeDialog(context);
		Dialog dialog = createDialog(context);
		llBtn.setVisibility(View.VISIBLE);
		tvCount.setVisibility(View.GONE);
		tvTitle.setText(title);
		tvMsg.setText(msg);
		tvOk.setText(okTitle);
		tvCancel.setText(canTitle);
		tvOk.setOnClickListener(okClick);
		if (null != canClick) {
			tvCancel.setOnClickListener(canClick);
		} else {
			tvCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismissAlert(context);
				}
			});
		}
		dialog.show();
	}

	/**
	 * 显示加载对话框
	 * 
	 * @param activity
	 * @param title
	 * @param msg
	 */
	public static void showLoadingDialog(final Activity activity, String title, String msg) {
		clearBeforeDialog(activity);
		ImageView ivLoad;
		TextView tvLoadTip;
		final TextView tvLoadCount;
		dismissAlert(activity);
		Dialog dialog = new Dialog(activity, R.style.dialog_basic);
		dialog.setContentView(R.layout.dialog_loading);

		dialogs.put(activity.toString(), dialog);
		ivLoad = (ImageView) dialog.findViewById(R.id.loading_imgview);
		tvLoadTip = (TextView) dialog.findViewById(R.id.loading_tip_tv);
		tvLoadCount = (TextView) dialog.findViewById(R.id.loading_count_tv);
		if (TextUtils.isEmpty(title)) {
			tvLoadCount.setVisibility(View.GONE);
		}
		// 启动动画
		Animation operatingAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		ivLoad.setAnimation(operatingAnim);
		// 设置对话框点击空白处不可关闭，点击返回按钮不可关闭。
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				} else {
					return false; // 默认返回 false
				}
			}
		});
		dialog.setCanceledOnTouchOutside(false);

		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU) {
					return true;
				}
				return false;
			}
		});
		tvLoadTip.setText(msg);

		loadingTimer = new CountDownTimer(60000, 1000) {

			@Override
			public void onFinish() {
				// 计时完毕时触发
				dismissAlert(activity);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// 计时过程显示
				tvLoadCount.setText(String.valueOf(millisUntilFinished / 1000));
			}
		};
		loadingTimer.cancel();
		loadingTimer.start();
		dialog.show();
	}


	/**
	 * 显示确认对话框
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param okStr
	 */
	public static void showConfirmMessage(final Activity context, String title, String msg, String okStr, final OnClickListener okClick) {
		clearBeforeDialog(context);
		Dialog dialog = createDialog(context);
		llBtn.setVisibility(View.VISIBLE);
		tvCount.setVisibility(View.GONE);
		tvTitle.setText(title);
		tvMsg.setText(msg);
		tvOk.setText(okStr);
		tvCancel.setVisibility(View.GONE);
		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissAlert(context);
				if (null != okClick) {
					okClick.onClick(v);
				}
			}
		});
		dialog.show();
	}

	/**
	 * 显示存在倒计时的确认对话框，倒计时结算自动点击
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param okStr
	 * @param timeout
	 *            单位毫秒
	 */
	public static void showConfirmMessageTimeout(int timeout, final Activity context, String title, String msg, String okStr, final OnClickListener okClick) {
		clearBeforeDialog(context);
		Dialog dialog = createDialog(context);
		llBtn.setVisibility(View.VISIBLE);
		loadingTimer = new CountDownTimer(timeout, 1000) {

			@Override
			public void onFinish() {
				// 计时完毕时触发
				dismissAlert(context);
				if (null != okClick) {
					okClick.onClick(tvOk);
				}
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// 计时过程显示
				tvCount.setText(String.valueOf(millisUntilFinished / 1000));
			}
		};
		if (timeout > 1000) {
			tvCount.setVisibility(View.VISIBLE);
			loadingTimer.start();
		} else {
			tvCount.setVisibility(View.GONE);
		}
		tvTitle.setText(title);
		tvMsg.setText(msg);
		tvOk.setText(okStr);
		tvCancel.setVisibility(View.GONE);
		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissAlert(context);
				if (null != okClick) {
					okClick.onClick(v);
					loadingTimer.cancel();
				}
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
					return true;
				}
				return false;
			}
		});
		dialog.show();

	}

	/**
	 * 显示确认对话框
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param okStr
	 */
	public static void showConfirmMessage(final Activity context, String title, String msg, String okStr) {
		clearBeforeDialog(context);
		Dialog dialog = createDialog(context);
		llBtn.setVisibility(View.VISIBLE);
		tvCount.setVisibility(View.GONE);
		tvTitle.setText(title);
		tvMsg.setText(msg);
		tvOk.setText(okStr);
		tvCancel.setVisibility(View.GONE);
		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissAlert(context);
			}
		});
		dialog.show();
	}

	private static Toast mToast;

	public static void showTip(Context mContext, String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
		}
		mToast.setText(msg);
		mToast.show();
	}

	/**
	 * 清除map里面同一个activity弹出的对话框
	 * 
	 * @param activity
	 */
	public static void clearBeforeDialog(Activity activity) {
		if (dialogs == null) return;
		Dialog dialog = (Dialog) dialogs.get(activity.toString());
		if (null != dialog && dialog.isShowing()) {
			try {
				dialog.dismiss();
			} catch (Exception e) {
				// logger.error("弹框关闭异常：",e);
			}
			dialogs.remove(activity.toString());
		}
	}

	public static void ShowInputMessageDialog(Activity context) {

	}
}
