package cn.chinaunicom.monitor.mainframe;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.BaseActivity;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.adapters.ChartsAdapter;
import cn.chinaunicom.monitor.adapters.MainframeDetailAdapter;
import cn.chinaunicom.monitor.adapters.MainframeDetailChartAdapter;
import cn.chinaunicom.monitor.utils.Const;
import cn.chinaunicom.monitor.utils.Utils;

/**
 * @author yfYang
 * */
public class ChartListActivity extends BaseActivity {

    private String itemName;
    private String param1;
    @Bind(R.id.mqCharList)
    ListView chartList;

    @Bind(R.id.imgBtnLeft)
    ImageButton backBtn;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @OnClick(R.id.imgBtnLeft)
    void back() {
        ChartListActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_list);
        ButterKnife.bind(this);
        initView();
    }
    @Override
    public void initView() {
        getImperativeData();
        initTitleBar();
        if (itemName.equals(Const.MAINFRAME_DETAIL)) {
            chartList.setAdapter(new MainframeDetailChartAdapter(this, "["+param1+"]"));
        } else {
            chartList.setAdapter(new ChartsAdapter(this));
        }
    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow, null));
        } else {
            backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.ic_back_arrow));
        }

        title.setText(itemName);
    }

    private void getImperativeData() {
        itemName = getIntent().getStringExtra("Monitor_Item");
        param1 = getIntent().getStringExtra("PARAM1");
    }

}
