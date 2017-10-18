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
import cn.chinaunicom.monitor.adapters.DeployListAdapter;
import cn.chinaunicom.monitor.beans.AppbelongEntity;
import cn.chinaunicom.monitor.beans.DeployCategoryEntity;
import cn.chinaunicom.monitor.beans.AppEntity;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.BaseReq;
import cn.chinaunicom.monitor.http.Request.DeployListReq;
import cn.chinaunicom.monitor.http.Response.CateAndAppbelongResp;
import cn.chinaunicom.monitor.http.Response.DeployListResp;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;

public class DeployListActivity extends BaseActivity {

    @Bind(R.id.imgBtnLeft)
    ImageButton backBtn;

    @Bind(R.id.imgBtnRight)
    ImageButton refreshBtn;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @Bind(R.id.deployCateSpinner)
    Spinner categorySpinner;

    @Bind(R.id.appBelongsSpinner)
    Spinner appBelongsSpinner;

    @Bind(R.id.appClassSpinner)
    Spinner appClassSpinner;

    @Bind(R.id.appList)
    ListView appList;

    @OnClick(R.id.imgBtnRight)
    void refresh() {
        if (null != curDeployCategory && null != curAppBelong && !isDeployTaskRunning) {
            startDeployListTask();
        }
    }

    @OnClick(R.id.imgBtnLeft)
    void back() {
        DeployListActivity.this.finish();
    }

    private String curCenterItemId;
    private String curCenterItemTitle;
    private DeployCategoryEntity curDeployCategory;
    private AppbelongEntity curAppBelong;

    private List<DeployCategoryEntity> categoryList = new ArrayList<>(); //类别
    private List<AppbelongEntity> appBelongList = new ArrayList<>();
    public  List<AppEntity> appClassEntities = new ArrayList<>(); //应用大类
    public  List<AppEntity> appEntities = new ArrayList<>();         //当前应用集

    private DeployListReq deployListReq;

    private DeployListAdapter deployListAdapter = new DeployListAdapter(this, appEntities);
    private CategorySpinnerAdapter categorySpinnerAdapter = new CategorySpinnerAdapter(categoryList);
    private AppBelongsSpinnerAdapter appBelongsSpinnerAdapter = new AppBelongsSpinnerAdapter(appBelongList);
    private AppClassSpinnerAdapter appClassSpinnerAdapter = new AppClassSpinnerAdapter(appClassEntities);
    private boolean isDeployTaskRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        getImperativeData();
        initSpinner();
        initListView();
        initTitleBar();
        startDeployCateAndBelongsTask();
    }

    private void getImperativeData() {
        curCenterItemId = getIntent().getStringExtra("CUR_CENTER_ITEM_ID");
        curCenterItemTitle = getIntent().getStringExtra("CUR_CENTER_TITLE");
    }

    private void initListView() {
        appList.setAdapter(deployListAdapter);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startMainframeActivity(position);
            }
        });
    }

    private void startMainframeActivity(int position) {
        Intent intent = new Intent(this, AppMainframeActivity.class);
        intent.putExtra("CUR_CENTER_ITEM_ID", curCenterItemId);
        intent.putExtra("CUR_CENTER_TITLE", curCenterItemTitle);
        intent.putExtra("APP_CLASS_ID", appEntities.get(position).appClassId);
        intent.putExtra("APP_HIERARCHY_ID", appEntities.get(position).appHierarchyId);
        intent.putExtra("APP_NAME", appEntities.get(position).appName);
        startActivity(intent);

    }

    private void initSpinner() {
        categorySpinner.setAdapter(categorySpinnerAdapter);
        appBelongsSpinner.setAdapter(appBelongsSpinnerAdapter);
        appClassSpinner.setAdapter(appClassSpinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (null != curDeployCategory) {
                    curDeployCategory.title = categoryList.get(position).title;
                    curDeployCategory.itemId = categoryList.get(position).itemId;
                    appBelongList.clear();
                    appBelongList.addAll(categoryList.get(position).belongs);
                    if (!Utils.isListEmpty(appBelongList)) {
                        curAppBelong.itemId = appBelongList.get(0).itemId;
                        curAppBelong.title = appBelongList.get(0).title;
                        appBelongsSpinnerAdapter.notifyDataSetChanged();
                        //把应用归属重新置为第一个位置，保持数据一致
                        appBelongsSpinner.setSelection(0);
                        if (!isDeployTaskRunning)
                            startDeployListTask();
                    } else {
                        appBelongList.clear();
                        appClassEntities.clear();
                        appEntities.clear();
                        appBelongsSpinnerAdapter.notifyDataSetChanged();
                        appClassSpinnerAdapter.notifyDataSetChanged();
                        deployListAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        appBelongsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (null != curAppBelong) {
                    curAppBelong.title = appBelongList.get(position).title;
                    curAppBelong.itemId = appBelongList.get(position).itemId;
                    appClassSpinner.setSelection(0);
                    if(!isDeployTaskRunning)
                        startDeployListTask();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        appClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                appEntities.clear();
                appEntities.addAll(appClassEntities.get(position).appInfos);
                deployListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        title.setText(curCenterItemTitle + "-部署情况");
    }

    private void startDeployCateAndBelongsTask() {
        DeployCateAndBelongsTask task = new DeployCateAndBelongsTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    private class DeployCateAndBelongsTask extends AsyncTask<Void, Void, CateAndAppbelongResp> {

        private BaseReq req;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new BaseReq();
            req.userToken = ChinaUnicomApplication.token;
        }

        @Override
        protected CateAndAppbelongResp doInBackground(Void... params) {
            CateAndAppbelongResp resp = new Http.Builder().create().getCateAndAppbelongs(req);
            return resp;
        }

        @Override
        protected void onPostExecute(CateAndAppbelongResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                categoryList.clear();
                categoryList.addAll(resp.data.records);
                categorySpinnerAdapter.notifyDataSetChanged();
                if (!Utils.isListEmpty(categoryList)) {
                    appBelongList.clear();
                    appBelongList.addAll(categoryList.get(0).belongs);

                    if (!Utils.isListEmpty(appBelongList)) {
                        initCurCateAndBelongs(categoryList.get(0), appBelongList.get(0));
                        appBelongsSpinnerAdapter.notifyDataSetChanged();
                    }
                }

            } else
                Utils.showErrorToast(DeployListActivity.this, Config.TOAST_REQUEST_FAILED);
        }
    }

    private void initCurCateAndBelongs(DeployCategoryEntity dc, AppbelongEntity ae) {
        if (null == curDeployCategory) {
            curDeployCategory = new DeployCategoryEntity(dc.itemId, dc.title);
        } else {
            curDeployCategory.itemId = dc.itemId;
            curDeployCategory.title = dc.title;
        }

        if (null == curAppBelong) {
            curAppBelong = new AppbelongEntity(ae.itemId, ae.title);
        } else {
            curAppBelong.itemId = ae.itemId;
            curAppBelong.title = ae.title;
        }

    }

    class SpinnerViewHolder {

        @Bind(R.id.spinnerItem)
        TextView spinnerItem;

        public SpinnerViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class AppBelongsSpinnerAdapter extends BaseAdapter {
        private List<AppbelongEntity> items;

        public AppBelongsSpinnerAdapter(List<AppbelongEntity> items) {
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
                convertView = View.inflate(DeployListActivity.this, R.layout.my_simple_spinner_item, null);
                viewHolder = new SpinnerViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SpinnerViewHolder) convertView.getTag();
            }

            viewHolder.spinnerItem.setText(items.get(position).title);

            return convertView;
        }
    }

    class CategorySpinnerAdapter extends BaseAdapter {
        private List<DeployCategoryEntity> items;
        public CategorySpinnerAdapter(List<DeployCategoryEntity> items) {
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
                convertView = View.inflate(DeployListActivity.this, R.layout.my_simple_spinner_item, null);
                viewHolder = new SpinnerViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SpinnerViewHolder) convertView.getTag();
            }

            viewHolder.spinnerItem.setText(items.get(position).title);

            return convertView;
        }
    }

    class AppClassSpinnerAdapter extends BaseAdapter {

        private List<AppEntity> items;

        public AppClassSpinnerAdapter(List<AppEntity> items) {
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
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            SpinnerViewHolder viewHolder;
            if (null != convertView) {
               viewHolder = (SpinnerViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(DeployListActivity.this, R.layout.my_simple_spinner_item, null);
                viewHolder = new SpinnerViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            String appName = items.get(position).appName;
            if (Utils.isStringEmpty(appName))
                appName = "无父应用";
            viewHolder.spinnerItem.setText(appName);

            return convertView;
        }
    }

    private void startDeployListTask() {
        deployListReq = new DeployListReq();
        deployListReq.appClassId = curAppBelong.itemId;
        deployListReq.appHierarchyId = curDeployCategory.itemId;
        deployListReq.appName = "";
        deployListReq.centerId = curCenterItemId;
        deployListReq.envirId = "4";
        deployListReq.userToken = ChinaUnicomApplication.token;
        DeployListTask task = new DeployListTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    private class DeployListTask extends AsyncTask<Void, Void, DeployListResp> {

        private LoadToast loadToast = new LoadToast(DeployListActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.initLoadToast(loadToast);
            isDeployTaskRunning = true;
            appBelongsSpinner.setEnabled(false);
            categorySpinner.setEnabled(false);
            appClassSpinner.setEnabled(false);
            loadToast.show();
        }

        @Override
        protected DeployListResp doInBackground(Void... params) {
            DeployListResp resp = new Http.Builder().create().getDepolyList(deployListReq);
            return resp;
        }

        @Override
        protected void onPostExecute(DeployListResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                if (!Utils.isListEmpty(resp.data.records)) {
                    appClassEntities.clear();
                    appClassEntities.addAll(resp.data.records);
                    appClassSpinnerAdapter.notifyDataSetChanged();
                    appClassSpinner.setSelection(0);
                    appEntities.clear();
                    appEntities.addAll(appClassEntities.get(0).appInfos);
                    deployListAdapter.notifyDataSetChanged();
                }
                loadToast.success();
            } else {
                loadToast.error();
                appClassEntities.clear();
                appEntities.clear();
                Utils.showErrorToast(DeployListActivity.this, Config.TOAST_REQUEST_FAILED);
            }
            isDeployTaskRunning = false;
            appBelongsSpinner.setEnabled(true);
            categorySpinner.setEnabled(true);
            appClassSpinner.setEnabled(true);
        }
    }
}
