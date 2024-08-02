package com.github.voisen.libmvp.base;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.github.voisen.libmvp.widget.ProgressHUB;

public interface BaseView {

    /**
     * 显示加载框
     * @param msg 加载框文本
     */
    void showLoading(CharSequence msg);

    /**
     * 隐藏加载框或消息框
     */
    void dismissLoading();

    /**
     * 显示消息
     * @param msg 消息内容
     */
    void showMessage(CharSequence msg);

    /**
     * 显示消息
     * @param drawable 图标
     * @param msg 消息
     */
    void showMessage(Drawable drawable, CharSequence msg);

    /**
     * 根据状态显示带图标的消息
     * @param status 状态
     * @param msg 消息
     */
    void showMessageWithStatus(ProgressHUB.Status status, CharSequence msg);

    /**
     * 显示进度条
     * @param progress 进度 0.0~1.0
     * @param msg 消息内容
     */
    void showProgress(float progress, CharSequence msg);

    /**
     * 显示提示框
     * @param msg 提示框
     */
    void showAlertMessage(CharSequence msg);

    /**
     * 显示提示框
     * @param title 标题
     * @param msg 消息
     * @param cancelText 取消按钮文本
     * @param sureText 确认按钮文本
     * @param onOkClickListener 确认回调
     */
    void showAlertMessage(CharSequence title, CharSequence msg, CharSequence cancelText, CharSequence sureText, DialogInterface.OnClickListener onOkClickListener);
}
