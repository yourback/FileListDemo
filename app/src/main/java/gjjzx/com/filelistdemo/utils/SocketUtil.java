package gjjzx.com.filelistdemo.utils;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.DataInputStream;
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

import gjjzx.com.filelistdemo.app.MyApplication;
import gjjzx.com.filelistdemo.bean.FileDiff;

/**
 * Created by PC on 2017/11/13.
 */

public class SocketUtil {
    private DatagramSocket sendSocket;


    public SocketUtil() {
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
        GetLogTask task = new GetLogTask();
        task.execute(fileDiffList);
    }


    //    ----------------------------------------以上是sockt udp 命令------------------------------------------------------
    //    ----------------------------------------以下是利用socket tcp 传输文件------------------------------------------------------
//    private Socket socket;
//    private InetSocketAddress isa;
//
//    private BufferedWriter out;
//
//    private BufferedReader in;
//
//    //持续监听服务器是否发来数据
//    private class ReadThread extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            try {
//                //socket设置
//                LogUtil.INSTANCE.e("接收文件socket开启");
//                socket = new Socket();
//                isa = new InetSocketAddress(MyApplication.DSTIP, MyApplication.DSTPORT);
//                socket.connect(isa, 5000);
//                LogUtil.INSTANCE.e("接收文件out开启");
//                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                LogUtil.INSTANCE.e("接收文件in开启");
//                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//                while (true){
//                    String data = in.readLine();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//
//
//            //监听服务器返回数据
//            while (true) {
//                String data = recevieData();
//                //data 大于1说明接收到数据了
//                //如果data = null 说明socket 已经断开了
//                //socket 断开后，监听结束，下次连接后再启动
//                if (TextUtils.isEmpty(data)) {
//                    if (connectedFail != null)
//                        connectedFail.failFunc();
//                    break;
//                } else if (data.equals("bb")) {
//                    MyApplication.isSocketConnected = false;
//                    break;
//                } else if (data.equals("sd")) {
//                    if (shutDownListener != null)
//                        shutDownListener.onShutDown();
//                } else if (data.equals("sp")) {
//                    if (stopPlayingListener != null) {
//                        stopPlayingListener.onStopPlaying();
//                    }
//                } else {
//                    //如果有返回的数据，说明点歌成功
//                    //就不需要显示服务器无响应的提示了
////                    MyApplication.isServerError = false;
//                    //点歌成功，需要在界面上显示
//                    Log.e(TAG, "点歌成功！服务器返回值：" + data);
//                    if (OrderSongListener != null)
//                        OrderSongListener.successFunc(data);
//                }
//            }
//        }
//    }


    // Activity类中嵌套类
    public class GetLogTask extends AsyncTask<List<FileDiff>, Void, String> {
        @Override
        protected String doInBackground(List<FileDiff>... param) {
            List<FileDiff> fileDiffList = param[0];
//            for (FileDiff fd : fileDiffList) {
//                postData("get " + fd.getFileName());
//            }
            final FileDiff fileDiff1 = fileDiffList.get(0);
            try {
                Socket s = new Socket(MyApplication.DSTIP, 7777);
                InputStream inputStream = s.getInputStream();
                DataInputStream input = new DataInputStream(inputStream);

                final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {

                            out.write("get " + fileDiff1.getFileName());
                            out.flush();
                        } catch (IOException e) {
                            LogUtil.INSTANCE.e("out 失败");
                            e.printStackTrace();
                        }
                    }
                }, 1000);

                byte[] b = new byte[10000];
                while (true) {
                    LogUtil.INSTANCE.e("监听中....");
                    int length = input.read(b);
                    String Msg = new String(b, 0, length, "gb2312");
                    LogUtil.INSTANCE.e(Msg);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }
    }

}
