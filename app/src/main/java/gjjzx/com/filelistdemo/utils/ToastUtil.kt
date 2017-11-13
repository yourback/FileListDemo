package gjjzx.com.filelistdemo.utils

import android.widget.Toast

import gjjzx.com.filelistdemo.app.MyApplication

/**
 * Created by PC on 2017/11/13.
 */

object ToastUtil {

    private var toast: Toast? = null

    fun show(o: Any) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getContext(), o.toString(), Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(o.toString())
        }
        toast!!.show()
    }
}
