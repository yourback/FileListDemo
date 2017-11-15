package gjjzx.com.filelistdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PC on 2017/11/14.
 */

public class FileInfo implements Parcelable {

    /**
     * fileName : 1.txt
     * FileSize : 3
     */

    private String fileName;
    private long FileSize;

    public FileInfo(String fileName, long fileSize) {
        this.fileName = fileName;
        FileSize = fileSize;
    }

    protected FileInfo(Parcel in) {
        fileName = in.readString();
        FileSize = in.readLong();
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return FileSize;
    }

    public void setFileSize(long fileSize) {
        FileSize = fileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fileName);
        parcel.writeLong(FileSize);
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", FileSize=" + FileSize +
                '}';
    }
}
