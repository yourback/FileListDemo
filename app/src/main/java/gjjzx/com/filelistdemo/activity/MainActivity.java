package gjjzx.com.filelistdemo.activity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.lemonsoft.lemonbubble.LemonBubble;

import java.lang.reflect.Type;
import java.util.List;

import gjjzx.com.filelistdemo.R;
import gjjzx.com.filelistdemo.bean.FileDiff;
import gjjzx.com.filelistdemo.bean.FileInfo;
import gjjzx.com.filelistdemo.diy.DialogFragment_SyncList;
import gjjzx.com.filelistdemo.utils.FileUtil;
import gjjzx.com.filelistdemo.utils.LogUtil;
import gjjzx.com.filelistdemo.utils.PermisionUtils;
import gjjzx.com.filelistdemo.utils.SocketUtil;
import gjjzx.com.filelistdemo.utils.ToastUtil;

public class MainActivity extends BaseActivity implements DialogFragment_SyncList.onClickListener {
    private static final int WAITING = 10000;
    private static final int INITFINISH = 10001;
    private static final int ERROR = 10002;
    private static final int NOTNEEDSYNC = 10003;
    private static final int NEEDSYNC = 10004;

    //    ------------------------------------------
    public static final int FILESYNCING = 11000;
    public static final int FILESYNCEND = 11001;

    private Handler UIhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case WAITING:
                    showWaiting((String) message.obj);
                    break;
                case INITFINISH:
                    initView();
                    showSuccess((String) message.obj);
                    break;
                case ERROR:
                    showError((String) message.obj);
                    break;
                case NOTNEEDSYNC:
                    showSuccess((String) message.obj);
                    break;
                case NEEDSYNC:
                    LemonBubble.hide();
                    //显示fragmentdialog 刷新列表
                    List<FileDiff> l = (List<FileDiff>) message.obj;
                    df_SyncList.show(getFragmentManager(), l);
                    break;

//                    --------------------------------以下是socketutil的用的提示--------------------------------------------
                case FILESYNCING:
                    showWaiting("正在同步 " + (String) message.obj);
                    break;

                case FILESYNCEND:
                    showSuccess("同步完毕");
                    initData();
                    break;

                default:
            }
            return false;
        }
    });


    //题头部分
    private TextView tv_title;
    private ImageView iv_titleRight;

    //列表部分
    private RecyclerView rv;
    private RVAdapter rvAdapter;

    //无文件布局
    private LinearLayout linearLayout_nopic;

    //文件列表list.
    private List<String> fileNames;

    //socket
    private SocketUtil su;

    //dialogfragment 同步列表
    private DialogFragment_SyncList df_SyncList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //权限问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermisionUtils.verifyStoragePermissions(this);
        }

        //设置控件
        findView();
        //dialogfragment初始化
        initDialogFragment();
        //设置需要的监听
        initListener();
        //数据初始化，大部分数据都需要开线程去取
        initData();
    }

    private void initDialogFragment() {
        df_SyncList = new DialogFragment_SyncList();
    }

    private void initListener() {
        LogUtil.INSTANCE.e("initListener");
        su.setListener(new SocketUtil.onSocketListener() {
            @Override
            public void onBuildSendSocketFail() {
                LogUtil.INSTANCE.e("socket建立出错");

            }

            @Override
            public void onSendDataFail() {
                LogUtil.INSTANCE.e("发送数据出错");
            }

            @Override
            public void onReturnDataSuccess(String rd) {
//                ToastUtil.INSTANCE.show("");
                //显示差多少文件
                LogUtil.INSTANCE.e(rd);
                Type type = new TypeToken<List<FileInfo>>() {
                }.getType();
                List<FileInfo> fileInfoList = new Gson().fromJson(rd, type);

                for (FileInfo fi : fileInfoList) {
                    LogUtil.INSTANCE.e("文件名称:" + fi.getFileName());
                    LogUtil.INSTANCE.e("文件大小:" + fi.getFileSize() + "字节");
                }

                List<FileDiff> fileDiffList = FileUtil.CheckoutFiles(fileInfoList);
                if (fileDiffList == null) {
                    //如果null，说明完全相同则不返回
                    Message m = new Message();
                    m.what = NOTNEEDSYNC;
                    m.obj = "文件相同，无需同步";
                    UIhandler.sendMessageDelayed(m, 1500);
                } else {
                    Message message = new Message();
                    message.what = NEEDSYNC;
                    message.obj = fileDiffList;
                    UIhandler.sendMessageDelayed(message, 1500);
                }
//                Message msg = new Message();
//                msg.what = WAITING;
//                msg.obj = "文件同步中...";
//                UIhandler.sendMessageDelayed(msg, 1500);
            }

            @Override
            public void onReturnDataOutTime() {
                Message msg = new Message();
                msg.what = ERROR;
                msg.obj = "服务器无响应";
                UIhandler.sendMessageDelayed(msg, 1500);
            }

            @Override
            public void onDataSending() {
                Message msg = new Message();
                msg.what = WAITING;
                msg.obj = "命令发送中...";
                UIhandler.sendMessage(msg);
            }
        });
    }

    //页面初始化
    private void initView() {
        LogUtil.INSTANCE.e("initView");
        if (fileNames.size() == 0) {
            tv_title.setText("没有文件");
            linearLayout_nopic.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tv_title.setText("文件列表（" + fileNames.size() + "）");
            linearLayout_nopic.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }

        //刷新列表就行
        rvAdapter.refreshFileNameList(fileNames);
    }


    private void findView() {
        LogUtil.INSTANCE.e("findView");
        //socket
        su = new SocketUtil(UIhandler);
        //题头
        tv_title = findViewById(R.id.title_tv);
        iv_titleRight = findViewById(R.id.title_sync);
        //recyclerview
        rv = findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rvAdapter = new RVAdapter();
        rv.setAdapter(rvAdapter);

        //nopic
        linearLayout_nopic = findViewById(R.id.nofile);


        iv_titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                su.postData("1");
            }
        });
    }


    //开线程初始化文件列表
    private void initData() {
        LogUtil.INSTANCE.e("initData");
        //开启等待
        Message msg = new Message();
        msg.what = WAITING;
        msg.obj = "文件列表刷新中...";
        UIhandler.sendMessage(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                fileNames = FileUtil.getFileList();
                //获得文件列表后的回调方法

                Message msg = new Message();
                msg.what = INITFINISH;
                msg.obj = "刷新文件列表完毕";
                UIhandler.sendMessageDelayed(msg, 2000);
            }
        }).start();
    }


    //显示等待
    private void showWaiting(String str) {
        LemonBubble.showRoundProgress(this, str);
    }


    //成功
    private void showSuccess(String obj) {
        LemonBubble.showRight(this, obj, 1500);
    }

    //失败
    private void showError(String obj) {
        LemonBubble.showError(this, obj, 1500);
    }


    //同步数据
    @Override
    public void onSubmitClick(List<FileDiff> fileDiffList) {
        df_SyncList.dismiss();
        ToastUtil.INSTANCE.show("确定同步");
        su.getFiles(fileDiffList);
    }


//    -------------------------------------以下为动态权限申请--------------------------------------------------------


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == PermisionUtils.REQUEST_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            ToastUtil.INSTANCE.show("缺少权限，APP无法运行");
            finish();
        } else {
            initData();
        }
    }
}
