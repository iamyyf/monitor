package cn.chinaunicom.monitor.mine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.BaseActivity;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.ReportEntity;
import cn.chinaunicom.monitor.sqlite.AlarmDatabaseHelper;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Logger;
import cn.chinaunicom.monitor.utils.Utils;

public class ReportActivity extends BaseActivity {

    private AlarmDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String centerName;
    private String centerId;
    private long reportSendTime;

    @Bind(R.id.imgBtnLeft)
    ImageButton backBtn;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @Bind(R.id.reportView)
    WebView reportView;

    @OnClick(R.id.imgBtnLeft)
    void back() {
        ReportActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        initDB();
        getImperativeData();
        initView();
    }

    @Override
    public void initView() {
        initTitleBar();
        String htmlData = getHtmlDataFromDB();

        if (!Utils.isStringEmpty(htmlData)) {
            reportView.getSettings().setUseWideViewPort(true);
            reportView.getSettings().setLoadWithOverviewMode(true);
            reportView.getSettings().setBuiltInZoomControls(true);
            reportView.loadData(htmlData, "text/html; charset=UTF-8", null);
        }
    }

    private String getHtmlDataFromDB() {
        Cursor cursor = db.query("REPORT", Config.REPORT_COLUMN, "center_name=? and center_id=? and send_time=?",
                new String[]{centerName, centerId, ""+reportSendTime}, null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getString(3);
        }
        return "";
    }

    private void initDB() {
        dbHelper = new AlarmDatabaseHelper(this, Config.DB_NAME, null, Config.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow, null));
        } else {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow));
        }
        title.setText("晨检报告详情");
    }

    private void getImperativeData() {
        centerName = getIntent().getStringExtra("CENTER_NAME");
        centerId = getIntent().getStringExtra("CENTER_ID");
        reportSendTime = getIntent().getLongExtra("SEND_TIME", 0);
    }
}
