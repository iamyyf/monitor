package cn.chinaunicom.monitor.mine;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jauker.widget.BadgeView;
import com.zaaach.toprightmenu.MenuItem;
import com.zaaach.toprightmenu.TopRightMenu;

import net.steamcrafted.loadtoast.LoadToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.BaseActivity;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.MainActivity;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.CenterEntity;
import cn.chinaunicom.monitor.beans.ReportEntity;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.ReportsReq;
import cn.chinaunicom.monitor.http.Response.ReportsResp;
import cn.chinaunicom.monitor.sqlite.AlarmDatabaseHelper;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Logger;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.ReportGridViewHolder;

public class ReportListActivity extends BaseActivity {

    private List<MenuItem> menuItems = new ArrayList<>();
    private Map<String, String> uncheckCenterMap = new HashMap<>();
    private List<CenterEntity> reportCenterEntities = new ArrayList<>();
    private List<ReportEntity> reportEntities = new ArrayList<>();
    private AlarmDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ReportGridViewAdapter mAdapter = new ReportGridViewAdapter(reportEntities);
    private TopRightMenu menu;
    private CenterEntity curCenter = null;
    private BadgeView imgRightBtnBadgeView;

    private final int UNREAD = 1;
    private final int HAS_READ = 0;

    @Bind(R.id.imgBtnRight)
    ImageView menuTopRight;

    @Bind(R.id.imgBtnLeft)
    ImageButton backBtn;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @Bind(R.id.reportGridView)
    GridView reportGridView;

    @OnClick(R.id.imgBtnLeft)
    void back() {
        ReportListActivity.this.finish();
    }

    @OnClick(R.id.imgBtnRight)
    void popMenu() {
        updateUnCheckCenterMap();
        updateReportCenterList();
        initTopRigtMenu();
        initMenuItem(reportCenterEntities, uncheckCenterMap);
        menu.showAsDropDown(menuTopRight, 0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        ButterKnife.bind(this);
        //顺序不能变
        initDB();
        if (!Utils.isListEmpty(ChinaUnicomApplication.reportsIds))
            startReportTask();
        else {
            updateReportCenterList();
            updateReportsList(curCenter);
            title.setText(curCenter.title + "-晨检报告");
        }
        initTopRigtMenu();
        initGridView();
        initView();
    }

    @Override
    public void initView() {
        initTitleBar();

        imgRightBtnBadgeView = new BadgeView(this);
        imgRightBtnBadgeView.setTargetView(menuTopRight);
        imgRightBtnBadgeView.getBackground().setAlpha(0);
        imgRightBtnBadgeView.setTextColor(Color.rgb(205, 175, 149));
        imgRightBtnBadgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT );

        updateUnCheckCenterMap();
        updateTopRightPoint();
    }

    private void updateUnCheckCenterMap() {
        Cursor uncheckCenterCursor = db.query("REPORT_CENTER", Config.REPORT_CENTER_COLUMN,
                null, null, null, null, null);
        while (uncheckCenterCursor.moveToNext()) {
            if (uncheckCenterCursor.getInt(3) == UNREAD) //有未读消息
                uncheckCenterMap.put(uncheckCenterCursor.getString(1), uncheckCenterCursor.getString(2));
        }
        uncheckCenterCursor.close();
    }

    private void updateReportsList(CenterEntity curCenter) {
        if (null == curCenter || Utils.isStringEmpty(curCenter.title))
            return;
        Cursor cursor = db.query("REPORT", Config.REPORT_COLUMN, "center_name=?",
                new String[]{curCenter.title}, null, null, null);
        reportEntities.clear();
        while (cursor.moveToNext()) {
            ReportEntity e = new ReportEntity();
            e.center = cursor.getString(1);
            e.centerId = cursor.getString(2);
            e.reportContent = cursor.getString(3);
            e.sendTime = cursor.getLong(4);
            e.isChecked = cursor.getInt(5);
            reportEntities.add(e);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initDB() {
        dbHelper = new AlarmDatabaseHelper(this, Config.DB_NAME, null, Config.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    private void initGridView() {
        reportGridView.setAdapter(mAdapter);

        reportGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReportEntity e = reportEntities.get(position);
                ContentValues fileValues = new ContentValues();
                fileValues.put("is_uncheck", HAS_READ);
                db.update("REPORT", fileValues,
                        "send_time=? and center_name=?", new String[]{e.sendTime+"", e.center});
                reportEntities.get(position).isChecked = HAS_READ;
                mAdapter.notifyDataSetChanged();
                //打开文件
                startReportActivity(curCenter.title, curCenter.itemId, reportEntities.get(position).sendTime);
            }
        });
    }

    private void startReportActivity(String centerName, String centerId, long sendTime) {
        Intent intent = new Intent(ReportListActivity.this, ReportActivity.class);
        intent.putExtra("CENTER_NAME", centerName);
        intent.putExtra("CENTER_ID", centerId);
        intent.putExtra("SEND_TIME", sendTime);
        startActivity(intent);
    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow, null));
            menuTopRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_menu, null));
        } else {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow));
            menuTopRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_menu));
        }
        //title.setText("晨检报告");
    }

    class ReportGridViewAdapter extends BaseAdapter {

        private List<ReportEntity> data;

        public ReportGridViewAdapter(List<ReportEntity> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ReportGridViewHolder viewHolder;
            if (null != convertView) {
                viewHolder = (ReportGridViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(ReportListActivity.this, R.layout.grid_item_report, null);
                viewHolder = new ReportGridViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            viewHolder.itemName.setText(formatTime(data.get(position).sendTime));
            viewHolder.icon.setImageResource(R.mipmap.ic_report_icon);
            if (data.get(position).isChecked == UNREAD) {
                viewHolder.isChecked.setTextColor(Color.RED);
                viewHolder.isChecked.setText("未读");
            } else {
                viewHolder.isChecked.setTextColor(Color.rgb(32,114,69));
                viewHolder.isChecked.setText("已读");
            }

            return convertView;
        }
    }

    private void updateTopRightPoint() {
        if (!uncheckCenterMap.isEmpty()) {
            imgRightBtnBadgeView.setText("●");
        } else {
            imgRightBtnBadgeView.setBadgeCount(0);
        }
    }

    private String formatTime(long millionSec) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        return sdf.format(new Date(millionSec));
    }

    private void initTopRigtMenu() {
        menu = new TopRightMenu(this);
        menu.setHeight(Config.POP_UP_DIALOG_HEIGHT)
                .setWidth(Config.POP_UP_DIALOG_WIDTH)
                .showIcon(true)
                .dimBackground(true)
                .needAnimationStyle(true)
                .setOnMenuItemClickListener(
                        new TopRightMenu.OnMenuItemClickListener() {
                            @Override
                            public void onMenuItemClick(int position) {
                                actionWhenClickMenuItem(position);
                            }
                        });
    }

    private void initMenuItem(List<CenterEntity> data, Map<String, String> uncheckCenterMap) {
        menuItems.clear();
        //如果未查看的中心Map中有该中心，则显示红点，否则不显示
        for (int i = 0; i < data.size(); i++) {
            if (uncheckCenterMap.containsKey(data.get(i).title)) {
                menuItems.add(new MenuItem(R.mipmap.ic_red_point, data.get(i).title));
            } else {
                menuItems.add(new MenuItem(R.mipmap.ic_blank, data.get(i).title));
            }
        }
        menu.addMenuList(menuItems);
    }

    private void updateReportCenterList() {
        reportCenterEntities.clear();
        Cursor centerCursor = db.query("REPORT_CENTER", Config.REPORT_CENTER_COLUMN, null, null, null, null, null);
        while (centerCursor.moveToNext()) {
            CenterEntity e = new CenterEntity();
            e.title = centerCursor.getString(1);
            e.itemId = centerCursor.getString(2);
            e.isUncheck = centerCursor.getInt(3);
            reportCenterEntities.add(e);
        }
        centerCursor.close();
        if (null == curCenter) {
            curCenter = new CenterEntity();
            if (!Utils.isListEmpty(reportCenterEntities)) {
                curCenter.title = reportCenterEntities.get(0).title;
                curCenter.itemId = reportCenterEntities.get(0).itemId;
                curCenter.isUncheck = reportCenterEntities.get(0).isUncheck;
            }
        }


    }

    private void actionWhenClickMenuItem(int position) {

        curCenter.title = reportCenterEntities.get(position).title;
        curCenter.itemId = reportCenterEntities.get(position).itemId;
        curCenter.isUncheck = HAS_READ;

        uncheckCenterMap.remove(curCenter.title);
        updateTopRightPoint();
        ContentValues values = new ContentValues();
        values.put("is_uncheck", HAS_READ);
        db.update("REPORT_CENTER", values, "center_name=?", new String[]{curCenter.title});
        title.setText(curCenter.title + "-晨检报告");
        updateReportsList(curCenter);
    }

    private void startReportTask() {
        ReportTask task = new ReportTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    class ReportTask extends AsyncTask<Void, Void, ReportsResp> {
        private ReportsReq req;
        private LoadToast loadToast;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new ReportsReq();
            req.userToken = ChinaUnicomApplication.token;
            req.userName = ChinaUnicomApplication.userName;
            req.reportIds = ChinaUnicomApplication.reportsIds;
            loadToast = new LoadToast(ReportListActivity.this);
            Utils.initLoadToast(loadToast);
            loadToast.show();
        }

        @Override
        protected ReportsResp doInBackground(Void... params) {
            ReportsResp resp = new Http.Builder().create().getReports(req);
            return resp;
        }

        @Override
        protected void onPostExecute(ReportsResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp) && !Utils.isListEmpty(resp.data.records)) {
                //请求到最新的晨检报告写到数据库
                Cursor cursor = null;
                for (int reportNum = 0; reportNum < resp.data.records.size(); reportNum++) {
                    ReportEntity e = resp.data.records.get(reportNum);
                    ContentValues values = new ContentValues();
                    values.put("center_name", e.center);
                    values.put("center_id", e.centerId);
                    values.put("report_content", e.reportContent);
                    values.put("send_time", e.sendTime);
                    values.put("is_uncheck", UNREAD);
                    db.insert("REPORT", null, values);

                    cursor = db.query("REPORT_CENTER", Config.REPORT_CENTER_COLUMN,
                            "center_name=? and center_id=?", new String[]{e.center, e.centerId}, null, null, null);

                    ContentValues center = new ContentValues();
                    if (cursor != null && !cursor.moveToNext()) {
                        center.put("center_name", e.center);
                        center.put("center_id", e.centerId);
                        center.put("is_uncheck", UNREAD);
                        db.insert("REPORT_CENTER", null, center);
                    } else if (cursor != null) {
                        center.put("is_uncheck", UNREAD);
                        db.update("REPORT_CENTER", center, "center_name=? and center_id=?", new String[]{e.center, e.centerId});
                    }
                }
                cursor.close();
                loadToast.success();
                updateReportCenterList();
                updateReportsList(curCenter);
                title.setText(curCenter.title + "-晨检报告");
                ChinaUnicomApplication.reportsIds.clear();
                updateUnCheckCenterMap();
                updateTopRightPoint();
                MainActivity.instance.updateMineBadge();
                if (null != MineFragment.instance)
                    MineFragment.instance.updateReportBadage();
            } else {
                loadToast.error();
                Utils.showErrorToast(ReportListActivity.this, Config.TOAST_REQUEST_FAILED);
            }
        }
    }
}
