package gjjzx.com.filelistdemo.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gjjzx.com.filelistdemo.bean.FileDiff;
import gjjzx.com.filelistdemo.bean.FileInfo;

/**
 * Created by PC on 2017/11/13.
 */

public class FileUtil {
    //文件读取与写入
    private static final String PATH = Environment.getExternalStorageDirectory() + "/FileListDemo";
//    private static final String PATH = "/";

    //获得文件夹下的所有那文件名称
    public static List<String> getFileList() {
        LogUtil.INSTANCE.e("获得所有文件名称");
        File file = new File(PATH);
        //如果文件不存在则新建
        if (!file.exists()) {
            LogUtil.INSTANCE.e("文件不存在，创建");
            file.mkdirs();
            return null;
        } else {
            LogUtil.INSTANCE.e("文件已存在");
        }
        return Arrays.asList(file.list());
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
     * 1.传入的list
     * 2.检查是否存在
     * 3.1 不存在建立
     * 3.2 存在且文件不相同则建立
     * 3.3 存在且文件大小的跳过
     */

    public static List<FileDiff> CheckoutFiles(List<FileInfo> fileInfoList) {
        List<FileDiff> fileDiffList = new ArrayList<>();

        //首先看删除的
        //以list形式返回文件名
        List<String> list = getFileList();
        if (list != null)
            outto:for (String str : list) {
                for (FileInfo fileInfo : fileInfoList) {
                    if (fileInfo.getFileName().equals(str))
                        continue outto;
                }
                fileDiffList.add(new FileDiff(str, 0, false, true, 0));
            }

        //看增加的和改变的
        for (FileInfo fi : fileInfoList) {
            File tempfile = new File(PATH + "/" + fi.getFileName());
            if (!tempfile.exists()) {
                fileDiffList.add(new FileDiff(fi, true, false, 0));
            } else {
                if (tempfile.length() != fi.getFileSize()) {
                    fileDiffList.add(new FileDiff(fi, false, false, tempfile.length()));
                }
            }
        }
        LogUtil.INSTANCE.e("需要替换的list");

        for (FileDiff d : fileDiffList)
            LogUtil.INSTANCE.e(d.toString());

        return fileDiffList.size() == 0 ? null : fileDiffList;
    }


}

