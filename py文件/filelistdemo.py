from socket import *
from threading import *
import json
import FileInfoBean


import FileUtil


# 接收命令socket
g_ActionSocket = None

g_FileSocket = None

# 接收文件socket


def postFile():
    while True:

        # 接收连接
        print("文件通道等待连接...")
        conn, addr = g_FileSocket.accept()
        print(addr, "文件通道建立")
        # 接收此连接发来的信息
        while True:
            print("等待文件名...")
            recvInfo = conn.recv(1024)

            # 收到消息进行解码处理
            recvDataDecodeStr = recvInfo.decode("utf-8")

            print('收到%r发来的消息：%s' % (addr, recvDataDecodeStr))

            if recvDataDecodeStr != "":
                # 收到的消息原封不动返回
                # conn.send((recvDataDecodeStr + "\n").encode("utf-8"))
                # conn.sendall(recvData)
                # print("消息返回完毕")

                if recvDataDecodeStr.startswith('get'):
                    filename = recvDataDecodeStr.split(" ")[1]
                    print("准备发送：" + filename)
                    currentFile = FileUtil.openFile(filename)
                    while True:
                        filedata = currentFile.read(1024 * 4)
                        if not filedata:
                            break
                        conn.send(filedata)
                    FileUtil.closeFile(currentFile)

            # 如果发过来的消息是'bb'服务端主动断开与addr的连接
                elif recvDataDecodeStr == 'bb':
                    print("文件连接断开", addr)
                    conn.close()
                    break


# 接收命令
def recvAction():
    while True:
        print('命令监听中....')
        data, addr = g_ActionSocket.recvfrom(1024)
        rd = data.decode("utf-8")
        print('接收命令：' + rd)

        if rd == "1":
            f = FileUtil.getfilelist()
            print("获得列表：")
            rdd = json.dumps(f, ensure_ascii=False,
                             cls=FileInfoBean.DateEncoder)
            print(rdd)
            # g_ActionSocket.sendto(f.encode('utf-8'), addr)
            g_ActionSocket.sendto(rdd.encode("utf-8"), addr)

        elif rd == 'bb':
            break
    print('客户端断开连接')
    g_ActionSocket.close()


# 主方法
def main():
    global g_FileSocket
    global g_ActionSocket
    # 新建socket对象
    g_FileSocket = socket(AF_INET, SOCK_STREAM)
    # 绑定
    g_FileSocket.bind(("", 7777))
    # 监听
    g_FileSocket.listen(7)

    g_ActionSocket = socket(AF_INET, SOCK_DGRAM)
    # 监听本地接口
    g_ActionSocket.bind(("", 5000))
    # 开启线程
    tr = Thread(target=recvAction)
    # 开启线程，接收数据
    pf = Thread(target=postFile)
    # 新建线程
    tr.start()
    # 线程开启
    pf.start()
    # 主线程等待
    tr.join()
    # 主线程阻塞
    pf.join()


if __name__ == '__main__':
    main()
