import os
from FileInfoBean import *


g_FilePath = "D://filelist"


def getfilelist():
    lt = []

    fl = os.listdir(g_FilePath)

    for f in fl:
        lt.append(FileInfo(f, getFileSize(g_FilePath + "//" + f)))

    return lt


def getFileSize(filepath):
    return os.path.getsize(filepath)


def openFile(filename):
    # return open(g_FilePath + "//" + filename, "r", encoding="UTF-8")
    return open(g_FilePath + "//" + filename, "rb")


def closeFile(file):
    file.close()


if __name__ == '__main__':
    getfilelist()
