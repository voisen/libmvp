package com.github.voisen.libmvp.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.Collection;

public class TinyRecyclerView extends RecyclerView {

    public interface TinyAdapter<T>{
        default int getItemViewType(int position){
            return 0;
        }

        @NonNull Class<? extends ViewBinding> getItemViewClass(int viewType);

        void onItemViewBinding(@NonNull ViewBinding viewBinding, @NonNull T data, int position);
    }

    private TinyAdapter mTinyAdapter;

    private BaseRecyclerAdapter<Object> mAdapter = new BaseRecyclerAdapter<Object>() {
        @Override
        public Class<? extends ViewBinding> getItemViewBindingClass(int viewType) {
            return mTinyAdapter.getItemViewClass(viewType);
        }

        @Override
        public void onBindView(ViewBinding viewBinding, Object data, int position) {
            mTinyAdapter.onItemViewBinding(viewBinding, data, position);
        }

        @Override
        public int getItemViewType(int position) {
            return mTinyAdapter.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            if (mTinyAdapter == null){
                return 0;
            }
            return super.getItemCount();
        }
    };

    public void setTinyAdapter(TinyAdapter mTinyAdapter) {
        this.mTinyAdapter = mTinyAdapter;
    }


    public void setData(Collection dataList){
        if (mTinyAdapter == null){
            throw new IllegalStateException("你必须先设置Tiny Adapter");
        }
        mAdapter.setData(dataList);
    }


    public void clearData(){
        mAdapter.clearData();
    }

    public void addData(@NonNull Object data){
        mAdapter.addData(data);
    }

    public void addData(int position, @NonNull Object data){
        mAdapter.addData(position, data);
    }

    public void addAll(@NonNull Collection dataList){
        mAdapter.addAll(dataList);
    }

    public void addAll(int index, @NonNull Collection dataList){
        mAdapter.addAll(index, dataList);
    }

    public void remove(int index){
        mAdapter.remove(index);
    }

    public int getItemCount(){
        return mAdapter.getItemCount();
    }

    @NonNull
    public BaseRecyclerAdapter<Object> getAdapter() {
        return mAdapter;
    }

    public TinyRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public TinyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TinyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setAdapter(mAdapter);
    }
}
