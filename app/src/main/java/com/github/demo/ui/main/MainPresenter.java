package com.github.demo.ui.main;

import java.util.Map;

class MainPresenter extends MainContract.Presenter {

    @Override
    void login(String username, String password) {
        mView.showLoading("登录中");
        mModel.login(username, password).subscribe(new HttpResponseObserver<Map<String, String>>(){
            @Override
            protected void onSuccess(Map<String, String> data) {
                mView.onLoginSuccess(data);
            }

            @Override
            protected void onFail(Throwable e) {
                mView.onLoginFail(e);
            }

            @Override
            public void onFinished() {
                mView.dismissLoading();
            }
        });
    }
}
