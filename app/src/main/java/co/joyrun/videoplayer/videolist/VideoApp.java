package co.joyrun.videoplayer.videolist;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by keven-liang on 2017/12/7.
 */

public class VideoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
