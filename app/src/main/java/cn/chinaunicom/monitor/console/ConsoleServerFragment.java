package cn.chinaunicom.monitor.console;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.adapters.OnlyTextSpinnerAdapter;
import cn.chinaunicom.monitor.beans.HostIp;
import cn.chinaunicom.monitor.beans.HostIpEntity;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.ConnectHostReq;
import cn.chinaunicom.monitor.http.Request.HostIPsReq;
import cn.chinaunicom.monitor.http.Response.BaseResp;
import cn.chinaunicom.monitor.http.Response.ConnectionResp;
import cn.chinaunicom.monitor.http.Response.HostIPsResp;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Logger;
import cn.chinaunicom.monitor.utils.Utils;

public class ConsoleServerFragment extends Fragment {

    private List<HostIp> hostIps;
    private OnlyTextSpinnerAdapter mSpinnerAdapter;
    private IpListAdapter mIpListAdapter;
    private List<String> spinnerItems = new ArrayList<>();
    private List<HostIpEntity> curServers = new ArrayList<>();

    @Bind(R.id.hostIpsSpinner)
    Spinner hostIpsSpinner;

    @Bind(R.id.ipsList)
    ListView ipsList;

    public ConsoleServerFragment() {
        // Required empty public constructor
    }

    public static ConsoleServerFragment getInstance() {
        ConsoleServerFragment fragment = new ConsoleServerFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_console_server, container, false);
        ButterKnife.bind(this, view);

        initSpinnerItemList();
        initIpListView();
        mSpinnerAdapter = new OnlyTextSpinnerAdapter(getActivity(), spinnerItems);
        mIpListAdapter = new IpListAdapter();
        hostIpsSpinner.setAdapter(mSpinnerAdapter);
        ipsList.setAdapter(mIpListAdapter);

        //如果控制台IP的列表为空，就下载
        if (Utils.isListEmpty(ChinaUnicomApplication.consoleHostIps))
            startHostIpTask();
        initSpinner();

        return view;
    }

    private void initSpinner() {
        hostIpsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curServers.clear();
                curServers.addAll(hostIps.get(position).hostList);
                //更新ip列表
                mIpListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinnerItemList() {
        hostIps = ChinaUnicomApplication.consoleHostIps;
        spinnerItems.clear();
        for (int i = 0; i < hostIps.size(); i++) {
            spinnerItems.add(hostIps.get(i).hostTypeName);
        }
    }

    private void initIpListView() {
       ipsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               startConnectHostTask();
           }
       });
    }

    class IpListViewHolder {
        TextView ip;
        TextView desc;
        ImageView serverLogo;

        public IpListViewHolder(View view) {
           ip = (TextView) view.findViewById(R.id.itemTitle);
           desc = (TextView) view.findViewById(R.id.desc);
           serverLogo = (ImageView) view.findViewById(R.id.logo);
        }
    }

    class IpListAdapter extends BaseAdapter {
        private IpListViewHolder viewHolder;
        @Override
        public int getCount() {
            return curServers.size();
        }

        @Override
        public Object getItem(int position) {
            return curServers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null != convertView) {
                viewHolder = (IpListViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(getActivity(), R.layout.list_item_with_logo, null);
                viewHolder = new IpListViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            viewHolder.ip.setText(curServers.get(position).ip);
            viewHolder.desc.setText(curServers.get(position).desc);
            viewHolder.serverLogo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_server));

            return convertView;
        }
    }

    private void startConnectHostTask() {
        ConnectHostTask task = new ConnectHostTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    class ConnectHostTask extends AsyncTask<Void, Void, ConnectionResp> {
        ConnectHostReq req;
        LoadToast loadToast;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new ConnectHostReq();
            req.userToken = ChinaUnicomApplication.token;
            req.userName = ChinaUnicomApplication.userName;
            loadToast = new LoadToast(getActivity());
            Utils.initLoadToast(loadToast);
            loadToast.show();
        }

        @Override
        protected ConnectionResp doInBackground(Void... params) {
            ConnectionResp resp = new Http.Builder().create().connectHost(req);
            return resp;
        }

        @Override
        protected void onPostExecute(ConnectionResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                ConsoleFragment.instance.consoleViewPager.setCurrentItem(1, true);
                ConsoleDicFragment.instance.commands.clear();
                ConsoleDicFragment.instance.commands.addAll(resp.data.commandList);
                ConsoleDicFragment.instance.mAdapter.notifyDataSetChanged();

                loadToast.success();
                Utils.showSuccessToast(getActivity(), "链接成功");
            } else {
                loadToast.error();
                Utils.showErrorToast(getActivity(), "链接失败");
            }
        }
    }

    private void startHostIpTask() {
        HostIpTask task = new HostIpTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    class HostIpTask extends AsyncTask<Void, Void, HostIPsResp> {
        HostIPsReq req;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new HostIPsReq();
            req.userToken = ChinaUnicomApplication.token;
        }

        @Override
        protected HostIPsResp doInBackground(Void... voids) {
            HostIPsResp resp = new Http.Builder().create().getHostIps(req);
            return resp;
        }

        @Override
        protected void onPostExecute(HostIPsResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                ChinaUnicomApplication.consoleHostIps.clear();
                if (!Utils.isListEmpty(resp.data.records)) {
                    ChinaUnicomApplication.consoleHostIps.clear();
                    ChinaUnicomApplication.consoleHostIps.addAll(resp.data.records);
                }
                initSpinnerItemList();
                mSpinnerAdapter.notifyDataSetChanged();
            } else {
                Utils.showErrorToast(getActivity(), Config.TOAST_REQUEST_FAILED);
            }
        }
    }

}
