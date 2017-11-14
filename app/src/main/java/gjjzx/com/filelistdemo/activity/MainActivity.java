package gjjzx.com.filelistdemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.lemonsoft.lemonbubble.LemonBubble;

import gjjzx.com.filelistdemo.R;
import gjjzx.com.filelistdemo.utils.FileUtil;
import gjjzx.com.filelistdemo.utils.LogUtil;
import gjjzx.com.filelistdemo.utils.ToastUtil;

public class MainActivity extends BaseActivity {
    private static final int WAITING = 10000;
    private static final int INITFINISH = 10001;
    //题头部分
    private TextView tv_title;
    private ImageView iv_titleRight;

    //列表部分
    private RecyclerView rv;
    private RVAdapter rvAdapter;

    //无文件布局
    private LinearLayout linearLayout_nopic;

    //文件列表list.
    private String[] fileNames;


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
                default:
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置控件
        findView();
        //设置需要的监听
        initListener();
        //数据初始化，大部分数据都需要开线程去取
        initData();
    }

    private void initListener() {
        LogUtil.INSTANCE.e("initListener");
        FileUtil.setListener(new FileUtil.FileUtilListener() {
            @Override
            public void getFileList(String[] strings) {
                //获得文件列表后的回调方法
                //刷新列表就行
                fileNames = strings;
                rvAdapter.refreshFileNameList(fileNames);
                Message msg = new Message();
                msg.what = INITFINISH;
                msg.obj = "刷新文件列表完毕";
                UIhandler.sendEmptyMessage(INITFINISH);
            }
        });
    }

    //页面初始化
    private void initView() {
        LogUtil.INSTANCE.e("initView");
        if (fileNames.length == 0) {
            tv_title.setText("没有文件");
            linearLayout_nopic.setVisibility(View.VISIBLE);
        } else {
            tv_title.setText("文件列表（" + fileNames.length + "）");
            linearLayout_nopic.setVisibility(View.GONE);
        }
    }


    private void findView() {
        LogUtil.INSTANCE.e("findView");
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
                ToastUtil.INSTANCE.show("文件同步");
            }
        });
    }


    //开线程初始化文件列表
    private void initData() {
        LogUtil.INSTANCE.e("initData");
        //开启等待
        Message msg = new Message();
        msg.what = WAITING;
        msg.obj = "文件列表初始化中...";
        UIhandler.sendMessage(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtil.getFileList();
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
}
