package gjjzx.com.filelistdemo.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by PC on 2017/11/13.
 */

public class FileUtil {
    //文件读取与写入
    private static final String PATH = Environment.getExternalStorageDirectory() + "/FileListDemo";
//    private static final String PATH = "/";

    //获得文件夹下的所有那文件名称
    public static void getFileList() {
        LogUtil.INSTANCE.e("获得所有文件名称");
        File file = new File(PATH);
        //如果文件不存在则新建
        if (!file.exists()) {
            LogUtil.INSTANCE.e("文件不存在，创建");
            file.mkdirs();
        } else {
            LogUtil.INSTANCE.e("文件已存在");
        }

        //获得list的文件列表
        if (listener != null) {
            String[] list = file.list();
            listener.getFileList(list);
        }
    }

    //根据文件名称新建文件
    public static void newFile(String fileName, int filelength) {
        File file = new File(PATH + "/" + fileName);
        if (!file.exists()) {
            //传送文件过来

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (file.length() != filelength) {
                file.delete();
                //传送文件过来
            }
        }
    }


    /**
     * 需要一个方法
     * 1.传入的是一个字典
     */





    //接口
    public interface FileUtilListener {
        void getFileList(String[] strings);

    }

    private static FileUtilListener listener;

    public static void setListener(FileUtilListener listener) {
        FileUtil.listener = listener;
    }
}

