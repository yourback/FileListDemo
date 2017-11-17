package gjjzx.com.filelistdemo.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gjjzx.com.filelistdemo.R;

/**
 * Created by PC on 2017/11/13.
 */

class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<String> fileNameList = new ArrayList<>();

    public RVAdapter() {
    }

    public void refreshFileNameList(List<String> strings) {
        if (strings == null) return;
        fileNameList = strings;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_adapter_fileName.setText(fileNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_adapter_fileName;

        ViewHolder(View itemView) {
            super(itemView);
            tv_adapter_fileName = itemView.findViewById(R.id.tv_item);
        }
    }
}
