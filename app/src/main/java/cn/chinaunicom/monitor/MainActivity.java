package cn.chinaunicom.monitor;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.widget.Button;
import android.widget.RadioGroup;

import com.jauker.widget.BadgeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.chinaunicom.monitor.alarm.AlarmFragment;
import cn.chinaunicom.monitor.asynctask.UnCheckAlarmCategoryTask;
import cn.chinaunicom.monitor.callback.TabBarBadgeCallBack;
import cn.chinaunicom.monitor.console.ConsoleFragment;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.JPushAliasReq;
import cn.chinaunicom.monitor.http.Response.JPushAliasResp;
import cn.chinaunicom.monitor.mainframe.MainframeFragment;
import cn.chinaunicom.monitor.mine.MineFragment;
import cn.chinaunicom.monitor.receiver.LocalBroadcastManager;
import cn.chinaunicom.monitor.sqlite.AlarmDatabaseHelper;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class  MainActivity extends BaseFragmentActivity implements TabBarBadgeCallBack {

    //默认页面
    private MainTabType currentMenuType = MainTabType.Mainframe;
    //退出程序的时间控制
    private long currentTime = 0;
    public  String alias;
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION
            = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static final boolean isForeground = true;
    public static MainActivity instance = null;
    private BadgeView alarmBadge = null;
    private BadgeView mineBadge = null;
    private AlarmDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public int uncheckAlarmCnt = 0;

    @Bind(R.id.mainTabGroup)
    RadioGroup radioGroup;

    @Bind(R.id.mainTabAlarmBtn)
    Button mainTabAlarmBtn;

    @Bind(R.id.mainTabMineBtn)
    Button mainTabMineBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initInstances();
        //将JPush的alias推送到服务器
        alias = getIntent().getStringExtra("USERNAME");
        initDB();
        queryUnCheckAlarmCnt();
        ChinaUnicomApplication.userName = alias;
        //将用户名推送到server当做JPush的Alias
        startPostAlias();
        //使用用户名当做JPush的别名
        initJPush(alias);
        registerMessageReceiver();
        initView();
    }

    @Override
    public void initView() {
        changeBodyView();
    }

    @Override
    public void updateAlarmBadge() {
        queryUnCheckAlarmCnt();
        alarmBadge.setBadgeCount(uncheckAlarmCnt);
    }

    @Override
    public void updateMineBadge() {
        if (!Utils.isListEmpty(ChinaUnicomApplication.reportsIds))
            mineBadge.setText("●");
        else
            mineBadge.setBadgeCount(0);
    }

    private void initInstances() {
        instance = this;
        alarmBadge = new BadgeView(this);
        alarmBadge.setTargetView(mainTabAlarmBtn);

        mineBadge = new BadgeView(MainActivity.this);
        mineBadge.getBackground().setAlpha(0);
        mineBadge.setTextColor(Color.RED);
        mineBadge.setBadgeGravity(Gravity.TOP | Gravity.RIGHT );
        mineBadge.setTargetView(mainTabMineBtn);
    }

    private void initDB() {
        Config.DB_NAME = alias + ".db";
        dbHelper = new AlarmDatabaseHelper(this, Config.DB_NAME, null, Config.DB_VERSION);
    }

    //获得未读消息条数
    private void queryUnCheckAlarmCnt() {
        uncheckAlarmCnt = 0;
        //初始化告警数据库名称，一个用户对应一个数据库
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query("ALARM_CATEGORY", new String[]{"uncheck_num"}, null, null, null, null,null);
        while (c.moveToNext()) {
            uncheckAlarmCnt += c.getInt(c.getColumnIndex("uncheck_num"));
        }
        c.close();
    }

    public void changeBodyView() {
        updateBodyView();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if (currentMenuType.id == checkedId)
                    return;
                currentMenuType = MainTabType.getTypeById(checkedId);
                updateBodyView();
            }
        });
    }

    public void updateBodyView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, currentMenuType.getFragment(), currentMenuType.tag);
        transaction.commit();
    }

    enum MainTabType {
        Mainframe(R.id.mainTabMainframe, "Mainframe") {
            @Override
            Fragment getFragment() {
                return MainframeFragment.getInstance();
            }
        },
        Alarm(R.id.mainTabAlarm, "Alarm") {
            @Override
            Fragment getFragment() {
                return AlarmFragment.getInstance();
            }
        },
        Console(R.id.mainTabConsole, "Console") {
            @Override
            Fragment getFragment() {
                return ConsoleFragment.getInstance();
            }
        },
        Mine(R.id.mainTabMine, "Mine") {
            @Override
            Fragment getFragment() {
                return MineFragment.getInstance();
            }
        };

        public final int id;
        public final String tag;

        MainTabType(int id, String tag) {
            this.id = id;
            this.tag = tag;
        }

        public static MainTabType getTypeById(int id) {
            MainTabType rtn = Mainframe;
            MainTabType values[] = MainTabType.values();

            for (MainTabType tab : values) {
                if (tab.id == id) {
                    rtn = tab;
                    break;
                }
            }

            return rtn;
        }

        abstract Fragment getFragment();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - currentTime > 2000) {
            currentTime = System.currentTimeMillis();
            Utils.showWarningToast(this, "再按一次退出");
        }
        else {
            super.onBackPressed();
        }
    }

    public void registerMessageReceiver() {
        //initNotification();
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
//                    String messge = intent.getStringExtra(KEY_MESSAGE);
//                    String extras = intent.getStringExtra(KEY_EXTRAS);
//                    StringBuilder showMsg = new StringBuilder();
//                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
//                    if (!Utils.isStringEmpty(extras)) {
//                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
//                    }
                    Utils.showSuccessToast(MainActivity.this, "received alarm");
                }
            } catch (Exception e){
            }
        }
    }

    private void initJPush(String alias) {
        //JPush初始化
        //JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        JPushInterface.setAlias(this, 1, alias);
    }

    public void initNotification() {
        int builderId =1;

        //CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(MainActivity.this);

        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(MainActivity.this);
        builder.statusBarDrawable = R.mipmap.ic_alarm;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为自动消失
        builder.notificationDefaults =
                Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;  // 设置为铃声与震动都要
        JPushInterface.setPushNotificationBuilder(builderId , builder);
    }

    private void startPostAlias() {
        PostJPushAliasTask task = new PostJPushAliasTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    class PostJPushAliasTask extends AsyncTask<Void, Void, JPushAliasResp> {
        private JPushAliasReq jPushAliasReq = new JPushAliasReq();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            jPushAliasReq.token = alias;
            jPushAliasReq.userName = alias;
            jPushAliasReq.userToken = ChinaUnicomApplication.token;
        }

        @Override
        protected JPushAliasResp doInBackground(Void... voids) {
            JPushAliasResp resp = new Http.Builder().create().postAliasToServer(jPushAliasReq);
            return resp;
        }

        @Override
        protected void onPostExecute(JPushAliasResp resp) {
            super.onPostExecute(resp);
            if(!Utils.isRequestSuccess(resp)) {
                Utils.showErrorToast(MainActivity.this, "推送服务注册失败，可能无法接收推送消息！");
            } else {
                //请求还未查看的告警信息
                startUnCheckAlarmCategoryTask();
            }
        }
    }

    private void startUnCheckAlarmCategoryTask() {
        UnCheckAlarmCategoryTask task = new UnCheckAlarmCategoryTask(MainActivity.this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }
}
