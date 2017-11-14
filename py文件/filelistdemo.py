from socket import *
from threading import *
import json
import FileInfoBean


import FileUtil


# 获得文件列表


g_socket = None

# 接收消息


def recvAction():
    while True:
        print('命令监听中....')
        data, addr = g_socket.recvfrom(1024)
        rd = data.decode("utf-8")
        print('接收命令：' + rd)

        if rd == "1":
            f = FileUtil.getfilelist()
            print("获得列表：")
            rdd = json.dumps(f, ensure_ascii=False,
                             cls=FileInfoBean.DateEncoder)
            print(rdd)
            # g_socket.sendto(f.encode('utf-8'), addr)
            g_socket.sendto(rdd.encode("utf-8"), addr)
        if rd == 'bb':
            break
    print('客户端断开连接')
    g_socket.close()


# 主方法
def main():
    global g_socket
    g_socket = socket(AF_INET, SOCK_DGRAM)
    # 监听本地接口
    g_socket.bind(("", 5000))
    # 开启线程
    tr = Thread(target=recvAction)
    # 新建线程
    tr.start()
    # 主线程等待
    tr.join()


if __name__ == '__main__':
    main()
