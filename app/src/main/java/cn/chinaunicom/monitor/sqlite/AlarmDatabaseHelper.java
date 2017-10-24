package cn.chinaunicom.monitor.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.utils.Utils;

/**
 * Created by yfYang on 2017/9/6.
 */

public class AlarmDatabaseHelper extends SQLiteOpenHelper {

    //未查看的类别：category类别名称 uncheck_num未查看的条数
    private static final String CREATE_ALARM_CATEGORY = "create table ALARM_CATEGORY (" +
            "id integer primary key," +
            "category text," +
            "uncheck_num integer," +
            "latest_content text," +
            "latest_send_time integer," +
            "column text," +
            "center_id text)";

    //所有存在本地的告警
    private static final String CREATE_ALARM_TABLE = "create table ALARM (" +
            "id integer primary key autoincrement," +
            "category text," +
            "column text," +
            "send_time integer," +
            "json_list text," +
            "json_map text," +
            "center_id text)";

    private static final String CREATE_CENTER_TABLE = "create table CENTER (" +
            "id integer primary key autoincrement," +
            "center_name text," +
            "center_id text," +
            "is_uncheck integer)"; //1:有新消息 0：没有

    private static final String CREATE_REPORT_TABLE = "create table REPORT (" +
            "id integer primary key autoincrement," +
            "center_name text," +
            "center_id text," +
            "report_content text," +
            "send_time integer," +
            "is_uncheck integer)"; //1:未读 0：已读

    private static final String CREATE_REPORT_CENTER_TABLE = "create table REPORT_CENTER (" +
            "id integer primary key autoincrement," +
            "center_name text," +
            "center_id text," +
            "is_uncheck integer)";

    public AlarmDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ALARM_CATEGORY);
        db.execSQL(CREATE_ALARM_TABLE);
        db.execSQL(CREATE_CENTER_TABLE);
        db.execSQL(CREATE_REPORT_TABLE);
        db.execSQL(CREATE_REPORT_CENTER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
