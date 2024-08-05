package com.github.voisen.libmvp.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.github.voisen.libmvp.AppProfile;
import com.github.voisen.libmvp.R;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class ProgressHUB {
    private final Dialog mAlert;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final static int ICON_SIZE = 50;
    private final static int LOADING_SIZE = 50;
    private TextView mMessageView;
    private LoadingView mLoadingView;
    private ImageView mIconView;

    public ProgressHUB(Context mContext) {
        this.mContext = mContext;
        mAlert = new Dialog(mContext);
        mAlert.setCancelable(false);
        initLoadingView();
    }

    private void initLoadingView() {
        LinearLayout loadingView = new LinearLayout(mContext);
        loadingView.setOrientation(LinearLayout.VERTICAL);
        loadingView.setHorizontalGravity(Gravity.CENTER);
        int loadingViewPadding = AutoSizeUtils.dp2px(mContext, 20);
        loadingView.setPadding(loadingViewPadding,loadingViewPadding,loadingViewPadding,loadingViewPadding);
        loadingView.setMinimumWidth(AutoSizeUtils.dp2px(mContext, 130));

        int bottomMargin = AutoSizeUtils.dp2px(mContext, 13);
        //添加imageview
        mIconView = new ImageView(mContext);
        LinearLayout.LayoutParams iconViewParams = new LinearLayout.LayoutParams(AutoSizeUtils.dp2px(mContext, ICON_SIZE), AutoSizeUtils.dp2px(mContext, ICON_SIZE));
        iconViewParams.bottomMargin = bottomMargin;
        loadingView.addView(mIconView, iconViewParams);

        mLoadingView = new LoadingView(mContext);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(AutoSizeUtils.dp2px(mContext, LOADING_SIZE), AutoSizeUtils.dp2px(mContext, LOADING_SIZE));
        barParams.bottomMargin = bottomMargin;
        loadingView.addView(mLoadingView, barParams);

        mMessageView = new TextView(mContext);
        mMessageView.setTextSize(15);
        mMessageView.setTextColor(Color.BLACK);
        mMessageView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams msgLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingView.addView(mMessageView, msgLayoutParams);
        mAlert.setContentView(loadingView);
    }

    private void applyAttributes(){
        Window alertWindow = mAlert.getWindow();
        mLoadingView.setDarkMode(AppProfile.getMode() == AppProfile.Mode.DARK);
        if (alertWindow == null){
            return;
        }
        WindowManager.LayoutParams attributes = alertWindow.getAttributes();
        if (attributes == null){
            attributes = new WindowManager.LayoutParams();
        }
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (AppProfile.getMode() == AppProfile.Mode.DARK){
            alertWindow.getDecorView().setBackgroundResource(R.drawable.libmvp_bg_hub_dark);
        }else{
            alertWindow.getDecorView().setBackgroundResource(R.drawable.libmvp_bg_hub_light);
        }
        attributes.dimAmount = 0.3f;
        alertWindow.setAttributes(attributes);
    }

    public void dismiss(){
        mHandler.removeCallbacksAndMessages(null);
        mAlert.dismiss();
    }

    public void showMessage(Drawable icon, CharSequence msg, int duration){
        mHandler.removeCallbacksAndMessages(null);
        if (icon == null){
            mIconView.setVisibility(View.GONE);
        }else{
            mIconView.setVisibility(View.VISIBLE);
            mIconView.setImageDrawable(icon);
        }
        mLoadingView.setVisibility(View.GONE);
        setMessage(msg);
        mHandler.postDelayed(mAlert::dismiss, duration);
        applyAttributes();
        mAlert.show();
    }


    public void showLoading(CharSequence msg){
        mLoadingView.setProgress(-1);
        mHandler.removeCallbacksAndMessages(null);
        setIcon(null);
        setMessage(msg);
        mLoadingView.setVisibility(View.VISIBLE);
        applyAttributes();
        mAlert.show();
    }

    public void showMessageWithStatus(AppProfile.Status status, CharSequence msg, long duration){
        mHandler.removeCallbacksAndMessages(null);
        setMessage(msg);
        switch (status){
            case INFO:
                setIcon(AppProfile.getInfoDrawable());
                break;
            case ERROR:
                setIcon(AppProfile.getErrorDrawable());
                break;
            case SUCCESS:
                setIcon(AppProfile.getSuccessDrawable());
                break;
            case UNKNOWN:
                setIcon(null);
                break;
        }
        mLoadingView.setVisibility(View.GONE);
        applyAttributes();
        mAlert.show();
        mHandler.postDelayed(mAlert::dismiss, duration);
    }


    public void showProgress(float progress, CharSequence msg){
        setIcon(null);
        setMessage(msg);
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.setProgress(progress);
        mHandler.removeCallbacksAndMessages(null);
        applyAttributes();
        mAlert.show();
    }

    private void setIcon(Drawable drawable) {
        if (drawable == null){
            mIconView.setVisibility(View.GONE);
        }else{
            mIconView.setVisibility(View.VISIBLE);
            mIconView.setImageDrawable(drawable);
        }
    }

    private void setMessage(CharSequence msg) {
        if (StringUtils.isEmpty(msg)){
            mMessageView.setVisibility(View.GONE);
        }else{
            mMessageView.setText(msg);
            mMessageView.setVisibility(View.VISIBLE);
            if (AppProfile.getMode() == AppProfile.Mode.DARK){
                mMessageView.setTextColor(Color.WHITE);
            }else{
                mMessageView.setTextColor(Color.BLACK);
            }
        }
    }
}
