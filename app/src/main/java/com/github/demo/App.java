package com.github.demo;

import android.app.Application;
import android.content.pm.ActivityInfo;

import com.github.voisen.libmvp.AppProfile;
import com.github.voisen.libmvp.widget.TinyToast;
import com.ihsanbal.logging.Level;
import com.github.voisen.libmvp.base.BaseActivity;
import com.github.voisen.libmvp.network.RetrofitCreator;
import com.github.voisen.libmvp.network.TrustEveryoneManager;

public class App extends Application {

    private final static String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        AppProfile.setMode(AppProfile.Mode.LIGHT);
        TrustEveryoneManager manager = new TrustEveryoneManager();
        try {
            RetrofitCreator.getOkhttpBuilder()
                    .sslSocketFactory(manager.getSocketFactory(), manager)
                    .hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BaseActivity.setDefaultScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RetrofitCreator.init(Level.BASIC, "http://www.baidu.com");
//        new ProgressHUB(this)
//                .showMessageWithStatus(ProgressHUB.Status.SUCCESS, "来了啊", 3000);
        TinyToast.showMessage(AppProfile.Status.SUCCESS, "应用启动成功", 1200);
    }
}
