package cn.chinaunicom.monitor.mainframe;

import android.content.Intent;
import android.graphics.Color;
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
import cn.chinaunicom.monitor.adapters.MainframeListAdapter;
import cn.chinaunicom.monitor.beans.CateEntity;
import cn.chinaunicom.monitor.beans.EnvirEntity;
import cn.chinaunicom.monitor.beans.MainframeEntity;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.BaseReq;
import cn.chinaunicom.monitor.http.Request.MainframeListReq;
import cn.chinaunicom.monitor.http.Response.EnviAndCateResp;
import cn.chinaunicom.monitor.http.Response.MainframeListResp;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;

public class MainframeListActivity extends BaseActivity{

    @Bind(R.id.imgBtnLeft)
    ImageButton backBtn;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @Bind(R.id.envirSpinner)
    Spinner envirSpinner;

    @Bind(R.id.categorySpinner)
    Spinner categorySpinner;

    @OnClick(R.id.imgBtnLeft)
    void back() {
        MainframeListActivity.this.finish();
    }

    @Bind(R.id.imgBtnRight)
    ImageButton refreshBtn;

    @Bind(R.id.mainframeList)
    ListView mainframeList;

    @OnClick(R.id.imgBtnRight)
    void refresh() {
        if (null != curCategory && null != curEnvironment && !mainframeListTaskRunning) {
            startMainframeListTask();
        }
    }

    private String curCenterItemId;
    private String curCenterItemTitle;
    private List<EnvirEntity> envirEntityList = new ArrayList<>();
    private List<CateEntity> cateEntityList = new ArrayList<>();
    private EnvirEntity curEnvironment;
    private CateEntity curCategory;
    public  List<MainframeEntity> mainframeEntities = new ArrayList<>();
    private MainframeListReq mainframeListReq = new MainframeListReq();
    private MainframeListAdapter mainframeListAdapter = new MainframeListAdapter(this, mainframeEntities);
    private EnvirSpinnerAdapter envirSpinnerAdapter = new EnvirSpinnerAdapter(envirEntityList);
    private CategorySpinnerAdapter categorySpinnerAdapter = new CategorySpinnerAdapter(cateEntityList);
    private boolean mainframeListTaskRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainframe_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        getImperativeData();
        initTitleBar();
        initSpinner();
        initList();
        startEnvirAndCateTask();
    }

    private void initList() {
        mainframeList.setAdapter(mainframeListAdapter);
        mainframeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMainframeDetailActivity(position);
            }
        });
    }

    private void startMainframeDetailActivity(int position) {
        Intent intent = new Intent(this, MainframeDetailActivity.class);
        intent.putExtra("HOST_IP", mainframeEntities.get(position).IP);
        startActivity(intent);
    }

    private void getImperativeData() {
        curCenterItemId = getIntent().getStringExtra("CUR_CENTER_ITEM_ID");
        curCenterItemTitle = getIntent().getStringExtra("CUR_CENTER_TITLE");
    }

    private void initSpinner() {
        envirSpinner.setAdapter(envirSpinnerAdapter);
        categorySpinner.setAdapter(categorySpinnerAdapter);

        envirSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (null != curEnvironment) {
                    curEnvironment.title = envirEntityList.get(position).title;
                    curEnvironment.itemId = envirEntityList.get(position).itemId;
                    startMainframeListTask();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (null != curCategory) {
                    curCategory.title = cateEntityList.get(position).title;
                    curCategory.itemId = cateEntityList.get(position).itemId;
                    startMainframeListTask();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow, null));
            refreshBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh, null));
        } else {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow));
            refreshBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh));
        }
        title.setText(curCenterItemTitle + "-主机情况");
    }

    private void startMainframeListTask() {
        if (!mainframeListTaskRunning) {
            mainframeListReq.appClassId = "";
            mainframeListReq.appHierarchyId = curCategory.itemId;
            mainframeListReq.appName = "";
            mainframeListReq.centerId = curCenterItemId;
            mainframeListReq.envirId = curEnvironment.itemId;
            mainframeListReq.userToken = ChinaUnicomApplication.token;
            MainframeListAsyncTask task = new MainframeListAsyncTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        }
    }


    private void startEnvirAndCateTask() {
        EnvirAndCateTask task = new EnvirAndCateTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    private class EnvirAndCateTask extends AsyncTask<Void, Void, EnviAndCateResp> {

        private BaseReq req;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new BaseReq();
            req.userToken = ChinaUnicomApplication.token;
        }

        @Override
        protected EnviAndCateResp doInBackground(Void... params) {
            EnviAndCateResp resp = new Http.Builder().create().getEnvirAndCateList(req);
            return resp;
        }

        @Override
        protected void onPostExecute(EnviAndCateResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                envirEntityList.clear();
                envirEntityList.addAll(resp.data.environment);
                cateEntityList.clear();
                cateEntityList.addAll(resp.data.category);
                envirSpinnerAdapter.notifyDataSetChanged();
                categorySpinnerAdapter.notifyDataSetChanged();
                if (!Utils.isListEmpty(envirEntityList) && !Utils.isListEmpty(cateEntityList))
                    initCurEnvirAndCate(envirEntityList.get(0), cateEntityList.get(0));
            } else
                Utils.showErrorToast(MainframeListActivity.this, Config.TOAST_REQUEST_FAILED);
        }
    }

    private void initCurEnvirAndCate(EnvirEntity ee, CateEntity ce) {
        if (null == curEnvironment) {
            curEnvironment = new EnvirEntity(ee.itemId, ee.title);
        } else {
            curEnvironment.itemId = ee.itemId;
            curEnvironment.title = ee.title;
        }

        if (null == curCategory) {
            curCategory = new CateEntity(ce.itemId, ce.title);
        } else {
            curCategory.itemId = ce.itemId;
            curCategory.title = ce.title;
        }

    }

    class SpinnerViewHolder {

        @Bind(R.id.spinnerItem)
        TextView spinnerItem;

        public SpinnerViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class EnvirSpinnerAdapter extends BaseAdapter {
        private List<EnvirEntity> items;
        public EnvirSpinnerAdapter(List<EnvirEntity> items) {
            this.items = items;
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
                convertView = View.inflate(MainframeListActivity.this, R.layout.my_simple_spinner_item, null);
                viewHolder = new SpinnerViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SpinnerViewHolder) convertView.getTag();
            }

            viewHolder.spinnerItem.setText(items.get(position).title);

            return convertView;
        }
    }

    private class CategorySpinnerAdapter extends BaseAdapter {
        private List<CateEntity> items;
        public CategorySpinnerAdapter(List<CateEntity> items) {
            this.items = items;
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
                convertView = View.inflate(MainframeListActivity.this, R.layout.my_simple_spinner_item, null);
                viewHolder = new SpinnerViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SpinnerViewHolder) convertView.getTag();
            }

            viewHolder.spinnerItem.setText(items.get(position).title);

            return convertView;
        }
    }

    public class MainframeListAsyncTask extends AsyncTask<Void, Void, MainframeListResp> {

        private LoadToast loadToast;

        public MainframeListAsyncTask() {

            this.loadToast = new LoadToast(MainframeListActivity.this);
            initLoadToast(this.loadToast);
        }

        private void initLoadToast(LoadToast loadToast) {
            loadToast.setProgressColor(Color.WHITE);
            loadToast.setTranslationY(Config.LOAD_TOAST_POS);
            loadToast.setBackgroundColor(Color.rgb(244,34,6));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            envirSpinner.setEnabled(false);
            categorySpinner.setEnabled(false);
            mainframeListTaskRunning = true;
            loadToast.show();
        }

        @Override
        protected MainframeListResp doInBackground(Void... params) {
            MainframeListResp resp = new Http.Builder().create().getMainframeList(mainframeListReq);
            return resp;
        }

        @Override
        protected void onPostExecute(MainframeListResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                if (!Utils.isListEmpty(resp.data.records)) {
                    mainframeEntities.clear();
                    for (int appKinds = 0; appKinds < resp.data.records.size(); appKinds++) {
                        mainframeEntities
                                .addAll(resp.data.records.get(appKinds).mainframeEntities);
                    }
                }
                mainframeListAdapter.notifyDataSetChanged();
                loadToast.success();
            } else {
                loadToast.error();
                Utils.showErrorToast(MainframeListActivity.this, Config.TOAST_REQUEST_FAILED);
            }
            envirSpinner.setEnabled(true);
            categorySpinner.setEnabled(true);
            mainframeListTaskRunning = false;
        }
    }
}
