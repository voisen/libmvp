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
import com.github.voisen.libmvp.databinding.LibmvpLayoutProgressHubBinding;
import com.github.voisen.libmvp.utils.ViewDataBindingUtils;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.utils.AutoSizeUtils;

public class ProgressHUB {
    private final Dialog mAlert;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());


    private LibmvpLayoutProgressHubBinding viewBinding;

    public ProgressHUB(Context mContext) {
        this.mContext = mContext;
        mAlert = new Dialog(mContext);
        mAlert.setCancelable(false);
        AutoSizeCompat.autoConvertDensityOfGlobal(mContext.getResources());
        viewBinding = ViewDataBindingUtils.inflate(mContext, LibmvpLayoutProgressHubBinding.class);
        initLoadingView();
    }

    private void initLoadingView() {
        mAlert.setContentView(viewBinding.getRoot());
    }

    private void applyAttributes(){
        Window alertWindow = mAlert.getWindow();
        viewBinding.loadingView.setDarkMode(AppProfile.getMode() == AppProfile.Mode.DARK);
        if (alertWindow == null){
            return;
        }

        View decorView = mAlert.getWindow().getDecorView();
        decorView.setPadding(0, 0, 0, 0);
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
        attributes.dimAmount = 0.2f;
        alertWindow.setAttributes(attributes);
    }

    public void dismiss(){
        mHandler.removeCallbacksAndMessages(null);
        mAlert.dismiss();
    }

    public void showMessage(Drawable icon, CharSequence msg, int duration){
        mHandler.removeCallbacksAndMessages(null);
        if (icon == null){
            viewBinding.ivIcon.setVisibility(View.GONE);
        }else{
            viewBinding.ivIcon.setVisibility(View.VISIBLE);
            viewBinding.ivIcon.setImageDrawable(icon);
        }
        viewBinding.loadingView.setVisibility(View.GONE);
        setMessage(msg);
        mHandler.postDelayed(mAlert::dismiss, duration);
        applyAttributes();
        mAlert.show();
    }


    public void showLoading(CharSequence msg){
        viewBinding.loadingView.setProgress(-1);
        mHandler.removeCallbacksAndMessages(null);
        setIcon(null);
        setMessage(msg);
        viewBinding.loadingView.setVisibility(View.VISIBLE);
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
        viewBinding.loadingView.setVisibility(View.GONE);
        applyAttributes();
        mAlert.show();
        mHandler.postDelayed(mAlert::dismiss, duration);
    }


    public void showProgress(float progress, CharSequence msg){
        setIcon(null);
        setMessage(msg);
        viewBinding.loadingView.setVisibility(View.VISIBLE);
        viewBinding.loadingView.setProgress(progress);
        mHandler.removeCallbacksAndMessages(null);
        applyAttributes();
        mAlert.show();
    }

    private void setIcon(Drawable drawable) {
        if (drawable == null){
            viewBinding.ivIcon.setVisibility(View.GONE);
        }else{
            viewBinding.ivIcon.setVisibility(View.VISIBLE);
            viewBinding.ivIcon.setImageDrawable(drawable);
        }
    }

    private void setMessage(CharSequence msg) {
        if (StringUtils.isEmpty(msg)){
            viewBinding.tvMsg.setVisibility(View.GONE);
        }else{
            viewBinding.tvMsg.setText(msg);
            viewBinding.tvMsg.setVisibility(View.VISIBLE);
            if (AppProfile.getMode() == AppProfile.Mode.DARK){
                viewBinding.tvMsg.setTextColor(Color.WHITE);
            }else{
                viewBinding.tvMsg.setTextColor(Color.BLACK);
            }
        }
    }
}
