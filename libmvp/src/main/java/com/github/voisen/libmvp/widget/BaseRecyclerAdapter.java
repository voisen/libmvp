package com.github.voisen.libmvp.widget;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.github.voisen.libmvp.utils.ViewDataBindingUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.ItemViewHolder> {

    private final List<T> mDataList = new ArrayList<>();

    public abstract Class<? extends ViewBinding> getItemViewBindingClass(int viewType);

    public abstract void onBindView(ViewBinding viewBinding, T data, int position);

    public void setData(Collection<T> data){
        mDataList.clear();
        if (data != null){
            mDataList.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void clearData(){
        this.mDataList.clear();
        notifyDataSetChanged();
    }

    public void addData(@NonNull T data){
        this.mDataList.add(data);
        notifyItemInserted(this.mDataList.size()-1);
    }

    public void addData(int position, @NonNull T data){
        this.mDataList.add(position, data);
        notifyItemInserted(position);
    }

    public void addAll(@NonNull Collection<T> dataList){
        int start = mDataList.size();
        this.mDataList.addAll(dataList);
        notifyItemRangeInserted(start, dataList.size());
    }

    public void addAll(int index, @NonNull Collection<T> dataList){
        this.mDataList.addAll(index, dataList);
        notifyItemRangeInserted(index, dataList.size());
    }

    public void remove(int index){
        this.mDataList.remove(index);
        notifyItemRemoved(index);
    }

    public List<T> getDataList() {
        return mDataList;
    }

    @NonNull
    @Override
    public final ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Class<? extends ViewBinding> bindingClass = getItemViewBindingClass(viewType);
        ViewBinding viewBinding = ViewDataBindingUtils.inflate(parent.getContext(), bindingClass, parent, false);
        if (viewBinding == null){
            throw new RuntimeException("不能初始化视图: "+ bindingClass.getCanonicalName());
        }
        return new ItemViewHolder(viewBinding.getRoot(), viewBinding);
    }

    @Override
    public final void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        onBindView(holder.mViewBinding, getItemData(position), position);
    }

    public T getItemData(int position){
        return mDataList.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    protected static class ItemViewHolder extends RecyclerView.ViewHolder {
        protected final ViewBinding mViewBinding;
        public ItemViewHolder(@NonNull View itemView, ViewBinding vb) {
            super(itemView);
            this.mViewBinding = vb;
        }
    }
}
