package app.personal.MVVM.Application;

import android.app.Application;
import android.os.StrictMode;

import app.personal.fury.BuildConfig;

public class FuryApp extends Application {

    public FuryApp() {
        if(BuildConfig.DEBUG)
//            Enabling logcat strict mode.
            StrictMode.enableDefaults();
    }
}
