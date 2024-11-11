package com.github.voisen.libmvp.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.StringUtils;
import com.github.voisen.libmvp.databinding.LibmvpLayoutTinyDialogBinding;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class TinyDialog implements DialogInterface {

    private Dialog mAlertDialog;

    private final Builder mBuilder;

    private final LibmvpLayoutTinyDialogBinding mAlertViewBinding;

    private TinyDialog(Builder builder){
        mBuilder = builder;
        mAlertViewBinding = LibmvpLayoutTinyDialogBinding.inflate(LayoutInflater.from(builder.mContext));

        mAlertDialog = new Dialog(mBuilder.mContext);
        mAlertDialog.setContentView(mAlertViewBinding.getRoot());
        mAlertViewBinding.tvTitle.setText(mBuilder.mTitle);
        setMessage(builder.mMessage);
        setTitle(builder.mTitle);
        setContentView(builder.mContentView);
        setNegativeButton(builder.mNegativeButtonText, builder.mNegativeButtonClickListener);
        setPositiveButton(builder.mPositiveButtonText, builder.mPositiveButtonClickListener);
        setCancelable(mBuilder.mCancelable);
    }

    public void setCancelable(boolean cancelable) {
        mAlertDialog.setCancelable(cancelable);
    }

    public void setNegativeButton(CharSequence buttonText, OnClickListener clickListener) {
        if (!StringUtils.isEmpty(buttonText)){
            mAlertViewBinding.btnNegative.setText(buttonText);
            mAlertViewBinding.btnNegative.setOnClickListener(v->{
                if (clickListener != null){
                    clickListener.onClick(this, BUTTON_NEGATIVE);
                }
                if (mBuilder.mClickButtonDismiss){
                    dismiss();
                }
            });
            mAlertViewBinding.btnNegative.setVisibility(View.VISIBLE);
        }else{
            mAlertViewBinding.btnNegative.setVisibility(View.GONE);
        }
    }

    public void setPositiveButton(CharSequence buttonText, OnClickListener clickListener) {
        if (!StringUtils.isEmpty(buttonText)){
            mAlertViewBinding.btnPositive.setText(buttonText);
            mAlertViewBinding.btnPositive.setOnClickListener(v->{
                if (clickListener != null){
                    clickListener.onClick(this, BUTTON_NEGATIVE);
                }
                if (mBuilder.mClickButtonDismiss){
                    dismiss();
                }
            });
            mAlertViewBinding.btnPositive.setVisibility(View.VISIBLE);
        }else{
            mAlertViewBinding.btnPositive.setVisibility(View.GONE);
        }
    }

    private void setContentView(View contentView) {
        mBuilder.mContentView = contentView;
        mAlertViewBinding.fragment.removeAllViews();
        if (contentView != null){
            mAlertViewBinding.fragment.addView(contentView);
        }
    }

    public void setTitle(CharSequence title) {
        mBuilder.mTitle = title;
        if (StringUtils.isEmpty(title)){
            mAlertViewBinding.tvTitle.setVisibility(View.GONE);
        }else{
            mAlertViewBinding.tvTitle.setText(mBuilder.mTitle);
            mAlertViewBinding.tvTitle.setVisibility(View.VISIBLE);
        }
    }

    public void setMessage(CharSequence message) {
        mBuilder.mMessage = message;
        if (StringUtils.isEmpty(message)){
            mAlertViewBinding.tvMessage.setVisibility(View.GONE);
        }else{
            mAlertViewBinding.tvMessage.setText(mBuilder.mMessage);
            mAlertViewBinding.tvMessage.setVisibility(View.VISIBLE);
        }
    }



    public void show(){
        int btnPositiveVisibility = mAlertViewBinding.btnPositive.getVisibility();
        int btnNegativeVisibility = mAlertViewBinding.btnNegative.getVisibility();
        if (btnNegativeVisibility != View.VISIBLE || btnPositiveVisibility != View.VISIBLE){
            mAlertViewBinding.tvLine2.setVisibility(View.GONE);
        }else{
            mAlertViewBinding.tvLine2.setVisibility(View.VISIBLE);
        }
        if (btnNegativeVisibility != View.VISIBLE && btnPositiveVisibility != View.VISIBLE){
            mAlertViewBinding.tvLine1.setVisibility(View.GONE);
            mAlertViewBinding.getRoot().setContentPadding(0, 0, 0, AutoSizeUtils.dp2px(mBuilder.mContext,  25));
        }else{
            mAlertViewBinding.tvLine1.setVisibility(View.VISIBLE);
            mAlertViewBinding.getRoot().setContentPadding(0, 0, 0, 0);
        }
        mAlertDialog.show();
        Window window = mAlertDialog.getWindow();
        if (window == null){
            return;
        }

        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams attributes = window.getAttributes();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(attributes);
        layoutParams.horizontalMargin = 0;
        layoutParams.width = (int) Math.min(Math.min(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight()) * 0.95f, AutoSizeUtils.dp2px(mBuilder.mContext, 800));
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
    }

    @Override
    public void cancel() {
        mAlertDialog.cancel();
    }

    @Override
    public void dismiss() {
        mAlertDialog.dismiss();
    }


    public static class Builder{
        private final Context mContext;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mNegativeButtonText;
        private CharSequence mPositiveButtonText;
        private View mContentView;
        private boolean mCancelable = true;
        private boolean mClickButtonDismiss = true;

        private OnClickListener mNegativeButtonClickListener;
        private OnClickListener mPositiveButtonClickListener;

        public Builder(Context context){
            mContext = context;
        }

        public Builder setTitle(CharSequence title){
            mTitle = title;
            return this;
        }

        public Builder setMessage(CharSequence message){
            mMessage = message;
            return this;
        }

        public Builder setTitle(@StringRes int titleRes){
            mTitle = mContext.getString(titleRes);
            return this;
        }

        public Builder setMessage(@StringRes int messageRes){
            mMessage = mContext.getString(messageRes);
            return this;
        }

        public Builder setCancelable(boolean cancelable){
            mCancelable = cancelable;
            return this;
        }

        public Builder setClickButtonDismiss(boolean clickButtonDismiss){
            mClickButtonDismiss = clickButtonDismiss;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, OnClickListener onClickListener){
            mPositiveButtonText = text;
            mPositiveButtonClickListener = onClickListener;
            return this;
        }


        public Builder setNegativeButton(CharSequence text, OnClickListener onClickListener){
            mNegativeButtonText = text;
            mNegativeButtonClickListener = onClickListener;
            return this;
        }

        public Builder setContentView(@Nullable View contentView){
            mContentView = contentView;
            return this;
        }

        public Builder setPositiveButton(@StringRes int text, OnClickListener onClickListener){
            return setPositiveButton(mContext.getString(text), onClickListener);
        }


        public Builder setNegativeButton(@StringRes int text, OnClickListener onClickListener){
            return setNegativeButton(mContext.getString(text), onClickListener);
        }

        public TinyDialog show(){
            TinyDialog tinyDialog = new TinyDialog(this);
            tinyDialog.show();
            return tinyDialog;
        }
    }
}
