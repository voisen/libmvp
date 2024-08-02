package com.github.demo.ui.recycler;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.github.demo.databinding.ActivityRecyclerBinding;
import com.github.demo.databinding.ItemRecyclerTestBinding;
import com.github.voisen.libmvp.base.BaseActivity;
import com.github.voisen.libmvp.base.BasePresenter;
import com.github.voisen.libmvp.widget.TinyRecyclerView;

public class RecyclerActivity extends BaseActivity<ActivityRecyclerBinding, BasePresenter> implements TinyRecyclerView.TinyAdapter<String> {

    @Override
    protected void onViewLoaded() {
        mViewBinding.recyclerView.setTinyAdapter(this);
        addTest();
    }

    private void addTest() {
        mHandler.postDelayed(this::addTest, 1000);
        int itemCount = mViewBinding.recyclerView.getItemCount();
        mViewBinding.recyclerView.addData(0, System.currentTimeMillis() + "-字符串");
        if (itemCount > 5){
            mViewBinding.recyclerView.remove(itemCount);
        }
    }

    @NonNull
    @Override
    public Class<? extends ViewBinding> getItemViewClass(int viewType) {
        return ItemRecyclerTestBinding.class;
    }

    @Override
    public void onItemViewBinding(@NonNull ViewBinding viewBinding, @NonNull String data, int position) {
        ItemRecyclerTestBinding vb = (ItemRecyclerTestBinding) viewBinding;
        vb.tvLeft.setText(data);
        vb.tvRight.setText(data + "Right");
    }
}