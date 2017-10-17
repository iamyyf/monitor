package cn.chinaunicom.monitor.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.MainActivity;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.LogoutReq;
import cn.chinaunicom.monitor.http.Response.LogoutResp;
import cn.chinaunicom.monitor.login.LoginActivity;
import cn.chinaunicom.monitor.sqlite.AlarmDatabaseHelper;
import cn.chinaunicom.monitor.utils.Const;
import cn.chinaunicom.monitor.utils.Utils;

public class MineFragment extends Fragment {

    private AlarmDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Bind(R.id.userNameTextView)
    TextView userName;

    @Bind(R.id.txtViewTitle)
    TextView title;


    @OnClick(R.id.clearBuffer)
    void clearBuffer() {
        new AlertDialog
                .Builder(getActivity())
                .setTitle("提示")
                .setMessage("是否要清除所有缓存？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearAlarmBuffer();
                    }
                })
                .show();
    }

    @OnClick(R.id.group)
    void help() {
        Utils.showInfoToast(getActivity(), "建设中...");
    }

    @OnClick(R.id.report)
    void report() {
        Utils.showInfoToast(getActivity(), "建设中...");
    }

    @OnClick(R.id.guide)
    void guide() {
        Utils.showInfoToast(getActivity(), "建设中...");
    }

    @OnClick(R.id.about)
    void about() {
        Utils.showInfoToast(getActivity(), "建设中...");
    }


    @OnClick(R.id.logout)
    void logout() {
        new AlertDialog
                .Builder(getActivity())
                .setTitle("提示")
                .setMessage("是否要退出登录？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startLogoutTask();
                    }
                })
                .show();
    }

    @OnClick(R.id.setting)
    void setting() {
        Utils.showInfoToast(getActivity(), "建设中...");
    }

    public MineFragment() {
        // Required empty public constructor
    }

    public static MineFragment getInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        initDB();
        initView();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private void initDB() {
        dbHelper = new AlarmDatabaseHelper(getActivity(), Const.DB_NAME, null, Const.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    private void initView() {
        initTitleBar();
        userName.setText(ChinaUnicomApplication.userName);
    }

    private void initTitleBar() {
        title.setText("我");
    }

    private void clearAlarmBuffer() {
        db.delete("ALARM", null, null);
        db.delete("CENTER", null, null);
        db.delete("ALARM_CATEGORY", null, null);
        MainActivity.instance.updateBadge();
        Utils.showSuccessToast(getActivity(), "缓存清除成功");
    }
    private void startLogoutTask() {
        LogoutTask task = new LogoutTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    class LogoutTask extends AsyncTask<Void, Void, LogoutResp> {
        private LogoutReq req;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new LogoutReq();
            req.userToken = ChinaUnicomApplication.token;
            req.userName = ChinaUnicomApplication.userName;
        }

        @Override
        protected LogoutResp doInBackground(Void... params) {
            LogoutResp resp = new Http.Builder().create().logout(req);
            return resp;
        }

        @Override
        protected void onPostExecute(LogoutResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                Utils.showErrorToast(getActivity(), "登出失败");
            }
        }
    }

}
