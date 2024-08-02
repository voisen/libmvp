package com.github.voisen.libmvp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;

@SuppressWarnings("all")
public final class ViewDataBindingUtils {

    public static<T extends ViewBinding> @Nullable T inflate(@NonNull Context context, Class<T> vbClazz, ViewGroup parent, boolean attachToRoot){
        try {
            Method method = vbClazz.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            return (T) method.invoke(null, layoutInflater, parent, attachToRoot);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static<T extends ViewBinding> @Nullable T inflate(@NonNull Context context, Class<T> vbClazz){
        try {
            Method method = vbClazz.getMethod("inflate", LayoutInflater.class);
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            return (T) method.invoke(null, layoutInflater);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static<T extends ViewBinding> @Nullable T bind(Class<T> vbClazz, @NonNull View view){
        try {
            Method method = vbClazz.getMethod("bind", View.class);
            return (T) method.invoke(null, view);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
