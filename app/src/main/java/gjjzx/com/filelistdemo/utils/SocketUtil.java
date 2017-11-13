package gjjzx.com.filelistdemo.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import gjjzx.com.filelistdemo.app.MyApplication;

/**
 * Created by PC on 2017/11/13.
 */

public class SocketUtil {
    private DatagramSocket sendSocket;


    public SocketUtil() {
    }

    private void SocketControl(boolean b) {
        if (b) {
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
                } catch (Exception e) {
                    LogUtil.INSTANCE.e("发送数据出错");
                    LogUtil.INSTANCE.e(e);
                    if (listener != null) {
                        listener.onSendDataFail();
                    }
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
    }


}
