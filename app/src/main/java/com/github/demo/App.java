package com.github.demo;

import android.app.Application;
import android.content.pm.ActivityInfo;

import com.ihsanbal.logging.Level;
import com.github.voisen.libmvp.base.BaseActivity;
import com.github.voisen.libmvp.network.RetrofitCreator;
import com.github.voisen.libmvp.network.TrustEveryoneManager;
import com.github.voisen.libmvp.widget.ProgressHUB;

public class App extends Application {

    private final static String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        ProgressHUB.setMode(ProgressHUB.Mode.DARK);
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
    }
}
