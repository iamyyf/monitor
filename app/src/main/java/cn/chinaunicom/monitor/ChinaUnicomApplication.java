package cn.chinaunicom.monitor;

import android.app.Application;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chinaunicom.monitor.adapters.AlarmCategoryAdapter;
import cn.chinaunicom.monitor.beans.AlarmCategoryEntity;
import cn.chinaunicom.monitor.beans.CellEntity;
import cn.chinaunicom.monitor.beans.CenterEntity;
import cn.chinaunicom.monitor.beans.GridInCenter;
import cn.chinaunicom.monitor.beans.GridItem;
import cn.chinaunicom.monitor.beans.HostIp;
import cn.chinaunicom.monitor.utils.Config;

/**
 * Created by yfyang on 2017/8/1.
 */

public class ChinaUnicomApplication extends Application {
    public static Application getApplication() {
        return mApplication;
    }
    private static Application mApplication;                                                //应用实例
    public static String token;                                                             //用户token
    public static String userName;                                                          //当前登录用户的用户名
    public static List<AlarmCategoryEntity> unCheckAlarmCategories = new ArrayList<>();     //未查看告警类别列表
    public static List<AlarmCategoryEntity> alarmCategoryEntities = new ArrayList<>();      //告警类别列表
    public static Map<String, Integer> badgeMap = new HashMap<>();                          //控制告警红点逻辑
    public static AlarmCategoryAdapter alarmCategoryAdapter;                                //告警类别列表的adapter
    public static List<CenterEntity> alarmCenterList = new ArrayList<>();                   //有告警的中心
    public static CenterEntity alarmCurCenter;                                              //告警当前显示的中心
    public static CenterEntity mainframeCurCenter;                                          //监控界面当前选择的中心
    public static List<GridInCenter> gridInCenterList= new ArrayList<>();
    public static List<CenterEntity> mainframeCenterList = new ArrayList<>();
    public static List<GridItem> mainframeCurGrid = new ArrayList<>();
    public static List<CellEntity> curChartCells = new ArrayList<>();
    public static List<Long> reportsIds = new ArrayList<>();                                //晨检报告的id
    public static List<HostIp> consoleHostIps = new ArrayList<>();                          //控制台server的ip

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        DisplayMetrics dm =getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;

        Config.LOAD_TOAST_POS = h_screen/2;
        Config.POP_UP_DIALOG_HEIGHT = h_screen;
        Config.POP_UP_DIALOG_WIDTH = (h_screen/3);

        alarmCategoryAdapter = new AlarmCategoryAdapter(getApplication());
    }
}
