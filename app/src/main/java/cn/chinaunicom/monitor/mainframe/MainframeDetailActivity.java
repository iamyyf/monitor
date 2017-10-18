package cn.chinaunicom.monitor.mainframe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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
import cn.chinaunicom.monitor.adapters.MainframeDetailAdapter;
import cn.chinaunicom.monitor.beans.ItemEntity;
import cn.chinaunicom.monitor.beans.ItemInfo;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.MainframeDetailReq;
import cn.chinaunicom.monitor.http.Response.MainframeDetailResp;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;

public class MainframeDetailActivity extends BaseActivity {

    @Bind(R.id.imgBtnLeft)
    ImageButton backBtn;

    @Bind(R.id.imgBtnRight)
    ImageButton refreshBtn;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @Bind(R.id.mainframeDetailList)
    ListView detailList;

    @Bind(R.id.monitorSpinner)
    Spinner monitorSpinner;

    @OnClick(R.id.imgBtnLeft)
    void back() {
        MainframeDetailActivity.this.finish();
    }

    @OnClick(R.id.imgBtnRight)
    void refresh() {
        if (!isTaskRunning) {
            startMainframeDetailTask();
        }
    }

    private String ip;
    private List<ItemInfo> itemInfoList = new ArrayList<>();
    private List<ItemEntity> itemEntities = new ArrayList<>();
    private boolean isTaskRunning = false;
    private MainframeDetailReq mainframeDetailReq = new MainframeDetailReq();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainframe_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        getImperativeData();
        initTitleBar();
        initSpinner();
        initList();
        startMainframeDetailTask();
    }

    private void getImperativeData() {
        ip = getIntent().getStringExtra("HOST_IP");
    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow, null));
            refreshBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh, null));
        } else {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow));
            refreshBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh));
        }
        title.setText(ip + "-详情");
    }

    private void initList() {
        detailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(new Intent(MainframeDetailActivity.this, ChartListActivity.class));
                intent.putExtra("Monitor_Item", Config.MAINFRAME_DETAIL);
                intent.putExtra("PARAM1", itemEntities.get(position).itemid+"");
                startActivity(intent);
            }
        });
    }

    private void initSpinner() {
        monitorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                itemEntities.clear();
                itemEntities.addAll(itemInfoList.get(position).itemInfos);
                detailList.setAdapter(new MainframeDetailAdapter(MainframeDetailActivity.this, itemEntities));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void startMainframeDetailTask() {
        mainframeDetailReq.host = ip;
        mainframeDetailReq.userToken = ChinaUnicomApplication.token;
        //TODO:测试用IP，有内容
        //mainframeDetailReq.host = "10.191.17.1";
        MainframeDetailTask task = new MainframeDetailTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    private class MainframeDetailTask extends AsyncTask<Void, Void, MainframeDetailResp> {
        private LoadToast loadToast = new LoadToast(MainframeDetailActivity.this);;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.initLoadToast(loadToast);
            isTaskRunning = true;
            loadToast.show();
        }

        @Override
        protected MainframeDetailResp doInBackground(Void... voids) {
            MainframeDetailResp resp = new Http.Builder().create().getMainframeDetail(mainframeDetailReq);
            return resp;
        }

        @Override
        protected void onPostExecute(MainframeDetailResp resp) {
            super.onPostExecute(resp);
            itemInfoList.clear();
            itemEntities.clear();
            if (Utils.isRequestSuccess(resp)) {
                loadToast.success();
                if (!Utils.isListEmpty(resp.data.records)) {
                    itemInfoList.addAll(resp.data.records);
                    monitorSpinner.setAdapter(new SpinnerAdapter(itemInfoList));
                }
            } else {
                loadToast.error();
                Utils.showErrorToast(MainframeDetailActivity.this, Config.TOAST_REQUEST_FAILED);
            }
            isTaskRunning = false;
        }
    }

    class SpinnerViewHolder {

        @Bind(R.id.spinnerItem)
        TextView spinnerItem;

        public SpinnerViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class SpinnerAdapter extends BaseAdapter {
        private List<ItemInfo> items;

        public SpinnerAdapter() {
            this.items = new ArrayList<>();
        }

        public SpinnerAdapter(List<ItemInfo> items) {
            this.items = new ArrayList<>(items);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpinnerViewHolder viewHolder;
            if (null == convertView) {
                convertView = View.inflate(MainframeDetailActivity.this, R.layout.my_simple_spinner_item, null);
                viewHolder = new SpinnerViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SpinnerViewHolder) convertView.getTag();
            }

            viewHolder.spinnerItem.setText(items.get(position).name);

            return convertView;
        }
    }
}
