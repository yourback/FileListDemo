package gjjzx.com.filelistdemo.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by PC on 2017/11/13.
 */

public class FileUtil {
    //文件读取与写入
    private static final String PATH = Environment.getExternalStorageDirectory() + "/file";
//    private static final String PATH = "/";

    //获得文件夹下的所有那文件名称
    public static void getFileList() {
        LogUtil.INSTANCE.e("获得所有文件名称");
        File file = new File(PATH);
        //如果文件不存在则新建
        if (!file.exists())
            file.mkdirs();
        //获得list的文件列表
        if (listener != null) {
            String[] list = file.list();
            if (list == null){
                LogUtil.INSTANCE.e("yes");
            }
            listener.getFileList(list);
        }
    }

    //接口
    public interface FileUtilListener {
        void getFileList(String[] strings);

    }

    private static FileUtilListener listener;

    public static void setListener(FileUtilListener listener) {
        FileUtil.listener = listener;
    }
}

