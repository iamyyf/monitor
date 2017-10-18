package cn.chinaunicom.monitor.mainframe;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.BaseActivity;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.adapters.MainframeListAdapter;
import cn.chinaunicom.monitor.beans.MainframeEntity;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.MainframeListReq;
import cn.chinaunicom.monitor.http.Response.MainframeListResp;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;

/**
 * Created by yfyang on 2017/8/15.
 */

public class AppMainframeActivity extends BaseActivity {

    @Bind(R.id.mainframeList)
    ListView mainframeList;

    @Bind(R.id.imgBtnLeft)
    ImageButton backBtn;

    @Bind(R.id.imgBtnRight)
    ImageButton refreshBtn;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @OnClick(R.id.imgBtnRight)
    void refresh() {
        if (!isMainframeListTaskRunning) {
            startMainframeListTask();
        }
    }

    @OnClick(R.id.imgBtnLeft)
    void back() {
        AppMainframeActivity.this.finish();
    }
    private String curCenterItemId;
    private String curCenterItemTitle;
    private String curAppClassId;
    private String curAppHierarchyId;
    private String curAppName;
    private boolean isMainframeListTaskRunning = false;
    private List<MainframeEntity> mainframeEntities = new ArrayList<>();
    private MainframeListReq mainframeListReq = new MainframeListReq();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_mainframe);
        ButterKnife.bind(this);
        initView();
    }
    @Override
    public void initView() {
        getImperativeData();
        initTitleBar();
        initListView();
        startMainframeListTask();
    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow, null));
            refreshBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh, null));
        } else {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow));
            refreshBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh));
        }
        title.setText(curAppName + "主机部署情况");
    }

    private void initListView() {
        mainframeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startMainframeDetailActivity(position);
            }
        });
    }

    private void startMainframeDetailActivity(int position) {
        Intent intent = new Intent(this, MainframeDetailActivity.class);
        intent.putExtra("HOST_IP", mainframeEntities.get(position).IP);
        startActivity(intent);
    }

    private void startMainframeListTask() {
        mainframeListReq.appClassId = curAppClassId;
        mainframeListReq.appHierarchyId = curAppHierarchyId;
        mainframeListReq.appName = curAppName;
        mainframeListReq.centerId = curCenterItemId;
        mainframeListReq.envirId = "4"; //生产环境
        mainframeListReq.userToken = ChinaUnicomApplication.token;
        MainframeListTask task = new MainframeListTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    private void getImperativeData() {
        curCenterItemId = getIntent().getStringExtra("CUR_CENTER_ITEM_ID");
        curCenterItemTitle = getIntent().getStringExtra("CUR_CENTER_TITLE");
        curAppClassId = getIntent().getStringExtra("APP_CLASS_ID");
        curAppHierarchyId = getIntent().getStringExtra("APP_HIERARCHY_ID");
        curAppName = getIntent().getStringExtra("APP_NAME");
    }

    private class MainframeListTask extends AsyncTask<Void, Void, MainframeListResp> {

        private LoadToast loadToast;

        public MainframeListTask() {
            loadToast = new LoadToast(AppMainframeActivity.this);
            initLoadToast(loadToast);
        }

        private void initLoadToast(LoadToast loadToast) {
            loadToast.setProgressColor(Color.WHITE);
            loadToast.setTranslationY(Config.LOAD_TOAST_POS);
            loadToast.setBackgroundColor(Color.rgb(244,34,6));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isMainframeListTaskRunning = true;
            loadToast.show();
        }

        @Override
        protected MainframeListResp doInBackground(Void... voids) {
            MainframeListResp resp = new Http.Builder().create().getMainframeList(mainframeListReq);
            return resp;
        }

        @Override
        protected void onPostExecute(MainframeListResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                loadToast.success();
                if (!Utils.isListEmpty(resp.data.records)
                        && !Utils.isListEmpty(resp.data.records.get(0).mainframeEntities)){
                    mainframeEntities.clear();
                    mainframeEntities.addAll(resp.data.records.get(0).mainframeEntities);
                } else {
                    Utils.showWarningToast(AppMainframeActivity.this, "请求成功，但是没有任何主机信息");
                }
                mainframeList.setAdapter(new MainframeListAdapter(AppMainframeActivity.this, mainframeEntities));
            } else {
                loadToast.error();
                Utils.showErrorToast(AppMainframeActivity.this, Config.TOAST_REQUEST_FAILED);
            }
            isMainframeListTaskRunning = false;
        }
    }
}
