package gjjzx.com.filelistdemo.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gjjzx.com.filelistdemo.activity.MainActivity;
import gjjzx.com.filelistdemo.app.MyApplication;
import gjjzx.com.filelistdemo.bean.FileDiff;

/**
 * Created by PC on 2017/11/13.
 */

public class SocketUtil {
    private DatagramSocket sendSocket;

    private Handler suHandler;

    public SocketUtil(Handler handler) {
        suHandler = handler;
    }

    private void SocketControl(boolean b) {
        if (b) {
            LogUtil.INSTANCE.e("开");
            //开
            try {
                sendSocket = new DatagramSocket();
                LogUtil.INSTANCE.e("新建发送接口成功");
            } catch (SocketException e) {
                LogUtil.INSTANCE.e(e);
                LogUtil.INSTANCE.e("新建发送接口出错");
                if (listener != null) {
                    listener.onBuildSendSocketFail();
                }
            }
        } else {
            //关
            if (sendSocket != null && !sendSocket.isClosed()) {
                sendSocket.close();
            }
        }
    }

    //发送
    public void postData(final String data) {

        //开线程发送
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sendSocket == null || sendSocket.isClosed())
                        SocketControl(true);

                    //server ip
                    InetAddress serverAddr = InetAddress.getByName(MyApplication.DSTIP);
                    LogUtil.INSTANCE.e("发送数据：" + data);
                    LogUtil.INSTANCE.e("发送地址：" + MyApplication.DSTIP + ":" + MyApplication.DSTPORT);
                    //发送数据转换成字符数组
                    byte[] bytes = data.getBytes();
                    DatagramPacket dp = new DatagramPacket(bytes, bytes.length, serverAddr, MyApplication.DSTPORT);
                    //发送
                    sendSocket.send(dp);
                    if (listener != null)
                        listener.onDataSending();

                } catch (Exception e) {
                    LogUtil.INSTANCE.e("发送数据出错");
                    LogUtil.INSTANCE.e(e);
                    if (listener != null) {
                        listener.onSendDataFail();
                    }
                }

                //接收返回值
                byte[] buffer = new byte[1024];
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                try {
                    //设置超时时间,3秒
                    sendSocket.setSoTimeout(5000);
                    sendSocket.receive(inPacket);

                    String result = new String(inPacket.getData(), inPacket.getOffset(),
                            inPacket.getLength());

                    if (listener != null)
                        listener.onReturnDataSuccess(result);
                } catch (Exception e) {
                    //超时处理
                    LogUtil.INSTANCE.e("服务器无应答，连接断开");
                    SocketControl(false);
                    if (listener != null)
                        listener.onReturnDataOutTime();
                }


            }
        }).start();
    }

    private onSocketListener listener;

    public void setListener(onSocketListener listener) {
        this.listener = listener;
    }

    public interface onSocketListener {
        //建立发送socket失败
        void onBuildSendSocketFail();

        //发送数据失败
        void onSendDataFail();

        //发送数据后，成功获得返回值
        void onReturnDataSuccess(String rd);

        //发送数据后，接收不到服务器返回值，超时
        void onReturnDataOutTime();

        //正在发送数据
        void onDataSending();
    }

    //获得文件
    public void getFiles(final List<FileDiff> fileDiffList) {
        //开启监听
        // Activity按钮事件中
        new GetLogTask().execute(fileDiffList);
    }

    //    ----------------------------------------以上是sockt udp 命令------------------------------------------------------
    //    ----------------------------------------以下是利用socket tcp 传输文件------------------------------------------------------

    // Activity类中嵌套类
    public class GetLogTask extends AsyncTask<List<FileDiff>, String, String> {
        @Override
        protected String doInBackground(List<FileDiff>... param) {
            List<FileDiff> fileDiffList = param[0];

            try {
                Socket s = new Socket(MyApplication.DSTIP, 7777);

                InputStream inputStream = s.getInputStream();

                final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));


                for (final FileDiff fd : fileDiffList) {

                    //通知界面改变    正在同步 xxx文件
                    Message m = new Message();
                    m.what = MainActivity.FILESYNCING;
                    m.obj = fd.getFileName();
                    suHandler.sendMessage(m);

                    if (fd.isDel()) {
                        FileUtil.delFile(fd.getFileName());
                    } else if (fd.getFileSize() != fd.getOldSize() || fd.isAdd()) {



                        LogUtil.INSTANCE.e("操作文件：" + fd.getFileName());

                        //新建文件
                        File file = new File(FileUtil.PATH + "/" + fd.getFileName());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        LogUtil.INSTANCE.e("新建文件输入输出流");
                        FileOutputStream fos = new FileOutputStream(file, false);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        //在这里不规定长度，否则文件会长度出问题
//                        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024 * 4);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    LogUtil.INSTANCE.e("向服务器发送 get " + fd.getFileName() + " 命令");
                                    out.write("get " + fd.getFileName());
                                    out.flush();
                                } catch (IOException e) {
                                    LogUtil.INSTANCE.e("out 失败");
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);

                        byte buffer[] = new byte[1024 * 4];
                        int temp = 0;
                        LogUtil.INSTANCE.e("准备接收文件");
                        // 从InputStream当中读取客户端所发送的数据
                        //记录文件总长度
                        long filelength = 0;
                        while ((temp = inputStream.read(buffer)) != -1) {
                            LogUtil.INSTANCE.e("接收文件包");
                            bos.write(buffer, 0, temp);
                            filelength += temp;
                            if (filelength == fd.getFileSize())
                                break;
                        }
                        LogUtil.INSTANCE.e(fd.getFileName() + "传输完毕");
                        bos.close();
                    }
                }
                LogUtil.INSTANCE.e("全部文件传输完毕");


                //关闭向服务器写的输出流
                out.write("bb");
                out.flush();
                out.close();
                //关闭服务器数据传入流
                inputStream.close();
                //关闭连接
                s.close();
                LogUtil.INSTANCE.e("关闭流");

                //所有同步完毕
                suHandler.sendEmptyMessage(MainActivity.FILESYNCEND);
            } catch (Exception ex) {
                LogUtil.INSTANCE.e(ex.toString());
                ex.printStackTrace();
            }


            return "";
        }
    }

}
