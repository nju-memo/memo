package edu.nju.memo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import edu.nju.memo.manager.ViewManager;


public class StartFloatBallService extends Service {

    private static final String TAG = "StartFloatBallService";

    public StartFloatBallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service");
        ViewManager manager = ViewManager.getInstance(this);
        manager.showFloatBall();
        manager.getFloatBall().setVisibility(View.VISIBLE);
    }
}
