package gjjzx.com.filelistdemo.diy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gjjzx.com.filelistdemo.R;
import gjjzx.com.filelistdemo.bean.FileDiff;

/**
 * Created by PC on 2017/11/15.
 */

class DialogFragment_SyncList_Adapter extends RecyclerView.Adapter<DialogFragment_SyncList_Adapter.ViewHolder> {

    private List<FileDiff> fileDiffList = new ArrayList<>();

    public void refreshList(List<FileDiff> fl) {
        fileDiffList = fl;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialogfragment_synclist_itemview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FileDiff fileDiff = fileDiffList.get(position);
        holder.tv_filename.setText(fileDiff.getFileName());
        if (fileDiff.isDel()) {
//            holder.tv_status.setTextColor(Color.RED);
            holder.tv_status.setText("删除");
        } else if (fileDiff.isAdd()) {
//            holder.tv_status.setTextColor(Color.RED);
            holder.tv_status.setText("新增");
        } else {
//            holder.tv_status.setTextColor(Color.BLACK);
            holder.tv_status.setText(fileDiff.getOldSize() + "字节 → " + fileDiff.getFileSize() + "字节");
        }
    }

    @Override
    public int getItemCount() {
        return fileDiffList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_filename, tv_status;


        public ViewHolder(View itemView) {
            super(itemView);
            tv_filename = itemView.findViewById(R.id.tv_dfsynclist_filename);
            tv_status = itemView.findViewById(R.id.tv_dfsynclist_status);
        }
    }

}
