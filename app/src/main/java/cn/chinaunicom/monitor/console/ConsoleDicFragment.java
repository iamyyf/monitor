package cn.chinaunicom.monitor.console;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.Command;
import cn.chinaunicom.monitor.beans.Connection;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.DisconnectServerReq;
import cn.chinaunicom.monitor.http.Response.DisconnectServerResp;
import cn.chinaunicom.monitor.utils.Logger;
import cn.chinaunicom.monitor.utils.Utils;

public class ConsoleDicFragment extends Fragment {

    private Connection conn = null;
    public static ConsoleDicFragment instance = null;
    public DicListAdapter mAdapter = null;
    public List<Command> commands = new ArrayList<>();

    @Bind(R.id.dicListView)
    ListView dicListView;

    @OnClick(R.id.disconnectBtn)
    void disconnect() {
        startDisconnectTask();
    }

    public ConsoleDicFragment() {
        // Required empty public constructor
    }

    public static ConsoleDicFragment getInstance() {
        ConsoleDicFragment fragment = new ConsoleDicFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_console_dic, container, false);
        ButterKnife.bind(this, view);
        initDicListView();
        return view;
    }

    private void initDicListView() {
        mAdapter = new DicListAdapter(commands);
        dicListView.setAdapter(mAdapter);
        dicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConsoleFragment.instance.startExcuteCommandTask(commands.get(position).cmdIdentifier);
            }
        });
    }

    private void startDisconnectTask() {
        DisconnectTask task = new DisconnectTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }


    class DisconnectTask extends AsyncTask<Void, Void, DisconnectServerResp> {
        DisconnectServerReq req;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new DisconnectServerReq();
            req.userName = ChinaUnicomApplication.userName;
            req.userToken = ChinaUnicomApplication.token;
        }

        @Override
        protected DisconnectServerResp doInBackground(Void... voids) {
            DisconnectServerResp resp = new Http.Builder().create().disconnectServer(req);
            return resp;
        }

        @Override
        protected void onPostExecute(DisconnectServerResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                Utils.showSuccessToast(getActivity(), "链接已断开");
                ConsoleFragment.instance.conn = null;
                ConsoleFragment.instance.consoleViewPager.setCurrentItem(0, true);
            } else {
                Utils.showErrorToast(getActivity(), "链接断开失败，请重试");
            }
        }
    }

    class DicViewHolder {
        ImageView logo;
        TextView cmd;
        TextView desc;


        public DicViewHolder(View view) {
            logo = (ImageView) view.findViewById(R.id.logo);
            cmd = (TextView) view.findViewById(R.id.itemTitle);
            desc = (TextView) view.findViewById(R.id.desc);
        }
    }

    class DicListAdapter extends BaseAdapter {
        private List<Command> commands;
        private DicViewHolder viewHolder;
        public DicListAdapter(List<Command> commands) {
            this.commands = commands;
        }

        @Override
        public int getCount() {
            return commands.size();
        }

        @Override
        public Object getItem(int position) {
            return commands.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null != convertView) {
                viewHolder = (DicViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(getActivity(), R.layout.list_item_with_logo, null);
                viewHolder = new DicViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            viewHolder.cmd.setText(commands.get(position).cmdTitle);
            viewHolder.desc.setText(commands.get(position).cmdDesc);
            viewHolder.logo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_command));
            return convertView;
        }
    }

}
