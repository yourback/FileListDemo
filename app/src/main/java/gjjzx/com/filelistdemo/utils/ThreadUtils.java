package gjjzx.com.filelistdemo.utils;

import android.os.Looper;

public class ThreadUtils {

    public static final String TAG = "ThreadUtils";

    public static boolean isInMainThread() {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        return myLooper == mainLooper;
    }

}