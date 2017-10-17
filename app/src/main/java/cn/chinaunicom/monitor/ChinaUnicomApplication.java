package cn.chinaunicom.monitor;

import android.app.Application;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chinaunicom.monitor.adapters.AlarmCategoryAdapter;
import cn.chinaunicom.monitor.beans.AlarmCategoryCenterEntity;
import cn.chinaunicom.monitor.beans.AlarmCategoryEntity;
import cn.chinaunicom.monitor.beans.CellEntity;
import cn.chinaunicom.monitor.beans.CenterEntity;
import cn.chinaunicom.monitor.beans.GridInCenter;
import cn.chinaunicom.monitor.beans.GridItem;
import cn.chinaunicom.monitor.utils.Const;

/**
 * Created by yfyang on 2017/8/1.
 */

public class ChinaUnicomApplication extends Application {
    public static Application getApplication() {
        return mApplication;
    }
    private static Application mApplication;
    public static String token;
    public static String userName;
    public static List<AlarmCategoryEntity> unCheckAlarmCategories = new ArrayList<>();
    public static List<AlarmCategoryEntity> alarmCategoryEntities = new ArrayList<>();
    public static Map<String, Integer> badgeMap = new HashMap<>();
    public static AlarmCategoryAdapter alarmCategoryAdapter;
    public static List<CenterEntity> alarmCenterList = new ArrayList<>();
    public static CenterEntity alarmCurCenter;
    public static CenterEntity mainframeCurCenter;
    public static List<GridInCenter> gridInCenterList= new ArrayList<>();
    public static List<CenterEntity> mainframeCenterList = new ArrayList<>();
    public static List<GridItem> mainframeCurGrid = new ArrayList<>();
    public static List<CellEntity> curChartCells = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        DisplayMetrics dm =getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;

        Const.LOAD_TOAST_POS = h_screen/2;
        Const.POP_UP_DIALOG_HEIGHT = h_screen;
        Const.POP_UP_DIALOG_WIDTH = (h_screen/3);

        alarmCategoryAdapter = new AlarmCategoryAdapter(getApplication());
    }
}
