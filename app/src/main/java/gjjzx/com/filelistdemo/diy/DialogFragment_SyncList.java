package gjjzx.com.filelistdemo.diy;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import gjjzx.com.filelistdemo.R;
import gjjzx.com.filelistdemo.bean.FileDiff;

/**
 * Created by PC on 2017/11/15.
 */

public class DialogFragment_SyncList extends DialogFragment {

    private Button submit, cancel;

    private RecyclerView rv;
    private DialogFragment_SyncList_Adapter adapter;

    private List<FileDiff> fileDiffList = new ArrayList<>();

    onClickListener listener;

    public DialogFragment_SyncList() {
    }

    public void show(FragmentManager transaction, List<FileDiff> fdl) {
        fileDiffList = fdl;
        show(transaction, "DialogFragment_SyncList");
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //消失title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //透明，配合background使用
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //点击外部不消失
        getDialog().setCanceledOnTouchOutside(false);

        listener = (onClickListener) getActivity();

        View v = inflater.inflate(R.layout.dialogfragment_synclist, null);

        submit = v.findViewById(R.id.dialogfragment_synclist_submit);
        cancel = v.findViewById(R.id.dialogfragment_synclist_cancel);
        rv = v.findViewById(R.id.dialogfragment_synclist_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DialogFragment_SyncList_Adapter();
        rv.setAdapter(adapter);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSubmitClick(fileDiffList);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;

    }

    public interface onClickListener {
        void onSubmitClick(List<FileDiff> fileDiffList);
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.refreshList(fileDiffList);

//        Dialog dialog = getDialog();
//        if (dialog != null) {
//            DisplayMetrics dm = new DisplayMetrics();
//            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
////            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.widthPixels * 0.8));
//            adapter.refreshList(fileDiffList);
//        }
    }
}
