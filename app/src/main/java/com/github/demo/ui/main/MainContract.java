package com.github.demo.ui.main;

import com.github.voisen.libmvp.base.BaseModel;
import com.github.voisen.libmvp.base.BasePresenter;
import com.github.voisen.libmvp.base.BaseView;
import com.github.voisen.libmvp.utils.Optional;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public interface MainContract {

    interface Model extends BaseModel{
        Observable<Optional<Map<String, String>>> login(String username, String password);
    }

    interface View extends BaseView{
        void onLoginSuccess(Map<String, String> data);
        void onLoginFail(Throwable e);
    }

    abstract class Presenter extends BasePresenter<MainModel, View>{
        abstract void login(String username, String password);
    }
}
