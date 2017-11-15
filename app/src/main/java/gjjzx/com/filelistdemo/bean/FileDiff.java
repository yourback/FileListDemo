package gjjzx.com.filelistdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PC on 2017/11/15.
 */

public class FileDiff extends FileInfo implements Parcelable {
    private boolean add;
    private boolean del;
    private long oldSize;

    public FileDiff(String fileName, long fileSize, boolean add, boolean del, long oldSize) {
        super(fileName, fileSize);
        this.add = add;
        this.del = del;
        this.oldSize = oldSize;
    }

    public FileDiff(FileInfo fi, boolean add, boolean del, long oldSize) {
        super(fi.getFileName(), fi.getFileSize());
        this.add = add;
        this.del = del;
        this.oldSize = oldSize;
    }

    public FileDiff(Parcel in, boolean add, boolean del, long oldSize) {
        super(in);
        this.add = add;
        this.del = del;
        this.oldSize = oldSize;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public long getOldSize() {
        return oldSize;
    }

    public void setOldSize(long oldSize) {
        this.oldSize = oldSize;
    }

    @Override
    public String toString() {
        return "FileDiff{" +
                "add=" + add +
                ", del=" + del +
                ", oldSize=" + oldSize +
                '}';
    }
}
