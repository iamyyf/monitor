package cn.chinaunicom.monitor.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.MainActivity;
import cn.chinaunicom.monitor.alarm.AlarmFragment;
import cn.chinaunicom.monitor.beans.AlarmCategoryEntity;
import cn.chinaunicom.monitor.beans.CenterEntity;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.AlarmCategoryReq;
import cn.chinaunicom.monitor.http.Response.UnCheckAlarmCategoryResp;
import cn.chinaunicom.monitor.sqlite.AlarmDatabaseHelper;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by yfYang on 2017/9/6.
 */

public class UnCheckAlarmCategoryTask extends AsyncTask<Void, Void, UnCheckAlarmCategoryResp> {
    private Context context;
    private AlarmCategoryReq req = new AlarmCategoryReq();
    private AlarmDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private int uncheckAlarmCnt = 0;

    public UnCheckAlarmCategoryTask(Context context) {
        this.context = context;
        dbHelper = new AlarmDatabaseHelper(context, Config.DB_NAME, null, Config.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    private void initAlarmCategoryList(String currentCenterId) {
        //1.将未读的categroy存入map，为badge做准备
        Cursor cursor = db.query("ALARM_CATEGORY",new String[]{"category", "uncheck_num"},
                "center_id=?", new String[]{currentCenterId}, null, null, null);
        ChinaUnicomApplication.badgeMap.clear();
        while (cursor.moveToNext()) {
            ChinaUnicomApplication.badgeMap.put(cursor.getString(0), cursor.getInt(1));
        }
    }

    private void getAlarmCategoryEntities(String currentCenterId) {
        Cursor cursor = db.query("ALARM_CATEGORY", Config.ALARM_CATEGORY_COLUMN, "center_id=?",
                new String[]{currentCenterId}, null, null,null);
        ChinaUnicomApplication.alarmCategoryEntities.clear();
        while (cursor.moveToNext()) {
            AlarmCategoryEntity e = new AlarmCategoryEntity();
            //括号里的序号对应SqliteDataBaseHelper中表的列
            e.title = cursor.getString(1);
            e.value = cursor.getInt(2);
            e.mesContent = cursor.getString(3);
            e.sendTime = cursor.getLong(4);
            e.column = cursor.getString(5);
            ChinaUnicomApplication.alarmCategoryEntities.add(e);
        }
        ChinaUnicomApplication.alarmCategoryAdapter.notifyDataSetChanged();
    }

    private void updateAlarmCenterList() {
        Cursor centerCursor = db.query("CENTER", Config.CENTER_COLUMN, null, null, null, null, null);
        ChinaUnicomApplication.alarmCenterList.clear();
        while (centerCursor.moveToNext()) {
            CenterEntity e = new CenterEntity();
            e.title = centerCursor.getString(1);
            e.itemId = centerCursor.getString(2);
            e.isUncheck = centerCursor.getInt(3);
            ChinaUnicomApplication.alarmCenterList.add(e);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        req.userName = ChinaUnicomApplication.userName;
        req.userToken = ChinaUnicomApplication.token;
    }

    @Override
    protected UnCheckAlarmCategoryResp doInBackground(Void... voids) {
        UnCheckAlarmCategoryResp resp = new Http.Builder().create().getUnCheckAlarmCategory(req);
        return resp;
    }

    @Override
    protected void onPostExecute(UnCheckAlarmCategoryResp resp) {
        super.onPostExecute(resp);
            if (!Utils.isListEmpty(resp.data.records)) {

                //回调接口处理AlarmFragment右上角的圆点显示
                if (null != AlarmFragment.instance) {
                    AlarmFragment.instance.showPoint();
                }

                ShortcutBadger.applyCount(context, resp.data.badge); //for 1.1.4+

                //CenterId和中心名称的映射
                //ChinaUnicomApplication.alarmCenterMap.clear();
                //ChinaUnicomApplication.alarmCenterMap.putAll(resp.data.centerMap);
                //将中心信息存到List中，便于弹出对话框使用

                //records是中心信息
                for (int centerNum = 0; centerNum < resp.data.records.size(); centerNum++) {
                    String centerName = resp.data.records.get(centerNum).centerName;
                    String centerId = resp.data.records.get(centerNum).centerId;
                    Cursor uncheckCenterCursor = db.query("CENTER", Config.CENTER_COLUMN,
                            "center_name=? and center_id=?", new String[]{centerName, centerId}, null, null, null);
                    //如果之前这个中心有未读消息，则更新is_uncheck字段，否则插入新内容
                    ContentValues centerValues = new ContentValues();
                    centerValues.put("center_name", centerName);
                    centerValues.put("center_id", centerId);
                    centerValues.put("is_uncheck", 1);
                    if (!uncheckCenterCursor.moveToNext()) {
                        db.insert("CENTER", null, centerValues);
                    } else {
                        db.update("CENTER", centerValues, "center_name=? and center_id=?", new String[]{centerName, centerId});
                    }

                    //TODO:
                    updateAlarmCenterList();
                    //在刚登录时，用户肯定是没有选择中心标志的，所以中心标志为空，默认设为请求到的排在第一个的中心
                    if (null == ChinaUnicomApplication.alarmCurCenter) {
                        String itemId = ChinaUnicomApplication.alarmCenterList.get(0).itemId;
                        String title = ChinaUnicomApplication.alarmCenterList.get(0).title;
                        ChinaUnicomApplication.alarmCurCenter = new CenterEntity(itemId, title);
                    }

                    ChinaUnicomApplication.unCheckAlarmCategories.clear();
                    //records里面的records列表存的是每一个中心下的告警类别
                    ChinaUnicomApplication.unCheckAlarmCategories.addAll(resp.data.records.get(centerNum).records);
                    for (int cateNum = 0; cateNum < ChinaUnicomApplication.unCheckAlarmCategories.size(); cateNum++) {
                        String cateName = ChinaUnicomApplication.unCheckAlarmCategories.get(cateNum).title;
                        String latestContent = ChinaUnicomApplication.unCheckAlarmCategories.get(cateNum).mesContent;
                        long latestTime = ChinaUnicomApplication.unCheckAlarmCategories.get(cateNum).sendTime;
                        String column = ChinaUnicomApplication.unCheckAlarmCategories.get(cateNum).column;
                        int cateAlarmCnt = ChinaUnicomApplication.unCheckAlarmCategories.get(cateNum).value;
                        String cateCenterId = resp.data.records.get(centerNum).centerId;
                        //查询未查看的类别中是否有新请求到的类别
                        Cursor uncheckCateCursor = db.query("ALARM_CATEGORY", Config.ALARM_CATEGORY_COLUMN,
                                "category=? and center_id=?", new String[]{cateName, cateCenterId}, null, null, null);

                        ContentValues cateValues = new ContentValues();
                        cateValues.put("category", cateName);
                        cateValues.put("uncheck_num", cateAlarmCnt);
                        cateValues.put("latest_content", latestContent);
                        cateValues.put("latest_send_time", latestTime);
                        cateValues.put("column", column);
                        cateValues.put("center_id", cateCenterId);
                        if (uncheckCateCursor.moveToNext()) {
                            //如果以前该类别存储过，只需要更新成最新数据即可
                            db.update("ALARM_CATEGORY", cateValues, "category=? and center_id=?", new String[]{cateName, cateCenterId});
                        } else {
                            //以前没有这类告警，则新增这类告警
                            db.insert("ALARM_CATEGORY", null, cateValues);
                        }

                        //每次来了新的告警进行下载，下载完成并更新数据库完成后，将本地的badge信息进行更新
                        initAlarmCategoryList(ChinaUnicomApplication.alarmCurCenter.itemId);
                        //每次来了新的告警进行网络下载，下载完成后对本地全局的告警类别列表信息进行更新
                        getAlarmCategoryEntities(ChinaUnicomApplication.alarmCurCenter.itemId);
//                    uncheckCateCursor.close();
//                    db.close();
                    }
                }
                MainActivity.instance.updateBadge();
                if (null != AlarmFragment.instance)
                    AlarmFragment.instance.title.setText(ChinaUnicomApplication.alarmCurCenter.title + "-告警");
        }
    }
}
