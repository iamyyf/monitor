package cn.chinaunicom.monitor.mainframe;

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
import cn.chinaunicom.monitor.adapters.DataSetChartsAdapter;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.DataSetIpReq;
import cn.chinaunicom.monitor.http.Response.DataSetIpResp;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.BaseViewHolder;

public class DataSetChartActivity extends BaseActivity {

    private List<String> IPs = new ArrayList<>();
    private IpSpinnerAdapter spinnerAdapter;

    @Bind(R.id.imgBtnLeft)
    ImageButton backBtn;

    @Bind(R.id.imgBtnRight)
    ImageButton imgBtnRight;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @Bind(R.id.ipSpinner)
    Spinner ipSpinner;

    @Bind(R.id.dataSetChartList)
    ListView chartList;

    @OnClick(R.id.imgBtnLeft)
    void back() {
        DataSetChartActivity.this.finish();
    }

    @OnClick(R.id.imgBtnRight)
    void refresh() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_set_chart);
        ButterKnife.bind(this);
        startIpTask();
        initView();
    }

    @Override
    public void initView() {
        initTitleBar();
        spinnerAdapter = new IpSpinnerAdapter(IPs);
        ipSpinner.setAdapter(spinnerAdapter);

        ipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chartList.setAdapter(new DataSetChartsAdapter(DataSetChartActivity.this, IPs.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow, null));
            imgBtnRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh, null));
        } else {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow));
            imgBtnRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh));
        }
        title.setText("数据归集");
    }


    class SpinnerViewHolder extends BaseViewHolder{

        @Bind(R.id.spinnerItem)
        TextView ip;

        public SpinnerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class IpSpinnerAdapter extends BaseAdapter {
        private List<String> items;
        public IpSpinnerAdapter(List<String> items) {
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
                    convertView = View.inflate(DataSetChartActivity.this, R.layout.my_simple_spinner_item, null);
                viewHolder = new SpinnerViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SpinnerViewHolder) convertView.getTag();
            }
            viewHolder.ip.setText(items.get(position));

            return convertView;
        }
    }

    private void startIpTask() {
        DataSetIpTask task = new DataSetIpTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    class DataSetIpTask extends AsyncTask<Void, Void, DataSetIpResp> {
        private DataSetIpReq req;
        LoadToast loadToast = new LoadToast(DataSetChartActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new DataSetIpReq();
            req.userToken = ChinaUnicomApplication.token;
            req.groupName = Config.DATA_SET_IP_REQ;
            Utils.initLoadToast(loadToast);
            loadToast.show();
        }

        @Override
        protected DataSetIpResp doInBackground(Void... params) {
            DataSetIpResp resp = new Http.Builder().create().getDataSetIp(req);
            return resp;
        }

        @Override
        protected void onPostExecute(DataSetIpResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                loadToast.success();
                IPs.clear();
                IPs.addAll(resp.data.records);
                spinnerAdapter.notifyDataSetChanged();
            } else {
                loadToast.error();
                Utils.showErrorToast(DataSetChartActivity.this, Config.TOAST_REQUEST_FAILED);
            }
        }
    }

}


