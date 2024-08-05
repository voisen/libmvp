package com.github.voisen.libmvp.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.github.voisen.libmvp.AppProfile;
import com.github.voisen.libmvp.R;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class TinyToast {

    private static Toast mToast;
    private static Context mContext;
    private static int mMessageTextSize = 17;
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public static void init(Context context){
        mContext = context;
    }

    public static void showMessage(AppProfile.Status status, CharSequence msg, long duration){
        showToast(status, msg, duration);
    }

    private static void cancelToast() {
        if (mToast != null){
            mToast.cancel();
            mToast = null;
        }
    }

    public static void setMessageTextSize(int messageTextSize) {
        TinyToast.mMessageTextSize = messageTextSize;
    }

    private static synchronized void showToast(AppProfile.Status status, CharSequence msg, long duration) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()){
            mHandler.post(() -> {
                showToast(status, msg, duration);
            });
            return;
        }
        cancelToast();
        mHandler.removeCallbacksAndMessages(null);
        cancelToast();
        mToast = Toast.makeText(mContext, null, Toast.LENGTH_LONG);
        CardView cardView = new CardView(mContext);
        cardView.setRadius(AutoSizeUtils.dp2px(mContext, 6));
        LinearLayout contentLayout = new LinearLayout(mContext);
        contentLayout.setMinimumWidth(AutoSizeUtils.dp2px(mContext, 100));
        int px = AutoSizeUtils.dp2px(mContext, 15);
        contentLayout.setPadding(px, (int) (px * 0.6), px, (int) (px * 0.6));
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        ImageView iconView = getImageView(status);
        if (iconView != null){
            int iconSize = AutoSizeUtils.dp2px(mContext, 45);
            ViewGroup.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
            contentLayout.addView(iconView, iconParams);
        }

        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(mMessageTextSize);
        textView.setText(msg);
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        textView.setMaxWidth((int) (Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels) * 0.7f));
        if (iconView != null){
            textView.setPadding(0, AutoSizeUtils.dp2px(mContext, 6), 0, 0);
        }
        contentLayout.addView(textView, -2, -2);
        contentLayout.setGravity(Gravity.CENTER);
        if (AppProfile.getMode() == AppProfile.Mode.DARK){
            cardView.setCardBackgroundColor(Color.parseColor("#333333"));
            textView.setTextColor(Color.WHITE);
        }else{
            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            textView.setTextColor(Color.BLACK);
        }
        cardView.setElevation(AutoSizeUtils.dp2px(mContext, 6));
        cardView.addView(contentLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mToast.setView(cardView);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mHandler.postDelayed(TinyToast::cancelToast, duration);
        mToast.show();
    }

    private static @Nullable ImageView getImageView(AppProfile.Status status) {
        Drawable iconDrawable = null;
        switch (status){
            case ERROR:
                iconDrawable = AppProfile.getErrorDrawable();
                break;
            case SUCCESS:
                iconDrawable = AppProfile.getSuccessDrawable();
                break;
            case INFO:
                iconDrawable = AppProfile.getInfoDrawable();
                break;
            case UNKNOWN:
                return null;
        }
        ImageView iconView = new ImageView(mContext);
        iconView.setImageDrawable(iconDrawable);
        return iconView;
    }

}
