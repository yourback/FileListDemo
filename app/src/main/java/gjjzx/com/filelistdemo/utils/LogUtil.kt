package gjjzx.com.filelistdemo.utils

import android.util.Log

import gjjzx.com.filelistdemo.app.MyApplication

/**
 * Created by PC on 2017/11/13.
 */

object LogUtil {
    fun e(o: Any, vararg tag: String) {
        if (MyApplication.isLog) {
            if (tag.isNotEmpty()) {
                Log.e(tag[0], o.toString())
            } else {
                Log.e("客户端", o.toString())
            }
        }
    }
}
