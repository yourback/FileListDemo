package gjjzx.com.filelistdemo.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by PC on 2017/11/13.
 */

public class MyApplication extends Application {

    //是否打开打印开关
    public static final boolean isLog = true;
    public static final String DSTIP = "10.1.75.252";
    public static final int DSTPORT = 5000;

    //全局context
    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

    }


}
