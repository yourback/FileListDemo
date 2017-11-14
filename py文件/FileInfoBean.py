import json


class FileInfo(json.JSONEncoder):

    def __init__(self, fileName, FileSize):
        super(FileInfo, self).__init__()
        self.fileName = fileName
        self.FileSize = FileSize

    def __str__(self):
        return {"fileName": self.fileName, "FileSize": self.FileSize}


class DateEncoder(json.JSONEncoder):

    def default(self, obj):
        if isinstance(obj, FileInfo):
            return obj.__str__()
        return json.JSONEncoder.default(self, obj)
