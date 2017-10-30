package cn.chinaunicom.monitor.console;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.adapters.FragmentViewPagerAdapter;
import cn.chinaunicom.monitor.beans.HostIp;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.ConnectHostReq;
import cn.chinaunicom.monitor.http.Request.ExcuteCommandReq;
import cn.chinaunicom.monitor.http.Request.HostIPsReq;
import cn.chinaunicom.monitor.http.Response.BaseResp;
import cn.chinaunicom.monitor.http.Response.ExcuteCommandResp;
import cn.chinaunicom.monitor.http.Response.HostIPsResp;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Logger;
import cn.chinaunicom.monitor.utils.Utils;

public class ConsoleFragment extends Fragment {

    private FragmentViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> fragments = new ArrayList<>();

    @Bind(R.id.txtViewTitle)
    TextView title;

    @Bind(R.id.console)
    TextView console;

    @Bind(R.id.consoleViewPager)
    ViewPager consoleViewPager;

//    @OnClick(R.id.connect)
//    void connect() {
//        startConnectHostTask();
//    }
//
//    @OnClick(R.id.ls)
//    void ls() {
//        startExcuteCommandTask("ls -l");
//    }
//
//    @OnClick(R.id.cd)
//    void cd() {
//        startExcuteCommandTask("cd /root/lpy");
//    }


    public ConsoleFragment() {
        // Required empty public constructor
    }

    public static ConsoleFragment getInstance() {
        ConsoleFragment fragment = new ConsoleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_console, container, false);
        ButterKnife.bind(this, view);
        initView();

        return view;
    }

    private void refreshConsole(String res) {
        console.append(res);
        int offset = console.getLineCount() * console.getLineHeight();
        if(offset > console.getHeight()){
            console.scrollTo(0,offset - console.getHeight());
        }
    }

    private void initView() {
        fragments.add(new ConsoleServerFragment());
        mViewPagerAdapter = new FragmentViewPagerAdapter(getChildFragmentManager(), fragments);
        consoleViewPager.setAdapter(mViewPagerAdapter);
        title.setText("控制台");
        console.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void startConnectHostTask() {
        ConnectHostTask task = new ConnectHostTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    private void startExcuteCommandTask(String command) {
        ExcuteCommandTask task = new ExcuteCommandTask(command);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    class ConnectHostTask extends AsyncTask<Void, Void, BaseResp> {
        ConnectHostReq req;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new ConnectHostReq();
            req.userToken = ChinaUnicomApplication.token;
            req.userName = ChinaUnicomApplication.userName;
        }

        @Override
        protected BaseResp doInBackground(Void... params) {
            BaseResp resp = new Http.Builder().create().connectHost(req);
            return resp;
        }

        @Override
        protected void onPostExecute(BaseResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                Utils.showSuccessToast(getActivity(), "链接成功...");
            } else {
                Utils.showErrorToast(getActivity(), "链接失败...");
            }
        }
    }

    class ExcuteCommandTask extends AsyncTask<Void, Void, ExcuteCommandResp> {
        ExcuteCommandReq req;
        String command;
        public ExcuteCommandTask(String command) {
            this.command = command;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new ExcuteCommandReq();
            req.userToken = ChinaUnicomApplication.token;
            req.userName = ChinaUnicomApplication.userName;
            req.command = command;
        }

        @Override
        protected ExcuteCommandResp doInBackground(Void... params) {
            ExcuteCommandResp resp = new Http.Builder().create().excuteCommand(req);
            return resp;
        }

        @Override
        protected void onPostExecute(ExcuteCommandResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                refreshConsole(buildCommandRes(resp.data.records));
            } else {
                Utils.showErrorToast(getActivity(), "执行失败...");
            }
        }

    }

    private String buildCommandRes(List<String> data) {
        StringBuilder sb = new StringBuilder();
        for (int line = 0; line < data.size(); line++) {
            sb.append(data.get(line));
            if (line != data.size() - 1)
                sb.append("\n");
        }
        return sb.toString();
    }

}
