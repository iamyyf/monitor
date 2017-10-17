package cn.chinaunicom.monitor.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import net.steamcrafted.loadtoast.LoadToast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.BaseActivity;
import cn.chinaunicom.monitor.BaseFragmentActivity;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.MainActivity;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.LoginReq;
import cn.chinaunicom.monitor.http.Response.LoginResp;
import cn.chinaunicom.monitor.utils.Const;
import cn.chinaunicom.monitor.utils.Utils;

public class LoginActivity extends BaseActivity {
    private LoadToast progressToast;
    private final String SHARED_PREFERENCES_NAME = "USER_INFO";
    private final String USER_NAME = "USERNAME";
    private final String PASSWORD = "PASSWORD";

    @Bind(R.id.usernameEdit)
    EditText usernameEdit;

    @Bind(R.id.passwordEdit)
    EditText passwordEdit;

    @Bind(R.id.loginBtn)
    Button loginBtn;

    @OnClick(R.id.loginBtn)
    void login() {
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (loginVerify(username, password)) {
            loginBtn.setEnabled(false);
            progressToast.show();
            startLoginTask(username, password);
        }
        else {
            loginBtn.setEnabled(true);
            Utils.showErrorToast(this, Const.TOAST_LOGIN_TIP);
        }
    }

    private LoginReq loginReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        try {
            //设置状态栏颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.colorWhite));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressToast = new LoadToast(this);
        progressToastStyle();
        usernameEdit.setText(getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(USER_NAME, ""));
        passwordEdit.setText(getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(PASSWORD, ""));
    }

    private void startLoginTask(String username, String password) {
        LoginTask task = new LoginTask(username, password);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    private void storeUserInfo(String userName, String password) {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME, userName);
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    private class LoginTask extends AsyncTask<Void, Void, LoginResp> {

        private String username;
        private String password;

        public  LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginReq = new LoginReq();
            loginReq.username = username;
            loginReq.password = password;
            loginReq.captcha = "";
        }

        @Override
        protected LoginResp doInBackground(Void... params) {
            LoginResp resp = new Http.Builder().create().login(loginReq);
            return resp;
        }

        @Override
        protected void onPostExecute(LoginResp resp) {
            super.onPostExecute(resp);
            if (null != resp && isLoginSuccess(resp)) {
                ChinaUnicomApplication.token = resp.data.token;
                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                        .putExtra("USERNAME", username));
                storeUserInfo(username, password);
                LoginActivity.this.finish();
            } else {
                progressToast.error();
                Utils.showErrorToast(LoginActivity.this, Const.TOAST_LOGIN_FAILED);
            }
            loginBtn.setEnabled(true);
        }
    }

    private boolean isLoginSuccess(LoginResp resp) {
        return (resp.msg.equals(Const.REQ_SUCCESS_MSG))
                && (resp.code == Const.REQ_SUCCESS_CODE);
    }

    private boolean loginVerify(String username, String password) {
        return !(Utils.isStringEmpty(username) || Utils.isStringEmpty(password));
    }

    private void progressToastStyle() {
        progressToast.setProgressColor(Color.WHITE);
        progressToast.setTranslationY(Const.LOAD_TOAST_POS);
        progressToast.setBackgroundColor(Color.rgb(244,34,6));
    }
}
