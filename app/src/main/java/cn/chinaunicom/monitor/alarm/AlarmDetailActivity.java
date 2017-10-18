package cn.chinaunicom.monitor.alarm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.steamcrafted.loadtoast.LoadToast;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.BaseActivity;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.AlarmDetailEntity;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.UnCheckAlarmDetailReq;
import cn.chinaunicom.monitor.http.Response.UnCheckAlarmDetailResp;
import cn.chinaunicom.monitor.sqlite.AlarmDatabaseHelper;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Logger;
import cn.chinaunicom.monitor.utils.Utils;

public class AlarmDetailActivity extends BaseActivity {

    private String alarmCategory;
    private String alarmCategoryTitle;
    private String alarmCenterName;
    private String alarmCenterId;
    private List<AlarmDetailEntity> alarmDetailEntities = new ArrayList<>();
    private AlarmDetailListAdapter alarmDetailListAdapter = new AlarmDetailListAdapter();
    private boolean isAlarmDtailTaskRunning = false;
    private AlarmDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Bind(R.id.imgBtnRight)
    ImageButton imgBtnRight;

    @Bind(R.id.imgBtnLeft)
    ImageButton backBtn;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @Bind(R.id.alarmDetailList)
    ListView alarmDetailList;

    @OnClick(R.id.imgBtnLeft)
    void back() {
        AlarmDetailActivity.this.finish();
    }

    @OnClick(R.id.imgBtnRight)
    void refresh() {
        if (!isAlarmDtailTaskRunning)
            startAlarmDetailTask();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);
        ButterKnife.bind(this);
        initDB();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //db.close();
    }

    @Override
    public void initView() {
        getImperativeData();
        initTitleBar();
        initListView();
        startAlarmDetailTask();
    }

    private void initDB() {
        dbHelper = new AlarmDatabaseHelper(this, Config.DB_NAME, null, Config.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    private void getAlarmDtailFromDB() {
        alarmDetailEntities.clear();
        Cursor cursor = db.query("ALARM", Config.ALARM_COLUMN, "category=? and center_id=?",
                new String[]{alarmCategoryTitle, alarmCenterId}, null, null, "send_time desc");
        while (cursor.moveToNext()) {
            AlarmDetailEntity e = new AlarmDetailEntity();
            e.sendTime = cursor.getLong(3);
            e.jsonList = cursor.getString(4);
            e.jsonMap = cursor.getString(5);
            alarmDetailEntities.add(e);
        }
        cursor.close();
        alarmDetailListAdapter.notifyDataSetChanged();
    }

    private void initListView() {
        alarmDetailList.setAdapter(alarmDetailListAdapter);
    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow, null));
            imgBtnRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh, null));
        } else {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow));
            imgBtnRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_refresh));
        }
        title.setText(alarmCategoryTitle + " 告警详情");
    }

    private void getImperativeData() {
        alarmCategory = getIntent().getStringExtra("ALARM_CATEGORY");
        alarmCategoryTitle = getIntent().getStringExtra("ALARM_CATEGORY_TITLE");
        alarmCenterName = getIntent().getStringExtra("CENTER_NAME");
        alarmCenterId = getIntent().getStringExtra("CENTER_ID");
    }

    private void startAlarmDetailTask() {
        UnCheckAlarmDetailTask task = new UnCheckAlarmDetailTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    class UnCheckAlarmDetailTask extends AsyncTask<Void, Void, UnCheckAlarmDetailResp> {
        private LoadToast loadToast = new LoadToast(AlarmDetailActivity.this);
        private UnCheckAlarmDetailReq req = new UnCheckAlarmDetailReq();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req.item = alarmCategory;
            req.userName = ChinaUnicomApplication.userName;
            req.centerId = alarmCenterId;
            req.userToken = ChinaUnicomApplication.token;
            isAlarmDtailTaskRunning = true;
            Utils.initLoadToast(loadToast);
            loadToast.show();
        }

        @Override
        protected UnCheckAlarmDetailResp doInBackground(Void... voids) {
            UnCheckAlarmDetailResp resp = new Http.Builder().create().getUnCheckAlarmDetail(req);
            return resp;
        }

        @Override
        protected void onPostExecute(UnCheckAlarmDetailResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                if (!Utils.isListEmpty(resp.data.records)) {
                    for (int alarmNum = 0; alarmNum < resp.data.records.size(); alarmNum++) {
                        ContentValues values = new ContentValues();
                        values.put("category",alarmCategoryTitle);
                        values.put("column", alarmCategory);
                        values.put("send_time", resp.data.records.get(alarmNum).sendTime);
                        values.put("json_list", resp.data.records.get(alarmNum).jsonList);
                        values.put("json_map", resp.data.records.get(alarmNum).jsonMap);
                        values.put("center_id", resp.data.centerId);
                        db.insert("ALARM", null, values);
                    }
                }
                loadToast.success();
            } else {
                loadToast.error();
                Utils.showErrorToast(AlarmDetailActivity.this, "未查看告警请求失败");
            }
            //把所有告警统一写入数据库后一起从数据库读出
            getAlarmDtailFromDB();
            isAlarmDtailTaskRunning = false;
        }
    }

    //告警详情的viewholder
    class AlarmDetailViewHolder {

        @Bind(R.id.alarmSendTime)
        TextView alarmSendTime;

        @Bind(R.id.alarmContent)
        TextView alarmContent;

        @Bind(R.id.alarmTitle)
        TextView alarmTitle;

        @Bind(R.id.alarmCenter)
        TextView alarmCenter;

        public AlarmDetailViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class AlarmDetailListAdapter extends BaseAdapter {
        AlarmDetailViewHolder viewHolder;

        @Override
        public int getCount() {
            return alarmDetailEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return alarmDetailEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (null != convertView) {
                viewHolder = (AlarmDetailViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(AlarmDetailActivity.this, R.layout.list_item_alarm_detail, null);
                viewHolder = new AlarmDetailViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            AlarmDetailEntity entity = alarmDetailEntities.get(position);
            viewHolder.alarmSendTime.setText(formatTime(entity.sendTime));

            Map<String, Object> infoMap
                    = (HashMap<String, Object>)jsonToObj(entity.jsonMap, new TypeToken<HashMap<String, Object>>(){}.getType());
            List<String> keyOrder
                    = (ArrayList<String>)jsonToObj(entity.jsonList, new TypeToken<ArrayList<String>>(){}.getType());
            StringBuilder sb = new StringBuilder();
            StringBuilder strAlarmTitle = new StringBuilder();
            int beginIndex = -1;
            int endIndex = -1;
            for (String key : keyOrder) {
                if (infoMap.containsKey(key) && !key.equals("problem")) {
                    sb.append(getItemName(key) + ":  " + infoMap.get(key) + "\n\n");
                    if (key.equals("curValue")) {
                        endIndex = sb.length() - 2;
                        beginIndex = endIndex - (""+infoMap.get(key)).length();
                    }
                } else if (infoMap.containsKey(key) && key.equals("problem")) {
                    strAlarmTitle.append(getItemName(key) + ":  " + infoMap.get(key));
                }
            }
            if (beginIndex != endIndex && beginIndex != -1) {
                Logger.e("INDEX", beginIndex + ":::" + endIndex);
                Spannable s = new SpannableString(sb.substring(0, sb.length() - 2));
                s.setSpan(new ForegroundColorSpan(Color.RED), beginIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //去掉最后两个换行
                viewHolder.alarmContent.setText(s);
            } else {
                viewHolder.alarmContent.setText(sb.substring(0, sb.length() - 2));
            }
            viewHolder.alarmTitle.setText(strAlarmTitle.toString());
            viewHolder.alarmCenter.setText(ChinaUnicomApplication.alarmCurCenter.title);

            return convertView;
        }
    }

    private String formatTime(long seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return " " + sdf.format(seconds) + " ";
    }

    private Object jsonToObj(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }

    private String getItemName(String param) {
        String rtn = "";
        if ("ip_list".equals(param))
            rtn = "IP";
        else if ("host".equals(param))
            rtn = "主机";
        else if ("curValue".equals(param))
            rtn = "当前数量";
        else if ("itemValue".equals(param))
            rtn = "配置数量";
        else if ("problem".equals(param))
            rtn = "故障";
        else if ("serverTypeName".equals(param))
            rtn = "机器类型";
        else if ("appName".equals(param))
            rtn = "部署应用";
        else if ("topic".equals(param))
            rtn = "积压主题及数量";
        else if ("notprocess".equals(param))
            rtn = "未处理消息数量";
        else if ("clusterName".equals(param))
            rtn = "集群";
        else if ("brokerName".equals(param))
            rtn = "消息队列";
        else if ("updateTime".equals(param))
            rtn = "宕机时间";
        else if ("group".equals(param))
            rtn = "订阅组";
        else if ("headCotent".equals(param))
            rtn = "headCotent";
        else if ("DataHouse".equals(param))
            rtn = "DataHouse";
        else
            rtn = param;

        return rtn;

    }
}
