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

if __name__ == '__main__':
    getfilelist()
